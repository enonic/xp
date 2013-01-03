<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/admin.css">
  <link rel="stylesheet" type="text/css" href="resources/css/main.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons-metro.css">
  <link rel="stylesheet" type="text/css" href="_app/main/css/main.css">

  <style type="text/css">
    #admin-home-background-container {
      position: absolute;
      left: 0;
      top: 0;
      right: 0;
      bottom: 0;
      background-repeat: no-repeat;
      background-position: center center;
      background-attachment: fixed;
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
    }

    #admin-home-info-container {
      position: absolute;
      top: 15px;
      left: 15px;
      background: transparent url(resources/images/wem-enonic-logo.png) 0 0 no-repeat;
      height: 24px;
    }

    #admin-home-installation-info {
      margin-top: -6px;
      padding: 0 0 0 181px;
    }

    #admin-home-version-info {
      color: #d2d6d6;
    }

    #admin-home-login-panel {
      position: absolute;
      width: 295px;
      padding: 240px 45px 0 45px;
      background: rgba(0, 0, 0, 0.2);
      top: 0 !important;
      left: 55% !important;
      bottom: 0 !important;
      color: #fff !important;
    }

    #admin-home-application-selector-panel {
      position: absolute;
      width: 295px;
      padding: 220px 45px 0 45px;
      background: rgba(0, 0, 0, 0.3);
      top: 0 !important;
      right: -295px !important;
      bottom: 0 !important;
      color: #fff !important;

    }

    #admin-home-background-container h3,
    #admin-home-installation-info {
      font-size: 24px;
      color: #fff;
    }


  </style>

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

  <!-- Third party libraries -->

  <!-- Application -->

  <!--script type="text/javascript" src="home.js"></script-->

</head>
<body>
<div id="admin-home-background-container" style="background-image: url(resources/images/mont_blanc.jpg)">

  <div id="admin-home-info-container">
    <div id="admin-home-installation-info">| Production</div>
    <div id="admin-home-version-info">5.0.1 Enterprise Edition</div>
  </div>

  <div id="admin-home-login-panel">

  </div>

</div>

<script type="text/javascript">
  Ext.onReady(function () {
    var userStoresStore = Ext.create('Ext.data.Store', {
      fields: ['key', 'name', 'default'],
      data: [
        {'key': '1', 'name': 'ABC', default: false},
        {'key': '2', 'name': 'LDAP', default: true},
        {'key': '3', 'name': 'Local', default: false},
        {'key': '4', 'name': 'Some very long value', default: false}
      ]
    });
    var defaultUserStore = userStoresStore.findRecord('default', true).raw.key;

    Ext.create('Ext.form.Panel', {
      frame: false,
      border: false,
      bodyStyle: 'background:transparent;',

      renderTo: 'admin-home-login-panel',
      items: [
        {
          xtype: 'component',
          autoEl: {
            tag: 'h3'
          },
          html: 'Login'
        },
        {
          xtype: 'combo',
          name: 'userstore',
          id: 'userstoreCombo',
          allowBlank: false,
          store: userStoresStore,
          fieldLabel: '',
          labelWidth: 1,
          labelCls: 'combo-field-label',
          queryMode: 'local',
          displayField: 'name',
          valueField: 'key',
          width: 200,
          tabIndex: 1,
          listeners: {
            render: function (combo) {
              combo.setValue(defaultUserStore);
            }
          }
        },
        {
          xtype: 'textfield',
          name: 'userid',
          allowBlank: false,
          emptyText: 'userid or e-mail',
          width: 200,
          tabIndex: 2
        },
        {
          xtype: 'textfield',
          name: 'password',
          allowBlank: false,
          inputType: 'password',
          emptyText: 'Password',
          width: 200,
          tabIndex: 3
        },
        {
          xtype: 'button',
          formBind: true,
          disabled: true,
          colspan: 2,
          text: 'Log In',
          tabIndex: 4,
          handler: function (button) {
            var loginPanel = Ext.get('admin-home-login-panel');
            loginPanel.on('click', function () {
              loginPanel.animate({
                duration: 700,
                to: {
                  opacity: 0
                }
              })
            });

          }
        }
      ]
    });


    /*

     */
  });

</script>

</body>
</html>
