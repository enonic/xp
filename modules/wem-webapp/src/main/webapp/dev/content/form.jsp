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
      float: left;
      vertical-align: top;
      padding-right: 10px;
    }

    .admin-related-item .center-column {
      width: 320px;
      display: table-cell;
      vertical-align: top;
      float: left;
    }

    .admin-related-item .right-column {
      display: table-cell;
      vertical-align: top;
      float: right;
    }

    .admin-formitemset-block {
      border: 1px solid #aaa !important;
    }


    .admin-sortable-insert-helper-below {
      border-bottom: 2px solid #ff4500 !important;
    }

    .admin-sortable-insert-helper-above {
      border-top: 2px solid #ff4500 !important;
    }

    .admin-draghandle {
      color: #59008c;
      cursor: move;
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
