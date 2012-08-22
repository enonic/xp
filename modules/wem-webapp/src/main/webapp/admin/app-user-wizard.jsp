<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>User Wizard Test</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css">

  <!-- Ext Js -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="config.js"></script>
  <script type="text/javascript">
    Ext.Loader.setConfig({
      paths: {
        'App': '_app/account/js',
        'Common': 'common/js'
      }
    });
  </script>

  <!-- Application -->

  <script type="text/javascript">
    Ext.application({
      name: 'App',
      appFolder: '_app/account/js',

      controllers: [
        'UserWizardController'
      ],

      requires: [
        'App.view.wizard.user.UserWizardPanel'
      ],

      launch: function () {
        Ext.create('Ext.container.Viewport', {
          layout: 'fit',
          items: [
            {
              xtype: 'userWizardPanel'
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
