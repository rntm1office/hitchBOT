﻿<%@ Page Title="" Language="C#" MasterPageFile="~/Access/Shared/PageWithHeader.master" AutoEventWireup="true" CodeBehind="AddTargetLocationPolygon.aspx.cs" Inherits="hitchbot_secure_api.Access.AddTargetLocationPolygon" %>

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

        .wiki-form {
            font-size: 16px;
        }

        .help-block2 {
            font-size: 14px;
        }

        #inputRaduisValue, #inputRadius {
            max-width: 50px;
        }

        .radius-select {
            /*width: 100px;*/
        }

        .fake-link {
            cursor: pointer;
        }

        .geo-input {
            /*max-width: 75px;*/
        }

        .geo-checkbox {
            padding-top: 22px;
        }

        .wiki-lines-detect {
            color: #737373;
        }

        .disabled-location {
            background-color: #eee;
        }
    </style>
    <script type="text/javascript"
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCV-d9jbUEWesRS6LRsWCWZpKZdOmXCUWA">
    </script>
    <script type="text/javascript">
        var markerCircle;
        var markerCircleRadius;
        var marker;
        var map;
        var polygon;
        var verticies = [];
        //updates the displayed coords to work with 
        function UpdateCoordsOnPage(latlng) {

            markerCircle.setCenter(latlng);
            $(".latValue").val(latlng.lat());
            $(".lngValue").val(latlng.lng());
        }

        function UpdateCoordsOnMap() {

            var lat = parseFloat($(".latValue").val());
            var lng = parseFloat($(".lngValue").val());
            var latlng = new google.maps.LatLng(lat, lng);
            markerCircle.setCenter(latlng);
            marker.setPosition(latlng);
        }

        function placeMarker(location) {

            var coordString = location.lat() + ', ' + location.lng();
            console.log(coordString);

            verticies.push(location);

            //send a string back to the server via a hidden field .. yes there is probably a better way to do this.
            var locations = verticies.map(function(entry) {
                return entry.lat() + ":" + entry.lng();
            }).join();

            document.getElementById("<%=locationArray.ClientID %>").value = locations;

            if (polygon)
                polygon.setMap(null);
            var marker = new google.maps.Marker({
                position: location,
                map: map
            });

            if (verticies.length > 2) {
                polygon = new google.maps.Polygon({
                    paths: verticies,
                    strokeColor: '#FF0000',
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: '#FF0000',
                    fillOpacity: 0.35
                });

                polygon.setMap(map);
            }
        }

        function initialize() {

            var myLatlng = new google.maps.LatLng(39.011902, -98.484246);

            var mapOptions = {
                center: myLatlng,
                zoom: 4
            };

            map = new google.maps.Map(document.getElementById('map-canvas'),
                mapOptions);

            //marker = new google.maps.Marker({
            //    position: myLatlng,
            //    map: map,
            //    draggable: true,
            //    title: "Drag me!"
            //});

            //google.maps.event.addListener(marker, 'drag', function () {
            //    UpdateCoordsOnPage(marker.getPosition());
            //});

            google.maps.event.addListener(map, 'click', function (event) {
                placeMarker(event.latLng);
            });

        }
        google.maps.event.addDomListener(window, 'load', initialize);
    </script>
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="mainContent" runat="server">
    <div class="container">
        <div class="jumbotron">
            <h2>Add Cleverscript Entries (Polygon)</h2>
            <div class="map-wrapper">
                <div id="map-canvas">
                </div>
            </div>
            <br />
            <form class="wiki-form" runat="server">

                <div id="errorAlert" class="alert alert-danger hidden" role="alert" runat="server">Uh oh!</div>

                <%-- This code was borrowed from http://www.bootply.com/katie/9CvIygzob8 --%>
                <div class="row">
                    <div class="col-md-12">
                        <p class="help-block help-block2">
                            <strong>Note: </strong>Due to the roundness of the earth and map projections,
                        <br />
                            ensure the selected area is larger than the intended area.
                        </p>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <div class="checkbox geo-checkbox">
                                <label>
                                    <input id="bucketCheckBox" type="checkbox" class="BucketCheckBox" runat="server" />
                                    Bucket List Item
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <asp:HiddenField ID="locationArray" runat="server" />
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="inputName">Name</label>
                            <input type="text" class="form-control" id="inputName" placeholder="Enter Name of Cleverscript Entry" runat="server"></input>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="inputRadius">Select a Label</label>

                            <div class="input-group radius-select">
                                <input id="inputTag" type="text" class="form-control input-tag" aria-label="..." runat="server" disabled="True" />
                                <div class="input-group-btn" id="inputLabel">
                                    <asp:HiddenField ID="selectedLabelID" runat="server" />
                                    <a class="btn btn-default dropdown-toggle btn-select2 fake-link select-button-radius" data-toggle="dropdown">Select Label<span class="caret"></span></a>
                                    <ul class="dropdown-menu">
                                        <asp:Literal ID="labelLiterals" runat="server"></asp:Literal>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label for="exampleInputPassword1">Cleverscript Entries (One Per Line) <span class="wiki-lines-detect"></span></label>
                    <textarea class="form-control wiki-entries" id="inputWiki1" placeholder="Cleverscript Entries" rows="5" runat="server"></textarea>
                </div>
                <div class="form-group">
                    <asp:Button ID="buttonSubmit" runat="server" Text="Submit" class="btn btn-success" OnClick="buttonSubmit_Click" />
                </div>


            </form>
        </div>
    </div>
</asp:Content>
<asp:Content ID="Content3" ContentPlaceHolderID="endScripts" runat="server">
    <script>
        <%-- Updates the radius value when the dropdown is used --%>
        $(".dropdown-menu li .radius-option").click(function () {
            var selText = $(this).text();
            $(this).parents('.input-group-btn').find('.dropdown-toggle').html(selText + ' <span class="caret"></span>');
            markerCircleRadius = parseInt(selText.split(" ")[0]);

            markerCircle.setRadius(markerCircleRadius * 1000);

            var hiddenRadiusVal = $('.inputRadiusValue');
            hiddenRadiusVal.val(markerCircleRadius);
        });

                <%-- Updates the radius value when the dropdown is used --%>
        $(".dropdown-menu li .label-option").click(function () {
            var selText = $(this).text();
            $(this).parents('.input-group-btn').find('.dropdown-toggle').html(selText + ' <span class="caret"></span>');


            $('#<% =selectedLabelID.ClientID %>').val($(this).attr('value'));

            $('#<% =inputTag.ClientID %>').val(selText);
        });


        <%-- Updates the radius value as seen on the map when the value is changed in the input box --%>
        $(".inputRadiusValue").bind("change paste keyup", function () {

            var value = parseInt($(this).val());
            markerCircle.setRadius(value * 1000);

            $('.input-group-btn').find('.dropdown-toggle').html(value.toString() + ' km<span class="caret"></span>');
        });

        $("#btnSearch").click(function () {
            alert($('.btn-select').text() + ", " + $('.btn-select2').text());
        });

        $(".latValue").bind("change paste keyup", UpdateCoordsOnMap);
        $(".lngValue").bind("change paste keyup", UpdateCoordsOnMap);

        $(".LocationCheckBox").change(function () {

            if ($(this).prop('checked')) {
                $('.inputRadiusValue').prop('disabled', false);
                $('.latValue').prop('disabled', false);
                $('.lngValue').prop('disabled', false);
                $('.select-button-radius').prop('disabled', false);
                $('.select-button-radius').removeClass('disabled-location');
                marker.setDraggable(true);

            }
            else {
                $('.inputRadiusValue').prop('disabled', true);
                $('.latValue').prop('disabled', true);
                $('.lngValue').prop('disabled', true);
                $('.select-button-radius').addClass('disabled-location');
                $('.select-button-radius').prop('disabled', true);
                marker.setDraggable(false);
            }
        });

        $(".wiki-entries").bind("change paste keyup", function () {
            var text = $(this).val();
            var numLines = text.split("\n").length;
            var outputText = "";
            if (text.length == 0) {
                outputText = "No ";
            }
            else {
                outputText = numLines.toString() + " ";
            }

            outputText += " line(s) are detected.";

            $(".wiki-lines-detect").text(outputText);
        });

    </script>
</asp:Content>
