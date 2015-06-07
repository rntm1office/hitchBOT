﻿using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Data.Entity.Core.Metadata.Edm;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Management;
using Newtonsoft.Json;
using hitchbot_secure_api.Dal;
using hitchbot_secure_api.Helpers;
using hitchbot_secure_api.Models;
using System.Configuration;

namespace hitchbot_secure_api.Controllers
{
    public class HitchBotController : ApiController
    {
        [HttpGet]
        public string Test(string repeat)
        {

            return repeat;
        }

        [HttpPost]
        public async Task<IHttpActionResult> UpdateGpsFromSpotFeed(int HitchBotId)
        {
            using (var db = new DatabaseContext())
            {
                var spotCurrentIdsTask =
                    db.Locations.Where(l => l.LocationProvider == LocationProvider.SpotGPS && l.HitchBotId == HitchBotId)
                        .Select(l => new { l.SpotID })
                        .ToListAsync();

                var feed_id = ConfigurationManager.AppSettings["spot-feed-id"];
                var url = string.Format(
                        "https://api.findmespot.com/spot-main-web/consumer/rest-api/2.0/public/feed/{0}/message.json",
                        feed_id);

                var spotty = WebApiHelper._download_serialized_json_data<SpotApiCall>(url);

                var previousIds = await spotCurrentIdsTask;

                var messages = spotty.response.feedMessageResponse.messages.message
                    .Where(l => l.messageType == "EXTREME-TRACK" || l.messageType == "NEWMOVEMENT" || l.messageType== "STOP")
                    .Where(l => previousIds.All(h => h.SpotID != l.id))
                    .Select(l => new LocationController.SpotDto
                    {
                        latitude = l.latitude,
                        longitude = l.longitude,
                        spotId = l.id,
                        unixTime = l.unixTime
                    }).ToList();

                messages.ForEach(l =>
                {
                    db.Locations.Add(new Location()
                    {
                        Latitude = l.latitude,
                        Longitude = l.longitude,
                        TakenTime = l.Time,
                        LocationProvider = LocationProvider.SpotGPS,
                        HitchBotId = HitchBotId,
                        SpotID = l.spotId
                    });
                });

                await db.SaveChangesAsync();

                return Ok();
            }
        }

        public async Task<IHttpActionResult> UpdateCleverscriptVariables(int HitchBotId)
        {
            using (var db = new DatabaseContext())
            {
                var location = await db.Locations.Where(l => l.HitchBotId == HitchBotId).OrderByDescending(l => l.TakenTime).FirstAsync();
                var weatherApi = new WeatherHelper.OpenWeatherApi();

                weatherApi.LoadWeatherData(location.Latitude, location.Longitude);

                var contextpacket = new ContextPacket()
                {
                    HitchBotId = HitchBotId,
                    Variables = new List<VariableValuePair>()
                };

                contextpacket.Variables.Add(weatherApi.GetCityNamePair());
                contextpacket.Variables.Add(weatherApi.GetTempCPair());
                contextpacket.Variables.Add(weatherApi.GetTempTextCPair());
                contextpacket.Variables.Add(weatherApi.GetWeatherStatusPair());

                db.ContextPackets.Add(contextpacket);

                await db.SaveChangesAsync();

                return Ok("Variables were updated successfully");
            }
        }

        [HttpGet]
        public async Task<IHttpActionResult> GetCleverscriptContext(int? HitchBotId)
        {
            if (!HitchBotId.HasValue)
            {
                var result = new
                {
                    data = new List<VariableValuePair>
                    {
                        new VariableValuePair
                        {
                            key = "current_city_name",
                            value = "Hamilton"
                        }
                    }
                };

                return Ok(result);
            }

            using (var db = new DatabaseContext())
            {

                var packets =
                    db.ContextPackets.Where(l => l.HitchBotId == HitchBotId)
                        .OrderByDescending(l => l.TimeCreated)
                        .FirstOrDefault();

                if (packets == null)
                {
                    BadRequest(string.Format("No cleverscript variables found for hitchBOT with ID: {0}.", HitchBotId));
                }

                var variables = db.VariableValuePairs.Where(l => l.ContextPacketId == packets.Id);

                if (!variables.Any())
                    BadRequest(string.Format("No cleverscript variables found for hitchBOT with ID: {0}.", HitchBotId));

                var result = new
                {
                    data = variables.ToList()
                };

                return Ok(result);
            }
        }

        [HttpPost]
        public async Task<IHttpActionResult> AddPairToHb(int HitchBotId, string key, string value)
        {
            if (string.IsNullOrWhiteSpace(key) || string.IsNullOrWhiteSpace(value))
                return BadRequest("One or more of the parameters was null or whitespace.");

            using (var db = new DatabaseContext())
            {
                var packets =
                    await db.ContextPackets.Where(l => l.HitchBotId == HitchBotId)
                        .OrderByDescending(l => l.TimeCreated)
                        .FirstOrDefaultAsync();

                if (packets == null)
                {
                    BadRequest(string.Format("No cleverscript variables found for hitchBOT with ID: {0}.", HitchBotId));
                }

                db.VariableValuePairs.Add(new VariableValuePair()
                {
                    key = key,
                    value = value,
                    ContextPacketId = packets.Id
                });

                await db.SaveChangesAsync();
            }
            return Ok();
        }
    }
}
