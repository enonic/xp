<%@ page import="com.enonic.wem.web.jsp.JspHelper" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/admin-preview-panel.css">
  <link rel="stylesheet" type="text/css" href="resources/css/BoxSelect.css">

  <!-- Ext JS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!-- Templates -->

  <script type="text/javascript" src="resources/app/view/XTemplates.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="global.config.js"></script>
  <script type="text/javascript">
    window.CONFIG = {
      baseUrl: '<%= JspHelper.getBaseUrl(request) %>'
    };

    Ext.Loader.setConfig({
      paths: {
        'Common': 'common/js',
        'Main': '_app/main/js',
        'Admin': 'resources/app'
      }
    });

  </script>

  <!-- Application -->

  <script type="text/javascript">
    Ext.application({
      name: 'App',
      appFolder: '_app/userstore/js',

      controllers: [
        'Admin.controller.userstore.GridPanelController',
        'Admin.controller.userstore.UserstorePreviewController',
        'Admin.controller.userstore.UserstoreWizardController',
        'Admin.controller.userstore.BrowseToolbarController'
      ],

      requires: [
        'Admin.MessageBus',
        'Admin.NotificationManager',
        'Admin.view.TabPanel',
        'Admin.lib.UriHelper',
        'Admin.lib.RemoteService'
      ],

      launch: function () {
        Ext.create('Ext.container.Viewport', {
          layout: 'fit',
          cls: 'admin-viewport',
          padding: 5,

          items: [
            {
              xtype: 'cmsTabPanel',
              items: [
                {
                  id: 'tab-browse',
                  title: 'Browse',
                  closable: false,
                  xtype: 'mainPanel'
                }
              ]
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
