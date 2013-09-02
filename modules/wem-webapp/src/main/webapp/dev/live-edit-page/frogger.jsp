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

<body data-live-edit-type="0" data-live-edit-key="/path/to/this/page" data-live-edit-name="Jumping Jack - Frogger">
<%@ include file="live-edit/loader-splash.jsp" %>
<script src="js/jquery.js"></script>

<div class="container">

  <div class="masthead">

    <h3 class="muted">Bluman Trampoliner</h3>

    <div class="navbar">
      <div class="navbar-inner">
        <div class="container">
          <ul class="nav">
            <li><a href="#">Forside</a></li>
            <li class="active"><a href="#">Trampoline</a></li>
            <li><a href="#">Kjøpeguide</a></li>
            <li><a href="#">Kundeservice</a></li>
          </ul>
        </div>
      </div>
    </div>
  </div>

  <div id="main" data-live-edit-type="1" data-live-edit-key="80" data-live-edit-name="Main">
    <!-- Product show -->
    <%@ include file="../../admin2/live-edit/data/mock-component-10022.html" %>

    <div class="row-fluid" data-live-edit-type="2" data-live-edit-key="010101" data-live-edit-name="Layout 70-30">

      <div class="span8" data-live-edit-type="1">
        <!-- Description -->
        <%@ include file="../../admin2/live-edit/data/mock-component-10024.html" %>

        <!-- Gallery -->
        <%@ include file="../../admin2/live-edit/data/mock-component-10023.html" %>

        <!-- Comments -->
        <%@ include file="../../admin2/live-edit/data/mock-component-10026.html" %>

      </div>
      <div class="span4" data-live-edit-type="1">
        <!-- Accessories -->
        <%@ include file="../../admin2/live-edit/data/mock-component-10025.html" %>

        <!-- Banner -->
        <%@ include file="../../admin2/live-edit/data/mock-component-10027.html" %>
      </div>
    </div>


  </div>
  <hr>

  <div class="footer">
    <small class="pull-right">Demo site made by Enonic 2013</small>
  </div>

</div>


<script src="js/bootstrap.js"></script>

<%@ include file="live-edit/scripts.jsp" %>

<%@ include file="live-edit/admin-links.jsp" %>

</body>
</html>
