Ext.define('Admin.controller.homescreen.Login', {
    extend: 'Admin.controller.homescreen.Controller',

    // TODO: Model/Store. See view LoginPanel for temporary inlined code.
    stores: [],
    models: [],
    views: [
        'Admin.view.homescreen.LoginPanel'
    ],

    init: function () {
        var me = this;

        this.application.on({
            'displayLogin': this.display,
            scope: this
        });

        me.control({
            'loginPanel': {
                afterrender: function (panel) {
                    me.focusUserIdInput(panel);
                }
            },
            'loginPanel button[itemId=loginButton]': {
                click: me.onLoginButtonClick
            },
            'loginPanel textfield[itemId=userstoreCombo]': {
                keydown: me.onLoginFormKeyDown
            },
            'loginPanel textfield[itemId=userId]': {
                keydown: me.onLoginFormKeyDown
            },
            'loginPanel textfield[itemId=password]': {
                keydown: me.onLoginFormKeyDown
            }
        });
    },


    display: function () {
        var me = this,
            loginElements = Ext.DomQuery.select('div[data-screen="login"]'),
            appSelectorElements = Ext.DomQuery.select('div[data-screen="app-selector"]');

        Ext.Array.forEach(appSelectorElements, function (el) {
            Ext.fly(el).setStyle('display', 'none');
        });

        Ext.Array.forEach(loginElements, function (el) {
            Ext.fly(el).setStyle('display', 'block');
        });
    },


    focusUserIdInput: function (loginPanel) {
        loginPanel.down('#userId').focus(false, 100);
    },


    onLoginFormKeyDown: function (field, evt) {
        if (evt.getKey() === evt.ENTER) {
            this.handleLoginSubmit();
        }
    },


    onLoginButtonClick: function () {
        this.handleLoginSubmit();
    },


    handleLoginSubmit: function () {
        var me = this,
            loginFormPanel = me.getLoginFormPanelView();

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
                            me.application.fireEvent('displayAppSelector');
                        }
                    }
                });
            },
            failure: function (form, action) {
                /**/
            }
        });
    }

});