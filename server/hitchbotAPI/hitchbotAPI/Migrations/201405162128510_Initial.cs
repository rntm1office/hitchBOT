namespace hitchbotAPI.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class Initial : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.Conversations",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        StartTime = c.DateTime(nullable: false),
                        EndTime = c.DateTime(),
                        TimeAdded = c.DateTime(nullable: false),
                        StartLocation_ID = c.Int(),
                        hitchBOT_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Locations", t => t.StartLocation_ID)
                .ForeignKey("dbo.hitchBOTs", t => t.hitchBOT_ID)
                .Index(t => t.StartLocation_ID)
                .Index(t => t.hitchBOT_ID);
            
            CreateTable(
                "dbo.ListenEvents",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        SpeechHeard = c.String(),
                        HeardTime = c.DateTime(nullable: false),
                        TimeAdded = c.DateTime(nullable: false),
                        Conversation_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Conversations", t => t.Conversation_ID)
                .Index(t => t.Conversation_ID);
            
            CreateTable(
                "dbo.SpeechEvents",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        SpeechSaid = c.String(),
                        OccuredTime = c.DateTime(nullable: false),
                        TimeAdded = c.DateTime(nullable: false),
                        Conversation_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Conversations", t => t.Conversation_ID)
                .Index(t => t.Conversation_ID);
            
            CreateTable(
                "dbo.Locations",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        NearestCity = c.String(),
                        Latitude = c.Double(nullable: false),
                        Longitude = c.Double(nullable: false),
                        Altitude = c.Double(),
                        Accuracy = c.Single(),
                        Velocity = c.Single(),
                        TakenTime = c.DateTime(nullable: false),
                        TimeAdded = c.DateTime(nullable: false),
                        hitchBOT_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.hitchBOTs", t => t.hitchBOT_ID)
                .Index(t => t.hitchBOT_ID);
            
            CreateTable(
                "dbo.hitchBOTs",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        Name = c.String(),
                        CreationTime = c.DateTime(nullable: false),
                        TimeAdded = c.DateTime(nullable: false),
                        Project_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Projects", t => t.Project_ID)
                .Index(t => t.Project_ID);
            
            CreateTable(
                "dbo.Images",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        url = c.String(),
                        TimeTaken = c.DateTime(nullable: false),
                        TimeAdded = c.DateTime(nullable: false),
                        location_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Locations", t => t.location_ID)
                .Index(t => t.location_ID);
            
            CreateTable(
                "dbo.Projects",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        Name = c.String(),
                        StartTime = c.DateTime(nullable: false),
                        EndTime = c.DateTime(),
                        Description = c.String(),
                        TimeAdded = c.DateTime(nullable: false),
                        EndLocation_ID = c.Int(),
                        StartLocation_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Locations", t => t.EndLocation_ID)
                .ForeignKey("dbo.Locations", t => t.StartLocation_ID)
                .Index(t => t.EndLocation_ID)
                .Index(t => t.StartLocation_ID);
            
            CreateTable(
                "dbo.TwitterAccounts",
                c => new
                    {
                        UserID = c.String(nullable: false, maxLength: 128),
                        consumerKey = c.String(),
                        consumerSecret = c.String(),
                        accessToken = c.String(),
                        accessTokenSecret = c.String(),
                        TimeAdded = c.DateTime(nullable: false),
                        HitchBot_ID = c.Int(),
                    })
                .PrimaryKey(t => t.UserID)
                .ForeignKey("dbo.hitchBOTs", t => t.HitchBot_ID)
                .Index(t => t.HitchBot_ID);
            
            CreateTable(
                "dbo.TwitterStatus",
                c => new
                    {
                        TweetID = c.String(nullable: false, maxLength: 128),
                        Text = c.String(maxLength: 140),
                        TimeAdded = c.DateTime(nullable: false),
                        TimeTweeted = c.DateTime(nullable: false),
                        TwitterAccount_UserID = c.String(maxLength: 128),
                    })
                .PrimaryKey(t => t.TweetID)
                .ForeignKey("dbo.TwitterAccounts", t => t.TwitterAccount_UserID)
                .Index(t => t.TwitterAccount_UserID);
            
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.TwitterStatus", "TwitterAccount_UserID", "dbo.TwitterAccounts");
            DropForeignKey("dbo.TwitterAccounts", "HitchBot_ID", "dbo.hitchBOTs");
            DropForeignKey("dbo.Projects", "StartLocation_ID", "dbo.Locations");
            DropForeignKey("dbo.hitchBOTs", "Project_ID", "dbo.Projects");
            DropForeignKey("dbo.Projects", "EndLocation_ID", "dbo.Locations");
            DropForeignKey("dbo.Images", "location_ID", "dbo.Locations");
            DropForeignKey("dbo.Locations", "hitchBOT_ID", "dbo.hitchBOTs");
            DropForeignKey("dbo.Conversations", "hitchBOT_ID", "dbo.hitchBOTs");
            DropForeignKey("dbo.Conversations", "StartLocation_ID", "dbo.Locations");
            DropForeignKey("dbo.SpeechEvents", "Conversation_ID", "dbo.Conversations");
            DropForeignKey("dbo.ListenEvents", "Conversation_ID", "dbo.Conversations");
            DropIndex("dbo.TwitterStatus", new[] { "TwitterAccount_UserID" });
            DropIndex("dbo.TwitterAccounts", new[] { "HitchBot_ID" });
            DropIndex("dbo.Projects", new[] { "StartLocation_ID" });
            DropIndex("dbo.Projects", new[] { "EndLocation_ID" });
            DropIndex("dbo.Images", new[] { "location_ID" });
            DropIndex("dbo.hitchBOTs", new[] { "Project_ID" });
            DropIndex("dbo.Locations", new[] { "hitchBOT_ID" });
            DropIndex("dbo.SpeechEvents", new[] { "Conversation_ID" });
            DropIndex("dbo.ListenEvents", new[] { "Conversation_ID" });
            DropIndex("dbo.Conversations", new[] { "hitchBOT_ID" });
            DropIndex("dbo.Conversations", new[] { "StartLocation_ID" });
            DropTable("dbo.TwitterStatus");
            DropTable("dbo.TwitterAccounts");
            DropTable("dbo.Projects");
            DropTable("dbo.Images");
            DropTable("dbo.hitchBOTs");
            DropTable("dbo.Locations");
            DropTable("dbo.SpeechEvents");
            DropTable("dbo.ListenEvents");
            DropTable("dbo.Conversations");
        }
    }
}