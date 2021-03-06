﻿<%@ Page Title="" Language="C#" MasterPageFile="~/Access/Shared/PageWithHeader.master" AutoEventWireup="true" CodeBehind="PreviewMapCoverage.aspx.cs" Inherits="hitchbot_secure_api.Access.PreviewMapCoverage" %>

<asp:Content ID="Content1" ContentPlaceHolderID="StyleContent" runat="server">
    <style type="text/css">
        html, body, #map-canvas {
            height: 100%;
            margin: 0;
            padding: 0;
        }

        .map-wrapper {
            height: 500px;
        }
    </style>
    <script type="text/javascript"
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCV-d9jbUEWesRS6LRsWCWZpKZdOmXCUWA">
    </script>
    <asp:Literal ID="coordsOutput" runat="server">

    </asp:Literal>
    <script type="text/javascript">

        var map;
        var hitchbotId = <%=HitchBotId%>;
        var currentHbPosition = new google.maps.LatLng(<%=lat%>, <%=lng%>);
        var marker;

        function initialize() {
            var myLatlng = new google.maps.LatLng(39.011902, -98.484246);

            var mapOptions = {
                center: myLatlng,
                zoom: 4
            };

            map = new google.maps.Map(document.getElementById('map-canvas'),
                mapOptions);

            var infowindow = new google.maps.InfoWindow({
                content: "Nothing here!"
            });

            for (i = 0; i < coords.length; i++) {

                var tempMarker = new google.maps.Marker({
                    position: coords[i].coord,
                    map: map,
                    draggable: false
                });

                var circleOptions = {
                    strokeColor: '#0000FF',
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: '#0000FF',
                    fillOpacity: 0.35,
                    map: map,
                    center: coords[i].coord,
                    radius: coords[i].radius * 1000
                };

                if (coords[i].bucketList) {
                    circleOptions.fillColor = '#4CAF50';
                    circleOptions.strokeColor = '#4CAF50';
                }

                tempMarker.html = getFormattedContent(coords[i]);

                google.maps.event.addListener(tempMarker, 'click', function () {
                    infowindow.setContent(this.html);
                    infowindow.open(map, this);
                });

                var TempCircle = new google.maps.Circle(circleOptions);
            }

            for (i = 0; i < polys.length; i++) {
                var coord = polys[i];

                var poly = new google.maps.Polygon({
                    paths: coord.coord,
                    strokeColor: '#FF0000',
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: '#FF0000',
                    fillOpacity: 0.35
                });

                if (coord.bucketList) {
                    poly.fillColor = '#4CAF50';
                    poly.strokeColor = '#4CAF50';
                }


                poly.html = getFormattedContent(coord);

                poly.setMap(map);

                google.maps.event.addListener(poly, 'click', function () {
                    infowindow.setContent(this.html);
                    infowindow.setPosition(this.getPath().getArray()[0]);
                    infowindow.open(map, this);
                });
            }
            
            //draw the hb mark on the map. Should end up where hitchbot is sitting last.
            var t =new google.maps.MarkerImage("http://hitchbotimg.blob.core.windows.net/img/hitchicon2.png",
		        new google.maps.Size(64,64),
		        new google.maps.Point(0,0),
		        new google.maps.Point(32,64));

            marker = new google.maps.Marker({
                position: currentHbPosition,
                map: map,
                animation:google.maps.Animation.DROP,
                draggable: true,
                icon: t
            });

            google.maps.event.addListener(marker, 'dragend', function(e) {
                var result = getPolyResult(e.latLng) + getCircleResult(e.latLng);
                if (!result)
                    result = getFormattedList({title: 'hitchBOT doesn\'t say anything here!', content: 'Maybe you should add something!!'});
                $("#content").html(result);
                var coordString = e.latLng.lat() + ', ' + e.latLng.lng();
                console.log(coordString);
                $('#current-coords').html('Location: ' + coordString);
            } );
        }
        google.maps.event.addDomListener(window, 'load', initialize);
    </script>
    <%--The following was referenced from http://stackoverflow.com/questions/4463907/how-do-i-know-if-a-lat-lng-point-is-contained-within-a-circle --%>
    <script type="text/javascript">
        
        function getPolyResult(latlng) {
            var result = "";

            for (var i = 0; i < polys.length; i++) {

                coord = polys[i];

                var poly = new google.maps.Polygon({
                    paths: coord.coord,
                    strokeColor: '#FF0000',
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: '#FF0000',
                    fillOpacity: 0.35
                });

                if (google.maps.geometry.poly.containsLocation(latlng, poly)) {
                    result += getFormattedList(coord);
                }

            }
            return result;
        }

        function getCircleResult(latlng) {
            var result = "";
            for (var i = 0; i < coords.length; i++) {
                var coord = coords[i];

                if (circleInRadius(latlng, coord.coord, coord.radius)) {
                    result += getFormattedList(coord);
                }
            }
            return result;
        }
        
        function circleInRadius(base, latlng, radius) {

            return google.maps.geometry.spherical.computeDistanceBetween(base, latlng) <= radius *1000;
        }

        function getFormattedList(coord){

            var title = coord.title;
            if (coord.bucketList) {
                title += ' bucket list';
            }

            return '<li class="list-group-item">'+
                '<h4 class="list-group-item-heading">' + title +
                '</h4>' +
                '<p class="list-group-item-text">'+ coord.content +
                '</p></li>';
        }

        function getFormattedContent(coord) {

            var title = coord.title;
            if (coord.bucketList) {
                title += ' bucket list';
            }
            return '<b>' + title + '</b>' + '<p>' + coord.content + '</p>';
        }
    </script>
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="mainContent" runat="server">
    <form runat="server">
        <asp:ScriptManager runat="server"></asp:ScriptManager>
        <div class="container">
            <div class="jumbotron">
                <h2>Cleverscript Entries</h2>
                <div class="map-wrapper">
                    <div id="map-canvas">
                    </div>
                </div>
            </div>

        </div>
        <div class="container">
            <div class="panel-default panel">
                <div class="panel-heading">
                    <h3>What will hitchBOT say?</h3>
                    <h5 id="current-coords"></h5>
                </div>
                <ul class="list-group" id="content">
                    <li class="list-group-item">
                        <h4 class="list-group-item-heading">Move the hB marker to see what hitchBOT says in that location.
                        </h4>
                        <p class="list-group-item-text">
                            Pretty cool eh?
                        </p>
                    </li>
                </ul>
            </div>
        </div>
    </form>
</asp:Content>
<asp:Content ID="Content3" ContentPlaceHolderID="endScripts" runat="server">
</asp:Content>
