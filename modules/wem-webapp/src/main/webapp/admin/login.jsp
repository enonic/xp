<!DOCTYPE html>

<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM - Login</title>
  <script type="text/javascript" src="../admin/resources/lib/ext/ext-all.js"></script>
  <link rel="stylesheet" type="text/css" href="../admin/resources/lib/ext/resources/css/admin.css" />

  <style>
    body {
      background: url(../admin/resources/images/mont_blanc.jpg) no-repeat center center fixed;
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
      padding: 0;
      margin: 0;
    }

    a {
      color: #fff !important;
      text-decoration: none !important;
    }

    h1 {
      color: #fff;
      font-size: 24px !important;
    }

    #version-info {
      position: absolute;
      top: 10px;
      left: 10px;
      color: #fff !important;
    }

    #stripe {
      position: absolute;
      top: 50%;
      left: 0;
      margin-top: -115px; /* half of #content height*/
      background: rgba(0, 0, 0, 0.35);
      width: 100%;
      height: 230px;
    }

    #form-container {
      text-align: left;
      position: absolute;
      left: 50%;
      top: 15%;
      width: 500px;
      margin-left: -140px;
    }

    .form {
      background-color: transparent !important;
    }

    #links-container {
      position: absolute;
      bottom: 10px;
      right: 10px;
      color: #fff !important;
    }

    #links-container a {
      margin-right: 30px;
    }

    #links-container a:last-child {
      margin-right: 0;
    }

    .combo-field-label {
      color: #fff;
      display: block;
      margin-top: 4px;
      margin-right: 2px;
      font-weight: bold;
      padding-right: 2px;
    }
  </style>
</head>
<body>

<div id="version-info">
  Enonic WEM 5.0.1 Enterprise Edition - Licensed to Large Customer
</div>
<div id="stripe">
  <div id="form-container">
  </div>
  <div id="links-container"></div>
</div>


<script type="text/javascript">
  var userStoresStore = Ext.create('Ext.data.Store', {
    fields: ['key', 'name', 'default'],
    data: [
      {'key': '1', 'name': 'ABC', default: false},
      {'key': '2', 'name': 'LDAP', default: true},
      {'key': '3', 'name': 'Local', default: false},
      {'key': '4', 'name': 'Some very long value', default: false}
    ]
  });

  function getDefaultUserStore() {
    return userStoresStore.findRecord('default', true);
  }

  Ext.onReady(function() {
    Ext.create('Ext.form.Panel', {
      renderTo: 'form-container',
      bodyCls: 'form',
      border: 0,
      url: 'main.jsp',
      standardSubmit: true,
      method: 'POST',
      layout: {
        type: 'table',
        columns: 2
      },
      defaults: {
        margin: 5
      },
      items: [
        {
          xtype: 'container',
          colspan: 2,
          margin: '0 0 0 95',
          html: '<h1>Enonic WEM</h1>'
        },
        {
          xtype: 'textfield',
          name: 'username',
          allowBlank: false,
          emptyText: 'User name',
          width: 300,
          tabIndex: 1
        },
        {
          xtype: 'combo',
          name: 'userstore',
          id: 'userstoreCombo',
          allowBlank: false,
          store: userStoresStore,
          fieldLabel: ' ',
          labelWidth: 1,
          labelCls: 'combo-field-label',
          queryMode: 'local',
          displayField: 'name',
          valueField: 'key',
          tabIndex: 4
        },
        {
          xtype: 'textfield',
          name: 'password',
          allowBlank: false,
          colspan: 2,
          emptyText: 'Password',
          width: 300,
          tabIndex: 2
        },
        {
          xtype: 'button',
          formBind: true,
          disabled: true,
          margin: '0 0 0 120',
          colspan: 2,
          text: 'Log In',
          tabIndex: 3,
          handler: function(button) {
            var form = this.up('form').getForm();
            form.submit();
          }
        }
      ]
    });

    var userstoreCombo = Ext.getCmp('userstoreCombo');
    userstoreCombo.setValue(getDefaultUserStore().raw.key);
  });

  Ext.create('Ext.Component', {
    html: '<a href="http://www.enonic.com/docs">Documentation</a><a href="http://www.enonic.com/community">Community</a><a href="#">About</a> ',
    renderTo: 'links-container'
  });

</script>

</body>
</html>