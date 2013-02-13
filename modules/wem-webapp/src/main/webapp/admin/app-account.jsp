<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons-metro.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons-icomoon.css">
  <link rel="stylesheet" type="text/css" href="resources/css/user-preview.css">
  <link rel="stylesheet" type="text/css" href="resources/css/user-preview-panel.css">
  <link rel="stylesheet" type="text/css" href="resources/css/BoxSelect.css">

  <!-- WEM ExtJS theme -->

  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/admin.css">

  <!-- ExtJS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="global.config.js"></script>
  <script type="text/javascript">

    window.CONFIG = {
      baseUrl: '<%= helper.getBaseUrl() %>'
    };

    Ext.Loader.setConfig({
      paths: {
        'Common': 'common/js',
        'Main': 'app/main/js',
        'Admin': 'resources/app'
      },
      disableCaching: false
    });

  </script>

  <!-- Templates -->

  <script type="text/javascript" src="resources/app/view/XTemplates.js"></script>

  <!-- Third party plugins -->

  <script type="text/javascript" src="resources/lib/plupload/js/plupload.full.js"></script>
  <script type="text/javascript" src="resources/lib/jit/jit.js"></script>

  <!-- Application -->

  <script type="text/javascript" src="app-account.js"></script>


</head>
<body>
</body>
</html>
