<!DOCTYPE html>

<html>
<head>
  <title>Enonic WEM - Login</title>
  <style type="text/css">
    html, body {
      width: 100%;
      height: 100%;
    }

    body {
      background: url("resources/images/mont_blanc.jpg") no-repeat center;
      font: bold 13px/1.5 Arial, sans-serif;
      color: #FFF;
      padding: 0;
      margin: 0;
      text-align: center;
    }

    #upper_ctr {
      position: absolute;
      top: 0;
      left: 0;
      padding: 1em;
    }

    #login_form {
      position: absolute;
      top: 50%;
      left: 0;
      width: 100%;
      height: 240px;
      margin: -120px 0 0;
      background: url("resources/images/bg_black.png") repeat;
    }

    #login_form .row {
      background: none;
      margin-bottom: 10px;
      padding: 2px 0;
      width: 100%;
    }

    #login_form .row.active {
      background: url("resources/images/bg_white.png") repeat;
    }

    #login_form .row .wrapper {
      width: 300px;
      margin: 0 auto;
      text-align: center;
      position: relative;
      display: block;
    }

    #login_form h1 {
      padding: 20px 0 0;
      font-size: 24px;
      font-weight: normal;
    }

    #login_form .input {
      padding: 0 5px;
      line-height: 30px;
      height: 30px;
      color: #000;
      background: #fff;
      border: 0 none;
      width: 100%;
    }

    #login_form #select_ctr {
      position: absolute;
      width: 200px;
      right: -220px;
      top: 5px;
    }

    #login_form select.select {
      border: 0 none;
      font-weight: bold;
      margin: 0;
      z-index: 10;
      float: left;
      position: relative;
    }

      /* dynamically created SPAN, placed below the SELECT */
    #login_form span.select {
      position: absolute;
      top: 0;
      left: 0;
      padding: 0 18px 0 0;
      background: url(resources/images/select_trigger.png) no-repeat top right;
      cursor: default;
      z-index: 1;
      text-align: left;
    }

    #login_form .submit {
      padding: 6px 20px;
    }

    #login_form input[type=submit] {
      margin: 0 auto;
    }

    #login_form #form_ctr {
      position: absolute;
      right: 0;
      bottom: 0;
      padding: 1em 0;
      margin: 0;
    }

    #login_form #form_ctr li {
      padding: 0 1em;
      list-style-type: disc;
      display: inline;
    }

    #login_form #form_ctr li a {
      color: #fff;
      text-decoration: none;
    }

  </style>
  <script type="text/javascript" src="../dev/live-edit/app/lib/jquery-1.8.0.min.js"></script>
</head>
<body>
<div id="upper_ctr">
  Enonic WEM 5.0.1 Enterprise Edition - Licensed to Large Customer
</div>
<form id="login_form" action="#">
  <div class="row">
    <h1 class="wrapper">Enonic WEM</h1>
  </div>
  <div class="row">
    <div class="wrapper">
      <input name="username" type="text" class="input" value="name" tabindex="2">

      <div id="select_ctr">
        <select name="userstore" class="select" tabindex="1">
          <option value="1">LDAP</option>
          <option value="1">local</option>
          <option value="1">Some very long value</option>
        </select>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="wrapper">
      <input name="password" type="password" class="input" value="password" tabindex="3">
    </div>
  </div>
  <div class="row">
    <div class="wrapper">
      <input type="submit" class="submit" value="Login"/>
    </div>
  </div>
  <ul id="form_ctr">
    <li><a href="#">Documentation</a></li>
    <li><a href="#">Community</a></li>
    <li><a href="#">About</a></li>
  </ul>
</form>

<script type="text/javascript">

  $(document).ready(function () {
    $('#login_form .input, #login_form .select').focus(onFocus).blur(onBlur);
    transformSelect();
    $('#login_form input[name=username]').focus();
  });

  function onFocus(event) {
    $(event.target).parents('.row').addClass('active')
  }

  function onBlur(event) {
    $(event.target).parents('.row').removeClass('active');
  }

  function transformSelect() {

    $('select.select').each(function () {
      var select = $(this);
      var title = select.attr('title');
      if ($('option:selected', this).val() != '') {
        title = $('option:selected', this).text();
      }
      select.css({'z-index': 10, 'opacity': 0, '-khtml-appearance': 'none'})
          .after('<span class="select">: ' + title + '</span>')
          .change(function () {
            val = $('option:selected', this).text();
            $(this).next().text(": " + val);
          })
    });

  }

</script>

</body>
</html>