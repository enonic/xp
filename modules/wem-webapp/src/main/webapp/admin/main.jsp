<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/admin.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons-metro.css">

  <!-- Ext JS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="global.config.js"></script>
  <script type="text/javascript">

    Ext.Loader.setConfig({
      paths: {
        'App': '_app/main/js',
        'Common': 'common/js',
        'Admin': 'resources/app'
      }
    });

  </script>

  <!-- Templates -->

  <script type="text/javascript" src="resources/app/view/XTemplates.js"></script>

  <!-- Third party libraries -->

  <script type="text/javascript" src="resources/app/lib/humane.js"></script>

  <!-- Application -->

  <script type="text/javascript">
    Ext.require('Ext.app.Application');
    var mainApp;

    Ext.onReady(function () {
      mainApp = Ext.create('Ext.app.Application', {
        name: 'App',
        appFolder: '_app/main/js',

        requires: [
          'Admin.lib.UriHelper'
        ],

        controllers: [
          'Admin.controller.TopBarController',
          'Admin.controller.NotifyUserController',
          // old controllers from _app/main
          'ActivityStreamController'
        ],

        launch: function () {
          Ext.create('Ext.container.Viewport', {
            id: 'mainViewport',
            layout: 'border',
            style: 'border: medium none',
            padding: 0,
            items: [
              {
                region: 'center',
                bodyCls: 'main-viewport-center-body',
                html: '<div id="appFrames" style="height: 100%; width: 100%;"></div>'
              }
              //{
              //  region: 'east',
              //  xtype: 'activityStreamPanel',
              //  collapsed: true
              //}
            ]
          });
        }
      });
    });

  </script>

</head>
<body>
</body>
</html>
