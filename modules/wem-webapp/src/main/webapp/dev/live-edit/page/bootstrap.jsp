<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Enonic &middot; CMS</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">

  <link href="css/bootstrap.css" rel="stylesheet">
  <link href="css/main.css" rel="stylesheet">

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

      <div class="span4" data-live-edit-type="region">
        <div data-live-edit-type="part" data-live-edit-key="010201" data-live-edit-name="OpenSource">
          <img src="img/geek.png" class="img-circle"/>

          <h2>Open source</h2>

          <p>Enonic CMS Community Edition is based on Java and open for developers at github.com.</p>

          <p><a class="btn" href="#">Download now &raquo;</a></p>
        </div>
      </div>

      <div class="span4" data-live-edit-type="region">
        <div data-live-edit-type="part" data-live-edit-key="010201" data-live-edit-name="Webagility">
          <img src="img/webagil.png" class="img-circle"/>

          <h2>Webagility</h2>

          <p>Outperform your competition and adapt change! Enonic CMS unleashes your agility.</p>

          <p><a class="btn" href="#">Experience Enonic CMS &raquo;</a></p>
        </div>
      </div>

      <div class="span4" data-live-edit-type="region">


      </div>

    </div>
  </div>

  <hr>

  <div id="south" data-live-edit-type="region" data-live-edit-key="90" data-live-edit-name="South">


  </div>

  <hr>

  <div class="footer">
    <p>&copy; Enonic 2013</p>
  </div>

</div>
<script src="js/bootstrap.js"></script>

<%@ include file="live-edit/scripts.jsp" %>

<%@ include file="live-edit/admin-links.jsp" %>

</body>
</html>
