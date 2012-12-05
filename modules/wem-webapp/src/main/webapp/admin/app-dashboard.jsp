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

  <link rel="stylesheet" type="text/css" href="resources/css/admin-top-bar.css">
  <link rel="stylesheet" type="text/css" href="resources/css/admin-start-menu.css">

  <!-- Ext JS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="global.config.js"></script>
  <script type="text/javascript">
    Ext.Loader.setConfig({
      enabled: true,
      paths: {
        'App': '_app/dashboard/js',
        'Admin': 'resources/app',
        'Common': 'common/js'
      },
      disableCaching: false
    });
  </script>

  <!-- Application -->

  <script type="text/javascript">
    Ext.application({
      name: 'App',
      appFolder: '_app/dashboard/js',

      requires: [
        // old views from _app/dashboard
        'App.view.DashboardPalette',
        'App.view.DashboardCanvas',
        'App.view.Dashlet',
        'App.view.ChartDashlet',
        'App.view.GridDashlet'
      ],

      controllers: [
        'Admin.controller.TopBarController'
      ],

      launch: function () {
        var me = this;
        Ext.create('Ext.container.Viewport', {
          cls: 'dashboard',
          layout: 'border',

          items: [
            {
              xtype: 'topBar',
              region: 'north',
              appName: 'Dashboard',
              appIconCls: 'icon-dashboard-24'
            },
            {
              xtype: 'dashboardPalette',
              region: 'west',
              width: 200,
              split: true,
              collapsible: true,
              animCollapse: true,
              collapsed: true
            },
            {
              xtype: 'dashboardCanvas',
              region: 'center',
              items: [
                {
                  items: [
                    {
                      xtype: 'gridDashlet'
                    },
                    {
                      html: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque vel diam in arcu pulvinar elementum id quis felis. Curabitur adipiscing, arcu vitae imperdiet luctus, libero elit ornare libero, vel vehicula sem purus at nunc. Fusce eget mi id sem euismod porta a a orci. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur tempor urna sit amet nisl feugiat fermentum. Morbi purus felis, pharetra in dignissim id, mattis ac metus. Morbi hendrerit, nisl at semper tincidunt, lacus libero eleifend augue, non accumsan purus orci sit amet quam. Ut dictum eleifend leo, vitae egestas velit vulputate eu. Aenean at diam non est ullamcorper commodo vel a sapien. Cras lacinia libero quis leo condimentum et porta neque dictum. Sed eleifend diam nec augue pellentesque ut fringilla tellus facilisis. Nullam mauris libero, vulputate sed euismod eget, faucibus ultricies enim. Morbi id elementum metus. In mattis lacus in turpis aliquam pretium. Morbi tincidunt ligula semper velit lobortis varius. Donec congue nibh a tortor blandit vel consequat risus pellentesque. Praesent commodo risus id est rutrum congue. Proin ante massa, vestibulum ut volutpat accumsan, bibendum eget tellus.'
                    }
                  ]
                },
                {
                  items: [
                    {
                      xtype: 'chartDashlet'
                    }
                  ]
                },
                {
                  items: [
                    {
                      html: 'Aliquam ultrices fermentum pulvinar. Sed non nisi sed nulla pellentesque viverra. Praesent porta pellentesque pulvinar.'
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
