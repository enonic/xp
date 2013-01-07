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
    #admin-home-background-container a {
      color: #fff;
    }

    #admin-home-background-container h3,
    #admin-home-installation-info {
      font-size: 24px;
      color: #fff;
    }

    #admin-home-background-container {
      position: fixed;
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


    /** Login form */

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
      top: 0;
      left: 590px;
      bottom: 0;
      color: #fff !important;
    }

    #admin-home-login-panel-licensed {
      text-align: center;
      padding-top: 50px;
    }

    #admin-home-links-container {
      position: absolute;
      text-align: center;
      bottom: 30px;
    }

    /** App selector */

    #admin-home-app-selector-container {
      -moz-transition: all .2s ease-out;
      -moz-transform: scale(.9);
      opacity: 0;
      color: #fff;
      left: 110px;
      position: absolute;
      top: 235px;
      width: 480px;
    }

    .admin-home-app-tile {
      -moz-transition: all 0.1s linear;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      background-color: #1c59af;
      float: left;
      width: 110px;
      height: 100px;
      margin: 0 10px 10px 0;
    }
    .admin-home-app-tile > .img-container  {
      position: absolute;
      width: 100%;
      margin-top: 10px;
      text-align: center;
    }
    .admin-home-app-tile > .img-container > img  {
      width: 48px;
      height: 48px;
    }
    .admin-home-app-tile > .text-container {
      position: absolute;
      bottom: 10px;
      width: 100%;
      text-align: center;
    }

    .admin-home-app-tile:hover {
      background-color: #012056;
    }

    /*
    .admin-home-app-tile:active {
      -moz-transform: scale(.97);
    }
    */

    #admin-home-app-selector-container.fade-in {
      opacity: 1 !important;
      -moz-transform: scale(1) !important;
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
<div id="admin-home-background-container" style="background-image: url(resources/images/710948main_typhoon_bopha_1600_1600-1200.jpg)">

  <div id="admin-home-info-container">
    <div id="admin-home-installation-info">| Production</div>
    <div id="admin-home-version-info">5.0.1 Enterprise Edition</div>
  </div>

  <div id="admin-home-login-panel">
    <div id="admin-home-login-panel-form">
      <div id="admin-home-login-panel-form-inner"></div>
      <div id="admin-home-login-panel-licensed">Licensed to Large Customer</div>
    </div>
    <div id="admin-home-links-container">
      <a href="http://www.enonic.com/community">Community</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="http://www.enonic.com/docs">Documentation</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://enonic.com/en/home/enonic-cms">About</a>
    </div>
  </div>

  <div id="admin-home-app-selector-container">
    <div class="admin-home-app-tile">
      <div class="img-container">
        <img src="resources/images/icons/metro/48x48/data.png"/>
      </div>
      <div class="text-container">Content Manager</div>
    </div>
    <div class="admin-home-app-tile">
      <div class="img-container">
        <img src="resources/images/icons/metro/48x48/data.png"/>
      </div>
      <div class="text-container">Content Manager</div>
    </div>
    <div class="admin-home-app-tile">
      <div class="img-container">
        <img src="resources/images/icons/metro/48x48/data.png"/>
      </div>
      <div class="text-container">Content Manager</div>
    </div>
    <div class="admin-home-app-tile">
      <div class="img-container">
        <img src="resources/images/icons/metro/48x48/data.png"/>
      </div>
      <div class="text-container">Content Manager</div>
    </div>
    <div class="admin-home-app-tile">
      <div class="img-container">
        <img src="resources/images/icons/metro/48x48/data.png"/>
      </div>
      <div class="text-container">Content Manager</div>
    </div>
    <div class="admin-home-app-tile">
      <div class="img-container">
        <img src="resources/images/icons/metro/48x48/data.png"/>
      </div>
      <div class="text-container">Content Manager</div>
    </div>
    <div class="admin-home-app-tile">
      <div class="img-container">
        <img src="resources/images/icons/metro/48x48/data.png"/>
      </div>
      <div class="text-container">Content Manager</div>
    </div>
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
      renderTo: 'admin-home-login-panel-form-inner',
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
          emptyText: 'password',
          width: 200,
          tabIndex: 3
        },
        {
          xtype: 'button',
          formBind: true,
          disabled: true,
          colspan: 2,
          style:'float:right;margin-right:5px',
          text: 'Log In',
          tabIndex: 4,
          handler: function (button) {
            var loginForm = Ext.get('admin-home-login-panel-form');
            var appSelector = Ext.get('admin-home-app-selector-container');
            loginForm.animate({
              duration: 500,
              to: {
                opacity: 0
              },
              listeners: {
                afteranimate: function () {
                  loginForm.hide();
                  appSelector.addCls('fade-in');
                }
              }
            });
          }
        }
      ]
    });

  });

</script>

</body>
</html>
