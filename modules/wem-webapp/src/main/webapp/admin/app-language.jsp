<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css">

  <!-- Ext JS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="global.config.js"></script>
  <script type="text/javascript">

    Ext.Loader.setConfig({
      paths: {
        'Admin': 'resources/app',
        'App': '_app/language/js',
        'Common': 'common/js',
        'Lib': 'resources/lib'
      }
    });

  </script>

  <!-- Application -->

  <script type="text/javascript">
    Ext.application({
      name: 'App',
      appFolder: '_app/language/js',

      controllers: [
        'LanguageController'
      ],

      requires: [
        'App.view.GridPanel'
      ],

      launch: function () {
        Ext.create('Ext.container.Viewport', {
          layout: 'border',
          padding: 5,

          items: [
            {
              region: 'center',
              xtype: 'languageGrid'
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
