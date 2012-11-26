<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="../../admin/resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="../../admin/resources/css/main.css">
  <link rel="stylesheet" type="text/css" href="../../admin/resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="../../admin/resources/css/admin-preview-panel.css">
  <link rel="stylesheet" type="text/css" href="../../admin/resources/css/admin-tree-panel.css">

  <!-- ExtJS -->

  <script type="text/javascript" src="../../admin/resources/lib/ext/ext-all-debug.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="../../admin/global.config.js"></script>
  <script type="text/javascript" charset="utf-8">

    window.CONFIG = {
      baseUrl: '<%= helper.getBaseUrl() %>'
    };

    Ext.Loader.setConfig({
      paths: {
        'Common': 'common/js',
        'Admin': '../../admin/resources/app'
      },
      disableCaching: false
    });

  </script>

  <!-- Templates -->

  <!--script type="text/javascript" src="../../admin/resources/app/view/XTemplates.js"></script-->

  <!-- Third party plugins -->


  <!-- Application -->

  <script type="text/javascript" src="app.js"></script>


</head>
<body>
</body>
</html>
