﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Data.Entity;

namespace hitchbotAPI.ApproveImagesPages
{
    public partial class AddTargetLocations : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (Session["New"] != null)
            {

            }
            else
            {
                Response.Redirect("Unauthorized.aspx");
            }
        }

        protected void buttonSubmit_Click(object sender, EventArgs e)
        {
            try
            {
                var wikiEntry = inputWiki1.InnerText;
                var radius = inputRadiusValue.Value;
                var name = inputName.Value;
                var lat = inputLat.Value;
                var lng = inputLong.Value;

                double? radiusActual = null;
                double latActual;
                double lngActual;

                using (var db = new Models.Database())
                {
                    var user = (Models.Password)Session["New"];
                    var hitchbot = db.hitchBOTs.Include(l => l.WikipediaEntries).First(l => l.ID == user.hitchBOT.ID);

                    Models.Location location = null;

                    if (LocationCheckBox.Checked)
                    {
                        /*
                        nullable double parse code borrowed from
                        http://stackoverflow.com/questions/3390750/how-to-use-int-tryparse-with-nullable-int
                        */
                        double tmp;

                        if (!double.TryParse(radius, out tmp))
                        {
                            setErrorMessage("Selected Radius is not valid!");
                            return;
                        }
                        radiusActual = tmp;

                        if (!double.TryParse(lat, out latActual))
                        {
                            setErrorMessage("Latitude is not valid number!");
                            return;
                        }

                        if (!double.TryParse(lng, out lngActual))
                        {
                            setErrorMessage("Longitude is not valid number!");
                            return;
                        }

                        location = new Models.Location
                        {
                            Latitude = latActual,
                            Longitude = lngActual,
                            TimeAdded = DateTime.UtcNow,
                            TakenTime = DateTime.UtcNow
                        };

                        db.Locations.Add(location);
                        db.SaveChanges();

                    }

                    var wiki = new Models.WikipediaEntry
                    {
                        TargetLocation = location,
                        WikipediaText = wikiEntry,
                        EntryName = name,
                        RadiusKM = radiusActual,
                        HitchBot = hitchbot,
                        TimeAdded = DateTime.UtcNow
                    };

                    db.WikipediaEntries.Add(wiki);

                    db.SaveChanges();
                }

                Response.Redirect("AddTargetSuccess.aspx");
            }
            catch
            {
                setErrorMessage("An unknown error occurred. let the sys admin know you saw this message.");
            }
        }

        protected void setErrorMessage(string error)
        {
            this.errorAlert.Attributes.Remove("class");
            this.errorAlert.Attributes.Add("class", "alert alert-danger");
            this.errorAlert.InnerText = error;
        }
    }
}