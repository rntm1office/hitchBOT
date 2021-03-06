﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace hitchbot_secure_api.Models
{
    public class CleverscriptContent
    {
        public int Id { get; set; }

        public int? LocationId { get; set; }
        public virtual Location Location { get; set; }
        public int? CleverscriptContextId { get; set; }
        public virtual CleverscriptContext CleverscriptContext { get; set; }
        public int HitchBotId { get; set; }
        public virtual HitchBot HitchBot { get; set; }

        public string CleverText { get; set; }
        public string VisitedCleverText { get; set; }
        public string EntryName { get; set; }
        public double? RadiusKm { get; set; }

        //mark this entry as something on hitchbots bucketlist.
        public bool isBucketList { get; set; }
        //mark this with a timestamp when hitchbot has visited this location, marking it off of the bucket list
        public DateTime? TimeVisited { get; set; }

        public virtual ICollection<PolgonVertex> PolgonVertices { get; set; }

        public DateTime TimeAdded { get; set; }
    }

    public class PolgonVertex
    {
        public int Id { get; set; }
        public int? LocationId { get; set; }
        public virtual Location Location { get; set; }

        public int? CleverscriptContentId { get; set; }
        public virtual CleverscriptContent CleverscriptContent { get; set; }
    }

    public class CleverscriptContext
    {
        public int Id { get; set; }

        public string BaseLabel { get; set; }
        public string HumanReadableBaseLabel { get; set; }

        public int? HitchBotId { get; set; }
        public virtual HitchBot HitchBot { get; set; }

        public ICollection<CleverscriptContent> CleverscriptContents { get; set; }

        public DateTime TimeAdded { get; set; }
    }
}
