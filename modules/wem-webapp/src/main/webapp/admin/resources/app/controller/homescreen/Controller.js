Ext.define('Admin.controller.homescreen.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],
    views: [
        'Admin.view.homescreen.Homescreen'
    ],


    init: function () {
        var me = this;

        me.control({
            '#mainViewport': {
                afterrender: {
                    fn: this.initView
                }
            },
            '#appSelectorListView': {
                itemclick: function (view, record, item, index, evt, eOpts) {
                    me.getHomeScreen().toggleShowHide();
                    me.application.fireEvent('loadApplication', record.data);
                },
                itemmouseenter: function (view, record, item, index, evt, eOpts) {
                    var data = record.data;
                    me.getHomeScreen().getAppSelectorContainer().updateAppInfoText(data.name, data.description);
                },
                itemmouseleave: function (view, record, item, index, evt, eOpts) {
                    me.getHomeScreen().getAppSelectorContainer().updateAppInfoText('', '');
                }
            },
            'loginPanel button[itemId=loginButton]': {
                click: this.onLoginButtonClick

            }
        });
    },


    onLoginButtonClick: function () {
        this.handleLoginSubmit();
    },


    handleLoginSubmit: function () {
        var me = this,
            loginForm = me.getHomeScreen().getLoginFormPanel();

        loginForm.getForm().submit({
            url: 'dummy-login-response.jsp',
            success: function (form, action) {
                me.getHomeScreen().displayAppSelectorView();
            },
            failure: function (form, action) {
                /**/
            }
        });
    },


    initView: function () {
        // Do not create the homescreen when ?homescreen=false
        // TODO: remove!
        var urlParts = document.URL.split('?');
        if (urlParts.length > 1) {
            var urlParams = Ext.urlDecode(urlParts[urlParts.length - 1]);
            console.log(urlParams)
            if (urlParams.homescreen && urlParams.homescreen === 'false') {
                return;
            }
        }

        Ext.create('Admin.view.homescreen.Homescreen');
    },


    getHomeScreen: function () {
        return Ext.ComponentQuery.query('homescreen')[0];
    }

});