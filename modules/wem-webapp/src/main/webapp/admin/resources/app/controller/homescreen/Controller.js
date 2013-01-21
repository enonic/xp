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
                click: me.onLoginButtonClick
            },
            'loginPanel textfield[itemId=userstoreCombo]': {
                keydown: me.onFormKeyDown
            },
            'loginPanel textfield[itemId=userId]': {
                keydown: me.onFormKeyDown
            },
            'loginPanel textfield[itemId=password]': {
                keydown: me.onFormKeyDown
            },
            'homescreen': {
                afterrender: function (view) {
                    Admin.lib.RemoteService.system_getSystemInfo({}, function (r) {
                        view.setInstallationLabelText(r.installationName);
                        view.setVersionText(r.version);
                    });
                }
            }
        });
    },


    initView: function () {
        var dummyCookie = Ext.util.Cookies.get('dummy_userIsLoggedIn'),
            userIsLoggedIn = dummyCookie && dummyCookie === 'true';

        Ext.create('Admin.view.homescreen.Homescreen', {
            userIsLoggedIn: userIsLoggedIn
        });
    },


    onFormKeyDown: function (field, evt) {
        if (evt.getKey() === evt.ENTER) {
            this.handleLoginSubmit();
        }
    },


    onLoginButtonClick: function () {
        this.handleLoginSubmit();
    },


    handleLoginSubmit: function () {
        var me = this,
            loginFormPanel = me.getHomeScreen().getLoginFormPanel();

        loginFormPanel.getForm().submit({
            url: 'dummy-login-response.jsp',
            success: function (form, action) {
                Ext.util.Cookies.set('dummy_userIsLoggedIn', 'true');

                loginFormPanel.animate({
                    duration: 500,
                    to: {
                        opacity: 0
                    },
                    listeners: {
                        afteranimate: function () {
                            me.getHomeScreen().displayAppSelector();
                        }
                    }
                });
            },
            failure: function (form, action) {
                /**/
            }
        });
    },


    getHomeScreen: function () {
        return Ext.ComponentQuery.query('homescreen')[0];
    }

});