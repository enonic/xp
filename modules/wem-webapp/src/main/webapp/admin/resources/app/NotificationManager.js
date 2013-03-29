Ext.define('Admin.NotificationManager', {
    singleton: true,

    /**
     * Position inside document body.
     * Available: 'tl', 't', 'tr', 'bl', 'b', 'br'.
     * Default is 'b'.
     */
    position: 'b',

    /**
     *    Space betwean notification items
     */
    space: 3,

    lifetime: 5000,
    slideDuration: 500,

    tpl: {
        manager: new Ext.Template(
            '<div class="admin-notification-container">',
            '<div class="admin-notification-wrapper"></div>',
            '</di>'
        ),

        notification: new Ext.Template(
            '<div class="admin-notification" style="height: 0; opacity: 0;">',
            '<div class="admin-notification-inner">',
            '<a class="admin-notification-remove" href="#">X</a>',
            '<div class="admin-notification-content">{message}</div>',
            '</div>',
            '</div>'
        ),

        error: new Ext.Template(
            '<span>{message}</span>'
        ),

        general: new Ext.Template(
            '<span style="float: right; margin-left: 20px;"><a href="#" class="admin-notification-result">See result</a> or <a href="#" class="admin-notification-publish">Publish to other locations</a></span>',
            '<span style="line-height: 1.5em;">Content "{contentName}" published successfully!</span> '
        ),

        publish: new Ext.Template(
            '<span style="float: right; margin-left: 20px;"><a href="#" class="admin-notification-publish">Publish</a> or <a href="#" class="admin-notification-close">Close</a></span>',
            '<span style="line-height: 1.5em;">Content "{contentName}" saved successfully!</span> '
        )
    },

    constructor: function (config) {
        // hash stored timers for active notifications
        this.timers = {};

        this.render();
    },

    render: function () {
        var me = this,
            node,
            pos = me.position;
        // render manager template to document body
        node = me.tpl.manager.append(Ext.getBody());
        me.el = Ext.get(node);

        // check position or set to default
        (pos[0] == 't') || (pos[0] == 'b') || (me.position = pos = 'b');
        // align verticaly
        me.getEl().setStyle((pos[0] == 't') ? { top: 0 } : { bottom: 0 });
        // align horizontaly
        me.getWrapperEl().setStyle((pos[1] != 'r') ? (pos[1] == 'l') ? { marginLeft: 0 } : { margin: 'auto' } : { marginRight: 0 });
    },

    getEl: function () {
        return this.el;
    },

    getWrapperEl: function () {
        return this.el.first('.admin-notification-wrapper');
    },

    getInnerEl: function (notificationEl) {
        return notificationEl.down('.admin-notification-inner');
    },

    getNotificationEl: function (node) {
        Ext.isElement(node) || (node = window.top.Ext.get(node));
        return node.up('.admin-notification', this.getEl());
    },

    /**
     *    Acceptable options:
     *    message
     *    backgroundColor - css color
     *    listeners - object or array of objects passed to Ext.Element.addListener,
     *    onRemove - callback function called on notification removing,
     *  lifetime
     *
     *  Returns notification id
     */
    notify: function (nOpts) {
        var me = this,
            notificationEl,
            height;

        notificationEl = me.renderNotification(nOpts);
        me.setNotificationListeners(notificationEl, nOpts);

        height = me.getInnerEl(notificationEl).getHeight();
        notificationEl.animate({
            duration: me.slideDuration,
            to: {
                height: height + me.space,
                opacity: 1
            },
            callback: function () {
                if (nOpts.lifetime < 0) {
                    return;
                }

                me.timers[notificationEl.id] = {
                    remainingTime: (nOpts.lifetime || me.lifetime),
                    callback: nOpts.onRemove
                };
                me.startTimer(notificationEl);
            }
        });

        return notificationEl.id;
    },

    /**
     *     Returns notification Id
     */
    error: function (message) {
        var opts = {
            message: this.tpl.error.apply({message: message || 'Lost connection to server - Please wait until connection restorred'}),
            backgroundColor: 'red'
        };

        return this.notify(opts);
    },

    /**
     *     Returns notification id
     */
    general: function (contentName, resultCallback, publishCallback) {
        var opts = {
            message: this.tpl.general.apply(contentName),
            backgroundColor: 'green',
            listeners: []
        };

        if (Ext.isFunction(resultCallback)) {
            opts.listeners.push({
                click: {
                    fn: resultCallback,
                    delegate: '.admin-notification-result',
                    stopEvent: true
                }
            });
        }
        if (Ext.isFunction(publishCallback)) {
            opts.listeners.push({
                click: {
                    fn: publishCallback,
                    delegate: '.admin-notification-publish',
                    stopEvent: true
                }
            });
        }

        return this.notify(opts);
    },

    /**
     *  Returns notification id
     */
    publish: function (contentName, publishCallback, closeCallback) {
        var opts = {
            message: this.tpl.publish.apply(contentName),
            backgroundColor: 'blue',
            listeners: []
        };

        if (Ext.isFunction(publishCallback)) {
            opts.listeners.push({
                click: {
                    fn: publishCallback,
                    delegate: '.admin-notification-publish',
                    stopEvent: true
                }
            });
        }
        if (Ext.isFunction(closeCallback)) {
            opts.listeners.push({
                click: {
                    fn: closeCallback,
                    delegate: '.admin-notification-close',
                    stopEvent: true
                }
            });
        }

        return this.notify(opts);
    },

    renderNotification: function (nOpts) {
        var me = this,
            tpl = me.tpl.notification,
            style = {},
            notificationEl;

        // create notification DOM element
        notificationEl = (me.position[0] == 't')
            ? tpl.insertFirst(me.getWrapperEl(), nOpts, true)
            : tpl.append(me.getWrapperEl(), nOpts, true);

        // set notification style
        nOpts.backgroundColor && (style.backgroundColor = nOpts.backgroundColor);
        (me.position[0] == 't')
            ? (style.marginBottom = me.space + 'px')
            : (style.marginTop = me.space + 'px');
        me.getInnerEl(notificationEl).setStyle(style);

        return notificationEl;
    },

    setNotificationListeners: function (notificationEl, nOpts) {
        var me = this;

        // set default listeners
        notificationEl.on({
            click: {
                fn: function () {
                    me.remove(notificationEl, nOpts.onRemove);
                },
                delegate: '.admin-notification-remove',
                stopEvent: true
            },
            mouseover: function () {
                me.stopTimer(notificationEl);
            },
            mouseleave: function () {
                me.startTimer(notificationEl);
            }
        });

        if (nOpts.listeners) {
            Ext.each(nOpts.listeners, function (listener) {
                notificationEl.on(listener);
            });
        }
    },

    remove: function (notificationEl, callback) {
        var me = this;

        Ext.isElement(notificationEl) || (notificationEl = Ext.get(notificationEl));

        if (!notificationEl) {
            return;
        }

        notificationEl.animate({
            duration: me.slideDuration,
            to: {
                height: 0,
                opacity: 0
            },
            callback: function () {
                Ext.removeNode(notificationEl.dom);
                if (callback) {
                    callback(notificationEl);
                }
            }
        });

        delete me.timers[notificationEl.id];
    },

    startTimer: function (notificationEl) {
        var me = this,
            timer = me.timers[notificationEl.id];

        if (!timer) {
            return;
        }

        timer.id = setTimeout(
            function () {
                me.remove(notificationEl, timer.callback);
            },
            timer.remainingTime
        );

        timer.startTime = Date.now();
    },

    stopTimer: function (notificationEl) {
        var timer = this.timers[notificationEl.id];

        if (!timer) {
            return;
        }

        clearTimeout(timer.id);

        timer.remainingTime -= Date.now() - timer.startTime;
    }
});