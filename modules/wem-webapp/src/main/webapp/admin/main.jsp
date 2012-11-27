<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="_app/main/css/main.css">

  <link rel="stylesheet" type="text/css" href="resources/css/admin-top-bar.css">
  <link rel="stylesheet" type="text/css" href="resources/css/admin-start-menu.css">

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
          'Admin.controller.Controller',
          'Admin.controller.LauncherToolbarController',
          'Admin.controller.NotifyUserController',
          // old controllers from _app/main
          'NotificationWindowController',
          'ActivityStreamController'
        ],

        launch: function () {
          Ext.create('Ext.container.Viewport', {
            id: 'main-viewport',
            layout: 'border',
            style: 'border: medium none',
            padding: 0,
            items: [
              {
                region: 'center',
                layout: 'border',
                items: [
                  {
                    region: 'north',
                    xtype: 'launcherToolbar'
                  },
                  {
                    id: 'main-viewport-center',
                    region: 'center',
                    bodyCls: 'main-viewport-center-body',
                    html: '<div id="app-frames" style="height: 100%; width: 100%;"><!-- --></div>'
                  }
                ]
              },
              {
                region: 'east',
                xtype: 'activityStreamPanel',
                collapsed: true
              }
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
