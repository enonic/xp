<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/admin.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons-metro.css">

  <link rel="stylesheet" type="text/css" href="resources/css/admin-top-bar.css">
  <link rel="stylesheet" type="text/css" href="resources/css/admin-start-menu.css">

  <!-- Ext JS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!-- Configuration -->
  <script type="text/javascript" src="resources/app/view/XTemplates.js"></script>
  <script type="text/javascript" src="global.config.js"></script>
  <script type="text/javascript">
    Ext.Loader.setConfig({
      enabled: true,
      paths: {
        'App': '_app/dashboard/js',
        'Admin': 'resources/app',
        'Common': 'common/js'
      },
      disableCaching: false
    });
  </script>

  <!-- Application -->

  <script type="text/javascript" src="app-dashboard.js"></script>

</head>
<body>
</body>
</html>
