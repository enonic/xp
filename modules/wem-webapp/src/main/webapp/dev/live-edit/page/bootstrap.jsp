<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Enonic &middot; CMS</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">

  <link href="css/bootstrap.css" rel="stylesheet">
  <style type="text/css">
    body {
      padding-top: 20px;
      padding-bottom: 60px;
    }

    .container {
      margin: 0 auto;
      max-width: 1000px;
    }

    .container > hr {
      margin: 60px 0;
    }

    .jumbotron {
      margin: 80px 0;
      text-align: center;
    }

    .jumbotron h1 {
      font-size: 100px;
      line-height: 1;
    }

    .jumbotron .lead {
      font-size: 24px;
      line-height: 1.25;
    }

    .jumbotron .btn {
      font-size: 21px;
      padding: 14px 24px;
    }

    .navbar .navbar-inner {
      padding: 0;
    }

    .navbar .nav {
      margin: 0;
      display: table;
      width: 100%;
    }

    .navbar .nav li {
      display: table-cell;
      width: 1%;
      float: none;
    }

    .navbar .nav li a {
      font-weight: bold;
      text-align: center;
      border-left: 1px solid rgba(255, 255, 255, .75);
      border-right: 1px solid rgba(0, 0, 0, .1);
    }

    .navbar .nav li:first-child a {
      border-left: 0;
      border-radius: 3px 0 0 3px;
    }

    .navbar .nav li:last-child a {
      border-right: 0;
      border-radius: 0 3px 3px 0;
    }

      /* CUSTOMIZE THE CAROUSEL
   -------------------------------------------------- */

      /* Carousel base class */
    .carousel {
      margin-bottom: 60px;
    }

    .carousel .container {
      position: relative;
      z-index: 9;
    }

    .carousel-control {
      height: 80px;
      margin-top: 0;
      font-size: 120px;
      text-shadow: 0 1px 1px rgba(0, 0, 0, .4);
      background-color: transparent;
      border: 0;
      z-index: 10;
    }

    .carousel .item {
      height: 500px;
    }

    .carousel img {
      position: absolute;
      top: 0;
      left: 0;
      min-width: 100%;
      height: 500px;
    }

    .carousel-caption {
      background-color: transparent;
      position: static;
      max-width: 550px;
      padding: 0 20px;
      margin-top: 200px;
    }

    .carousel-caption h1,
    .carousel-caption .lead {
      margin: 0;
      line-height: 1.25;
      color: #fff;
      text-shadow: 0 1px 1px rgba(0, 0, 0, .4);
    }

    .carousel-caption .btn {
      margin-top: 10px;
    }
  </style>
  <link href="css/bootstrap-responsive.css" rel="stylesheet">
  <%@ include file="live-edit/css.jsp" %>
</head>

<body data-live-edit-type="page" data-live-edit-key="/path/to/this/page" data-live-edit-name="Bootstrap">
<%@ include file="live-edit/loader-splash.jsp" %>
<script src="js/jquery.js"></script>

<div class="container">

  <div class="masthead">
    <h3 class="muted">Enonic CMS</h3>

    <div class="navbar">
      <div class="navbar-inner">
        <div class="container">
          <ul class="nav">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#">Community</a></li>
            <li><a href="#">Try now</a></li>
            <li><a href="#">Documentation</a></li>
            <li><a href="#">Support</a></li>
            <li><a href="#">Contact us</a></li>
          </ul>
        </div>
      </div>
    </div>
  </div>
  <div id="north" data-live-edit-type="region" data-live-edit-key="70" data-live-edit-name="North">

  </div>

  <hr>

  <div id="center" data-live-edit-type="region" data-live-edit-key="80" data-live-edit-name="Center">
    <div class="row-fluid" data-live-edit-type="layout" data-live-edit-key="010101" data-live-edit-name="Triboxes">

      <div class="span4" data-live-edit-type="column">
        <div data-live-edit-type="part" data-live-edit-key="010201" data-live-edit-name="OpenSource">
          <h2>Open source</h2>

          <p>Enonic CMS Community Edition is based on Java and open for developers at github.com.</p>

          <p><a class="btn" href="#">Download now &raquo;</a></p>
        </div>
      </div>

      <div class="span4" data-live-edit-type="column">
        <div data-live-edit-type="part" data-live-edit-key="010201" data-live-edit-name="Webagility">
          <h2>Webagility</h2>

          <p>Outperform your competition and adapt change! Enonic CMS unleashes your agility.</p>

          <p><a class="btn" href="#">Experience Enonic CMS &raquo;</a></p>
        </div>
      </div>

      <div class="span4" data-live-edit-type="column">
        <div class="live-edit-empty-column-placeholder">
          Drag parts here
        </div>
      </div>

    </div>
  </div>

  <hr>

  <div id="south" data-live-edit-type="region" data-live-edit-key="90" data-live-edit-name="South">
    <div data-live-edit-type="part" data-live-edit-key="10013" data-live-edit-name="Sub Menu">
      <ul class="nav nav-list">
        <li class="nav-header">Home</li>
        <li><a href="#">Enonic CMS</a></li>
        <li><a href="#">Pricing</a></li>
        <li><a href="#">Training</a></li>
        <li><a href="#">Service and support</a></li>
        <li><a href="#">Customers</a></li>
        <li><a href="#">Company</a></li>
        <li><a href="#">Partners</a></li>
      </ul>
    </div>

  </div>

  <hr>

  <div class="footer">
    <p>&copy; Enonic 2013</p>
  </div>

</div>
<script src="js/bootstrap.js"></script>

<%@ include file="live-edit/scripts.jsp" %>


</body>
</html>
