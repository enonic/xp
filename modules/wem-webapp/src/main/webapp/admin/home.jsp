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
    #admin-home-background a {
      color: #fff;
    }

    #admin-home-background h3,
    #admin-home-installation-info {
      font-size: 24px;
      color: #fff;
    }

    #admin-home-background {
      position: fixed;
      left: 0;
      top: 0;
      right: 0;
      bottom: 0;
      background-color: #141414;
      background-repeat: no-repeat;
      background-position: center center;
      background-attachment: fixed;
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
    }

    #admin-home-branding {
      position: absolute;
      top: 15px;
      left: 20px;
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

    #admin-home-center {
      width: 775px;
      position: absolute;
      left: 50%;
      margin-left: -375px;
      background-color: transparent;
      top: 0;
      bottom: 0;
    }

    #admin-home-center #admin-home-left-column {
      position: absolute;
      width: 480px;
      background: transparent;
      /*background: rgba(255, 255, 255, 0.2);*/
      top: 0;
      bottom: 0;
      left: 0;
      color: #fff !important;
    }

    #admin-home-center #admin-home-right-column {
      position: absolute;
      width: 295px;
      padding: 265px 45px 0 45px;
      background: rgba(0, 0, 0, 0.2);
      top: 0;
      right: 0;
      bottom: 0;
      color: #fff !important;
    }

    #admin-home-login-licensed-to {
      text-align: center;
      padding-top: 35px;
    }

    #admin-home-links-container {
      position: absolute;
      text-align: center;
      bottom: 30px;
    }


    /** App selector */

    #admin-home-app-selector {
      position: absolute;
      left: 0;
      top: 235px;
      visibility: hidden;
      opacity: 0;
      color: #fff;

      transition: all .2s ease-out;
      -moz-transition: all .2s ease-out;
      -webkit-transition: all .2s ease-out;
      -o-transition: all .2s ease-out;

      transform: scale(.9);
      -moz-transform: scale(.9);
      -webkit-transform: scale(.9);
      -o-transform: scale(.9);
    }

    #admin-home-app-selector-search-container {
      margin-bottom: 10px;
    }

    #admin-home-app-info-container {
      visibility: hidden;
      opacity: 0;
    }
    #admin-home-app-info-name {
      color: #ebebeb;
    }
    #admin-home-app-info-description {
      color: #ebebeb;
    }

    .admin-home-app-tile {
      transition: all 0.1s linear;
      -moz-transition: all 0.1s linear;
      -webkit-transition: all 0.1s linear;
      -o-transition: all 0.1s linear;

      cursor: pointer;
      position: relative;
      overflow: hidden;
      background-color: #1c59af;
      float: left;
      width: 110px;
      height: 100px;
      margin: 0 10px 10px 0;
    }

    .admin-home-app-tile > .img-container {
      position: absolute;
      width: 100%;
      margin-top: 10px;
      text-align: center;
    }

    .admin-home-app-tile > .img-container > img {
      width: 48px;
      height: 48px;
    }

    .admin-home-app-tile > .name-container {
      position: absolute;
      bottom: 10px;
      width: 100%;
      text-align: center;
    }

    .admin-home-app-tile-over {
      background-color: #012056;
    }

    .fade-in {
      opacity: 1 !important;

      transform: scale(1) !important;
      -moz-transform: scale(1) !important;
      -webkit-transform: scale(1) !important;
      -o-transform: scale(1) !important;
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
<!--
http://apod.nasa.gov/apod/image/1212/YosemiteWinterNightPacholka950.jpg
http://apod.nasa.gov/apod/image/1212/halebopp_dimai_852.jpg
http://apod.nasa.gov/apod/image/1212/sunpillar_strand_2000.jpg
http://apod.nasa.gov/apod/image/1212/SGU-Castillo-Orion-IMG4323-1200x800.jpg
 -->

<div id="admin-home-background" style="background-image: url(resources/images/710948main_typhoon_bopha_1600_1600-1200.jpg)">

  <div id="admin-home-branding">
    <div id="admin-home-installation-info">| Production</div>
    <div id="admin-home-version-info">5.0.1 Enterprise Edition</div>
  </div>

  <div id="admin-home-center">
    <div id="admin-home-left-column">
      <div id="admin-home-app-selector">
        <div id="admin-home-app-selector-search-container"></div>
        <div id="admin-home-app-tiles-container">
          <!-- -->
        </div>
      </div>
    </div>

    <div id="admin-home-right-column">
      <div id="admin-home-login-form">
        <h3>Login</h3>
        <div id="admin-home-login-form-container"></div>
        <div id="admin-home-login-licensed-to">Licensed to Large Customer</div>
      </div>
      <div id="admin-home-app-info-container">
        <h3 id="admin-home-app-info-name"><!-- --></h3>
        <div id="admin-home-app-info-description"><!-- --></div>
      </div>
      <div id="admin-home-links-container">
        <a href="http://www.enonic.com/community">Community</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="http://www.enonic.com/docs">Documentation</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a
          href="https://enonic.com/en/home/enonic-cms">About</a>
      </div>
    </div>
  </div>

</div>

<script type="text/javascript">
  Ext.onReady(function () {
    /**
     * Userstore store
     */
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

    /**
     * Apps model
     */
    Ext.define('AppsModel', {
      extend: 'Ext.data.Model',
      fields: [
        { name: 'id', type: 'string' },
        { name: 'name', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'appUrl', type: 'string' },
        { name: 'icon', type: 'string' }
      ]
    });

    /**
     * Apps store
     */
    Ext.create('Ext.data.Store', {
      id: 'appsStore',
      model: 'AppsModel',
      pageSize: 100,
      data: [
        {
          id: 'app-0',
          name: 'Content Manager',
          description: 'Vel eius tation id, duo principes inciderint mediocritatem ut. Utroque ponderum duo ei. Cu cum choro delenit, est elitr utroque scripserit te. Mea ad lorem munere epicuri, clita omnes evertitur sed an. Eu aliquid ornatus principes vel. An eam justo malis debitis, ignota vocibus periculis in sit, alia adolescens ei has.',
          appUrl: 'app-content-manager.jsp',
          icon: 'resources/images/icons/metro/48x48/data.png'
        },
        {
          id: 'app-1',
          name: 'Space Admin',
          description: 'Ius nibh voluptua lobortis ut, ex nec hinc vitae. Eu qui reque movet, tota vivendum postulant ea mea, his oporteat consetetur te. Deserunt vituperatoribus cum ut, cu pri euismod expetenda adipiscing. Facilisi assueverit ad his, at mel posidonium neglegentur consequuntur. Sapientem complectitur usu te, errem platonem ad eam, ne vis assum fastidii.',
          appUrl: 'blank.html',
          icon: 'resources/images/icons/metro/48x48/earth.png'
        },
        {
          id: 'app-2',
          name: 'Content Studio',
          description: 'Doctus recteque intellegat duo ut, cu vidit neglegentur duo, has tritani verterem id. Feugiat omnesque intellegam ut sea, elitr tractatos et mel, pri paulo definiebas liberavisse ea. Eos diceret electram no, ad liber dictas vel. Vix solum tation veritus eu.',
          appUrl: 'app-content-studio.jsp',
          icon: 'resources/images/icons/metro/48x48/cube_molecule.png'
        },
        {
          id: 'app-3',
          name: 'Cluster',
          description: 'Ei malis impedit expetendis quo. His id iusto nihil quando, qui facer equidem molestie ei, dolore possit eripuit ad eum. Dissentiet instructior no nec, blandit salutandi ea vel, legere essent quo at. At eos consul perpetua. Sea duis postea et, cum agam justo cu. Nulla numquam vim no.',
          appUrl: 'blank.html',
          icon: 'resources/images/icons/metro/48x48/virus.png'
        },
        {
          id: 'app-4',
          name: 'Userstores',
          description: 'Ex vis fugit euripidis dissentias, id impedit suavitate mediocritatem sea. Cu error apeirian his. Qui te possit accumsan vituperata, facete prodesset comprehensam ei sea. Odio recteque sententiae an mei. Per meliore deleniti in, ex probo luptatum indoctum nec.',
          appUrl: 'app-userstore.jsp',
          icon: 'resources/images/icons/metro/48x48/user_earth.png'
        },
        {
          id: 'app-5',
          name: 'Accounts',
          description: 'Ei veri vituperata cum, no habeo dicta diceret vis. Eirmod audiam efficiendi quo ne, et duo decore epicurei. In veri liber movet usu, posse lorem erroribus at usu. Mea in vidisse mentitum repudiare, invidunt sensibus adipiscing ea nec. Illud munere te sit, ad singulis definitionem his.',
          appUrl: 'app-account.jsp',
          icon: 'resources/images/icons/metro/48x48/users2.png'
        },
        {
          id: 'app-6',
          name: 'Scheduler',
          description: 'Ei veri vituperata cum, no habeo dicta diceret vis. Eirmod audiam efficiendi quo ne, et duo decore epicurei. In veri liber movet usu, posse lorem erroribus at usu. Mea in vidisse mentitum repudiare, invidunt sensibus adipiscing ea nec. Illud munere te sit, ad singulis definitionem his.',
          appUrl: 'blank.html',
          icon: 'resources/images/icons/metro/48x48/clock.png'
        },
        {
          id: 'app-7',
          name: 'Live Portal Trace',
          description: 'Ei veri vituperata cum, no habeo dicta diceret vis. Eirmod audiam efficiendi quo ne, et duo decore epicurei. In veri liber movet usu, posse lorem erroribus at usu. Mea in vidisse mentitum repudiare, invidunt sensibus adipiscing ea nec. Illud munere te sit, ad singulis definitionem his.',
          appUrl: 'blank.html',
          icon: 'resources/images/icons/metro/48x48/window_time.png'
        }

      ]
    });

    /**
     * App selector data view
     */
    var appSelectorTemplate = new Ext.XTemplate(
        '<tpl for=".">',
        ' <div class="admin-home-app-tile">',
        '	  <div class="img-container">',
        '		  <img src="{icon}"/>',
        '	  </div>',
        '	  <div class="name-container">{name}</div>',
        ' </div>',
        '</tpl>'
    );

    function updateAppInfo(title, description) {
      Ext.fly('admin-home-app-info-name').setHTML(title);
      Ext.fly('admin-home-app-info-description').setHTML(description);
    }

    var appsSelectorView = Ext.create('Ext.view.View', {
      store: Ext.data.StoreManager.lookup('appsStore'),
      tpl: appSelectorTemplate,
      trackOver: true,
      overItemCls: 'admin-home-app-tile-over',
      itemSelector: 'div.admin-home-app-tile',
      emptyText: 'No application found',
      renderTo: 'admin-home-app-tiles-container',
      listeners: {
        itemmouseenter: function ( view, record, item, index, evt, eOpts ) {
          var data = record.data;
          updateAppInfo(data.name, data.description);
        },
        itemmouseleave: function ( view, record, item, index, evt, eOpts ) {
          updateAppInfo('', '');
        },
        itemclick: function ( view, record, item, index, evt, eOpts ) {
          document.location.href = record.data.appUrl;
        }
      }
    });

    /**
     * Form
     */
    Ext.create('Ext.form.Panel', {
      frame: false,
      border: false,
      bodyStyle: 'background:transparent;',
      renderTo: 'admin-home-login-form-container',
      items: [
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
            var loginForm = Ext.get('admin-home-login-form'),
                appSelector = Ext.get('admin-home-app-selector'),
                openApps = Ext.get('admin-home-app-info-container');

            loginForm.setVisibilityMode(Ext.Element.OFFSETS);
            loginForm.animate({
              duration: 500,
              to: {
                opacity: 0
              },
              listeners: {
                afteranimate: function () {
                  loginForm.hide();

                  Ext.getCmp('admin-home-app-selector-search').focus();

                  appSelector.setStyle('visibility', 'visible').addCls('fade-in');
                  openApps.setStyle('visibility', 'visible').addCls('fade-in');
                }
              }
            });
          }
        }
      ]
    });

    /**
     * App Search Field
     */
    Ext.create('Ext.form.field.Text', {
      renderTo: 'admin-home-app-selector-search-container',
      id: 'admin-home-app-selector-search',
      emptyText: 'Application Search',
      width: '470px',
      listeners: {
        change: function (textField, newValue, oldValue) {
          console.log(newValue);
          var appsStore = Ext.data.StoreManager.lookup('appsStore');
          appsStore.clearFilter();
          appsStore.filter('name', newValue);
        }
      }
    });
  });

</script>

</body>
</html>
