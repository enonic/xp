<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/user-preview.css">
  <link rel="stylesheet" type="text/css" href="resources/css/user-preview-panel.css">
  <link rel="stylesheet" type="text/css" href="resources/css/BoxSelect.css">

  <!-- WEM ExtJS theme -->

  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/admin.css">

  <!-- ExtJS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

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

  <!-- Templates -->

  <script type="text/javascript" src="resources/app/view/XTemplates.js"></script>

  <!-- Third party plugins -->

  <script type="text/javascript" src="resources/lib/plupload/js/plupload.full.js"></script>
  <script type="text/javascript" src="resources/lib/jit/jit.js"></script>

  <!-- Application -->

  <script type="text/javascript">
    Ext.application({
      name: 'App',

      controllers: [
        'Admin.controller.account.Controller',
        'Admin.controller.account.GridPanelController',
        'Admin.controller.account.BrowseToolbarController',
        'Admin.controller.account.DetailPanelController',
        'Admin.controller.account.FilterPanelController',
        'Admin.controller.account.EditUserPanelController',
        'Admin.controller.account.UserWizardController',
        'Admin.controller.account.GroupWizardController',
        'Admin.controller.account.UserPreviewController',
        'Admin.controller.account.GroupPreviewController',
        'Admin.controller.NotifyUserController'
      ],

      requires: [
        'Admin.view.TabPanel',
        'Admin.lib.UriHelper',
        'Admin.lib.RemoteService'
      ],

      launch: function () {
        Ext.create('Ext.container.Viewport', {
          layout: 'fit',
          cls: 'admin-viewport',

          items: [
            {
              xtype: 'cmsTabPanel',
              items: [
                {
                  id: 'tab-browse',
                  title: 'Browse',
                  closable: false,
                  border: false,
                  xtype: 'panel',
                  layout: 'border',
                  items: [
                    {
                      region: 'west',
                      xtype: 'accountFilter',
                      width: 200
                    },
                    {
                      region: 'center',
                      xtype: 'accountShow'
                    }
                  ]
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
