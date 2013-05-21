<%@ page import="com.enonic.wem.web.jsp.JspHelper" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons-metro.css">

  <!-- WEM ExtJS theme -->

  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/admin.css">

  <!-- Ext JS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!-- Configuration -->
  <script type="text/javascript" src="global.config.js"></script>
  <script type="text/javascript">
    Ext.Loader.setConfig({
      paths: {
        'App': '_app/dashboard/js',
        'Admin': 'resources/app',
        'Common': 'common/js'
      }
    });
  </script>

  <!-- Application -->

  <script type="text/javascript" src="app-dashboard.js"></script>

</head>
<body>
</body>
</html>
