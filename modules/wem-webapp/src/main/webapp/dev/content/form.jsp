<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM - Content Data Form</title>
  <link rel="stylesheet" type="text/css" href="../../admin/resources/lib/ext/resources/css/admin.css">
  <style type="text/css">

    .admin-related-item {
      display: table;
      margin: 5px 0;
      width: 100%;
    }

    .admin-related-item img {
      display: table-cell;
      vertical-align: top;
      padding-right: 10px;
      width: 42px;
    }

    .admin-related-item .center-column {
      display: table-cell;
      vertical-align: top;
    }

    .admin-related-item .right-column {
      display: table-cell;
      vertical-align: top;
      text-align: right;
    }

  </style>

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
      disableCaching: true
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
