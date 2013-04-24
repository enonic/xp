<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>

  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>

  <!-- Styles -->
  <link rel="stylesheet" type="text/css" href="../../../admin/resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="../../../admin/resources/css/icons-icomoon.css">
  <link rel="stylesheet" type="text/css" href="../../../admin/resources/css/icons-metro.css">
  <link rel="stylesheet" type="text/css" href="../../../admin/resources/lib/ext/resources/css/admin.css">

  <!-- ExtJS -->
  <script type="text/javascript" src="../../../admin/resources/lib/ext/ext-all-debug.js"></script>

  <!-- Configuration -->
  <script type="text/javascript">

    window.CONFIG = {
      baseUrl: '<%= helper.getBaseUrl() %>'
    };

    Ext.Loader.setConfig({
      enabled: true,
      paths: {
        'Admin': 'js'
      },
      disableCaching: false
    });

    Ext.require('Admin.lib.RemoteService');

    Ext.override(Ext.LoadMask, {
      floating: {
        shadow: false
      },
      msg: undefined,
      cls: 'admin-load-mask',
      msgCls: 'admin-load-text',
      maskCls: 'admin-mask-white'
    });

  </script>

  <!-- TODO: Inline everything. -->
  <script type="text/javascript" src="../../../admin/resources/app/view/XTemplates.js"></script>

  <!-- Third party plugins -->
  <script type="text/javascript" src="../../../admin/resources/lib/plupload/js/plupload.full.js"></script>

  <!-- Application -->
  <script type="text/javascript" src="js/main.js"></script>

</head>
<body>
</body>
</html>
