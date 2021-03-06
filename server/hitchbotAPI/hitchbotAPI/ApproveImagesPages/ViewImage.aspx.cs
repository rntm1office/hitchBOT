﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Data.Entity;
using System.Globalization;

namespace hitchbotAPI
{
    public partial class ViewImage : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (Session["New"] != null)
            {
                var user = (Models.Password)Session["New"];
                using (var db = new Models.Database())
                {
                    int hitchBOTid = user.hitchBOT.ID;
                    var img = db.Images.Include(i => i.HitchBOT).Where(i => i.HitchBOT.ID == hitchBOTid && (i.TimeApproved == null || i.TimeDenied == null)).OrderBy(i => i.TimeTaken);
                    if (string.IsNullOrEmpty(this.imagePreview.ImageUrl))
                    {
                        this.imagePreview.ImageUrl = "http://imgur.com/" + img.FirstOrDefault().url + ".jpg";
                        this.imgurLink.NavigateUrl = "http://imgur.com/" + img.FirstOrDefault().url;
                    }
                }
            }
            else
            {
                Response.Redirect("Unauthorized.aspx");
            }
        }

        protected void Approve_Click(object sender, EventArgs e)
        {
            GetLiveDateTime();
        }

        protected void Deny_Click(object sender, EventArgs e)
        {

        }

        protected DateTime GetLiveDateTime()
        {
            try
            {
                DateTime day = datePicker.SelectedDate;
                if (day.Year == 1)
                {
                    lblError.Text = "No date Selected!";
                    return DateTime.UtcNow;
                }
                DateTime time = DateTime.ParseExact(time24hrBOX.Text, "HH:mm:ss", CultureInfo.InvariantCulture);
                DateTime total = day.Add(new TimeSpan(time.Hour, time.Minute, time.Second));
                selectedTimePreview.Text = total.ToString("dddd MMMM d h:mm:ss tt K", CultureInfo.InvariantCulture);
                return time;
            }
            catch (FormatException ex)
            {
                lblError.Text = "Time format incorrect!";
                selectedTimePreview.Text = DateTime.UtcNow.ToString("dddd MMMM d h:mm:ss tt K", CultureInfo.InvariantCulture);
            }
            return DateTime.UtcNow;
        }

        protected void time24hrBOX_TextChanged(object sender, EventArgs e)
        {
            GetLiveDateTime();
        }

        protected void datePicker_SelectionChanged(object sender, EventArgs e)
        {
            GetLiveDateTime();
        }
    }
}