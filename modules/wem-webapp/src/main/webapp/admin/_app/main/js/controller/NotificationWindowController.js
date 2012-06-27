Ext.define('App.controller.NotificationWindowController', {
    extend: 'Ext.app.Controller',

    views: ['NotificationWindow'],

    init: function () {
        Ext.create('widget.notificationWindow');

        this.control({
            'notificationWindow': {
                afterrender: this.addWindowClickListener
            }
        });

        this.application.on(
            {
                'notifier.show': this.show,
                scope: this
            }
        );


    },

    show: function (title, message, opts) {
        this.getNotificationWindow().update(
            {
                messageTitle: title,
                messageText: message,
                notifyUser: opts.notifyUser === undefined ? false : opts.notifyUser
            }
        );
        this.getNotificationWindow().setNotifyOpts(opts);
        this.fadeWindowInOut();
    },

    fadeWindowInOut: function () {
        var self = this;
        var notificationWindow = this.getNotificationWindow();
        notificationWindow.show();
        notificationWindow.stopAnimation();
        notificationWindow.center();
        notificationWindow.animate(
            {
                duration: 300,
                from: {
                    opacity: 0
                },
                to: {
                    opacity: 1
                },
                easing: 'easeIn'
            }
        ).animate(
            {
                duration: 4000,
                from: {
                    opacity: 1
                },
                to: {
                    opacity: 1
                }
            }
        ).animate(
            {
                duration: 500,
                from: {
                    opacity: 1
                },
                to: {
                    opacity: 0
                },
                listeners: {
                    'afteranimate': function (t) {
                        notificationWindow.setPosition(-5000, -5000);
                    },
                    scope: this
                }
            }
        );
    },

    hide: function () {
        var notificationWindow = this.getNotificationWindow();
        notificationWindow.stopAnimation();
        notificationWindow.getEl().setOpacity(0);
        notificationWindow.setPosition(-5000, -5000);
    },

    addWindowClickListener: function () {
        var me = this;
        var notificationWindow = this.getNotificationWindow();

        notificationWindow.getEl().on('mouseenter', function () {
            notificationWindow.getActiveAnimation().paused = true;
        }, this);

        notificationWindow.getEl().on('mouseleave', function () {
            notificationWindow.getActiveAnimation().paused = false;
        }, this);

        notificationWindow.getEl().on('click', function (event, target) {
            if (target.className.indexOf('notify-user') > -1) {
                var notifyOpts = notificationWindow.getNotifyOpts();
                Ext.Ajax.request({
                    url: 'data/user/userinfo',
                    method: 'GET',
                    params: {key: notifyOpts.userKey},
                    success: function (response) {
                        var jsonObj = Ext.JSON.decode(response.responseText);
                        var model = {data: jsonObj};
                        if (notifyOpts.newUser) {
                            model.subject = "User Created";
                            model.message = Ext.String.format(Templates.common.notifyUserMessage,
                                jsonObj.displayName, jsonObj.name, jsonObj.userStore,
                                me.getCurrentUser().displayName);
                        } else {
                            model.subject = "User Updated";

                        }
                        me.application.fireEvent('showNotifyUserWindow ', model);
                    }
                });
            }

            this.hide();
        }, this);
    },

    getNotificationWindow: function () {
        var win = Ext.ComponentQuery.query('notificationWindow')[0];
        if (!win) {
            win = Ext.create('widget.notificationWindow');
        }
        return win;
    },

    getCurrentUser: function () {
        return {
            "name": "mer",
            "displayName": "Morten Øien Eriksen",
            "key": "2AF735F668BB0B75F8AF886C4D304F049460EE43",
            "lastModified": "2010-03-15 16:00:02",
            "qualifiedName": "enonic\\mer",
            "email": "mer@enonic.com"
        };
    }

});
