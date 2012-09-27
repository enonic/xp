<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM - Create content test page</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css">

  <!-- ExtJS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all.js"></script>

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
      }
    });
  </script>

  <script type="text/javascript">
    Ext.application({
      name: 'App',

      requires: [
        'Admin.view.content.ContentPanel',
        'Admin.lib.RemoteService'
      ],

      launch: function () {
        Ext.create('Ext.container.Viewport', {
          layout: 'fit',
          cls: 'admin-viewport',
          padding: 5,

          items: [
            {
              id: 'tab-content',
              title: 'Content',
              closable: false,
              xtype: 'createContentPanel'
            }
          ]
        });
      }
    });

  </script>

</head>
<body>
</body>
</html>
