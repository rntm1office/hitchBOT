﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
using hitchbot_secure_api.Models;

namespace hitchbot_secure_api.Controllers
{
    public partial class TabletController : ApiController
    {
        [HttpPost]
        public async Task<IHttpActionResult> LogTabletStatus([FromBody] ReturnTabletStatus context)
        {
            if (!ModelState.IsValid)
                return BadRequest("Model sent was not valid.");

            using (var db = new Dal.DatabaseContext())
            {
                db.TabletStatuses.Add(new TabletStatus(context)
                {
                    BatteryPercentage = context.BatteryPercentage,
                    BatteryTemp = context.BatteryTemp,
                    BatteryVoltage = context.BatteryVoltage,
                    IsCharging = context.IsCharging
                });

                await db.SaveChangesAsync();
            }

            return Ok();
        }
    }
}