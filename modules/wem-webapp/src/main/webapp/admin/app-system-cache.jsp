<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css">

  <!-- ExtJS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!-- Templates -->

  <script type="text/javascript" src="_app/cache/js/templates/Templates.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="config.js"></script>
  <script type="text/javascript">
    Ext.Loader.setConfig({
      enabled: true,
      paths: {
        'Admin': 'resources/app',
        'App': '_app/cache/js',
        'Common': 'common/js',
        'Lib': 'resources/lib'
      }
    });

  </script>

  <!-- Application -->

  <script type="text/javascript">
    Ext.application({
      name: 'App',
      appFolder: '_app/cache/js',

      controllers: [
        'SystemCacheController'
      ],

      requires: [
        'App.view.ShowPanel'
      ],


      launch: function () {
        Ext.create('Ext.container.Viewport', {
          layout: 'border',
          padding: 5,

          items: [
            {
              region: 'center',
              xtype: 'systemCacheShow'
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
