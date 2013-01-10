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
            }
        });
    },


    initView: function () {
        // Temp: Do not create the homescreen when ?homescreen=false
        var urlParts = document.URL.split('?');
        if (urlParts.length > 1) {
            var urlParams = Ext.urlDecode(urlParts[urlParts.length - 1]);
            if (urlParams.homescreen && urlParams.homescreen === 'false') {
                return;
            }
        }

        var dummyCookie = Ext.util.Cookies.get('dummy_userIsLoggedIn');
        var userIsLoggedIn = dummyCookie && dummyCookie === 'true';

        Ext.create('Admin.view.homescreen.Homescreen', {
            userIsLoggedIn: userIsLoggedIn
        });
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
                            me.getHomeScreen().displayAppSelector(true);
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