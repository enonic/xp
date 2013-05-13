<%@ page import="com.enonic.wem.web.jsp.JspHelper" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons-metro.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons-icomoon.css">
  <link rel="stylesheet" type="text/css" href="resources/css/admin-preview-panel.css">

  <!-- WEM ExtJS theme -->

  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/admin.css"/>

  <!-- ExtJS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!--  ExtJS UX -->

  <link rel="stylesheet" type="text/css" href="resources/lib/ux/toggleslide/css/style.css"/>

  <script type="text/javascript" src="resources/lib/ux/toggleslide/Thumb.js"></script>
  <script type="text/javascript" src="resources/lib/ux/toggleslide/ToggleSlide.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="global.config.js"></script>
  <script type="text/javascript" charset="utf-8">

    window.CONFIG = {
      baseUrl: '<%= JspHelper.getBaseUrl(request) %>'
    };

    Ext.Loader.setConfig({
      paths: {
        'Common': 'common/js',
        'Admin': 'resources/app',
        'Ext.ux': 'resources/lib/ux'
      },
      disableCaching: false
    });

  </script>

  <!-- Templates -->

  <script type="text/javascript" src="resources/app/view/XTemplates.js"></script>
  <script type="text/javascript" src="resources/app/view/contentManager/wizard/evaluateContentDisplayNameScript.js"></script>

  <!-- Third party plugins -->

  <script type="text/javascript" src="resources/lib/plupload/js/plupload.full.js"></script>
  <script type="text/javascript" src="resources/lib/jit/jit-yc.js"></script>

  <!-- Application -->

  <script type="text/javascript" src="app-content-manager.js"></script>

</head>
<body>
</body>
</html>
