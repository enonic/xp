Ext.define('Admin.view.FeedbackBox', {
    extend: 'Ext.Component',
    alias: 'widget.feedbackBox',
    autoHeight: true,
    floating: true,
    shadow: false,
    autoRender: true,
    width: 370,

    notifyOpts: undefined,

    tpl: '<div class="admin-notification-window clearfix">' +
         '	<table border="0" cellpadding="0" cellspacing="0">' +
         '		<tr>' +
         '			<td style="width: 48px;padding-top:4px;" valign="top">' +
         '				<img src="../admin/resources/images/icons/48x48/message.png" style="width:48px; height:48px"/>' +
         '			</td>' +
         '			<td valign="top" style="padding-left:15px">' +
         '				<h3>{messageTitle}</h3>' +
         '				<div class="message-text">{messageText}</div>' +
         '			</td>' +
         '     </tr>' +
         '		<tr>' +
         '		    <td colspan="2" style="text-align: right">' +
         '		        <tpl if="notifyUser">' +
         '					<span class="link notify-user" href="javascript:;">Send Notification</span>' +
         '             </tpl>' +
         '			</td>' +
         '     </tr>' +
         '	</table>' +
         '</div>',


    initComponent: function () {
        var me = this;

        me.callParent(arguments);
        me.update({});
    },


    afterRender: function () {
        var me = this;
        me.getEl().setOpacity(0);
        me.addClickListener();
        me.callParent(arguments);
    },


    doShow: function (title, message, opts) {
        var me = this;

        me.update({
            messageTitle: title,
            messageText: message,
            notifyUser: opts.notifyUser === undefined ? false : opts.notifyUser
        });
        me.setNotifyOpts(opts);
        me.show();
        me.fadeInOut();
    },


    fadeInOut: function () {
        var me = this;
        me.stopAnimation();
        me.center();
        me.animate(
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
                        me.setPosition(-5000, -5000);
                    },
                    scope: this
                }
            }
        );
    },


    hide: function () {
        var me = this;
        me.stopAnimation();
        me.getEl().setOpacity(0);
        me.setPosition(-5000, -5000);
    },


    addClickListener: function () {
        var me = this;

        me.getEl().on('mouseenter', function () {
            me.getActiveAnimation().paused = true;
        }, this);

        me.getEl().on('mouseleave', function () {
            me.getActiveAnimation().paused = false;
        }, this);

        me.getEl().on('click', function (event, target) {
            if (target.className.indexOf('notify-user') > -1) {
                var notifyOpts = me.getNotifyOpts();
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


    getCurrentUser: function () {
        return {
            "name": "mer",
            "displayName": "Morten Øien Eriksen",
            "key": "2AF735F668BB0B75F8AF886C4D304F049460EE43",
            "lastModified": "2010-03-15 16:00:02",
            "qualifiedName": "enonic\\mer",
            "email": "mer@enonic.com"
        };
    },


    onRender: function () {
        this.callParent(arguments);
    },


    setNotifyOpts: function (notifyOpts) {
        this.notifyOpts = notifyOpts;
    },


    getNotifyOpts: function () {
        return this.notifyOpts;
    }

});
