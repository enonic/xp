var admin;
(function (admin) {
    (function (api) {
        (function (message) {
            var messageBus = Ext.create('Ext.util.Observable');
            function showFeedback(message) {
                messageBus.fireEvent('showNotification', 'notify', message);
            }
            message.showFeedback = showFeedback;
            function updateAppTabCount(appId, tabCount) {
                var eventName = 'topBar.onUpdateAppTabCount';
                var config = {
                    appId: appId,
                    tabCount: tabCount
                };
                messageBus.fireEvent(eventName, config);
            }
            message.updateAppTabCount = updateAppTabCount;
            function addListener(name, func, scope) {
                messageBus.addListener(name, func, scope);
            }
            message.addListener = addListener;
        })(api.message || (api.message = {}));
        var message = api.message;
    })(admin.api || (admin.api = {}));
    var api = admin.api;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (api) {
        (function (notify) {
            var NotificationManager = (function () {
                function NotificationManager() {
                    this.position = 'b';
                    this.space = 3;
                    this.lifetime = 5000;
                    this.slideDuration = 1000;
                    this.tpl = {
                        manager: new Ext.Template('<div class="admin-notification-container">', '   <div class="admin-notification-wrapper"></div>', '</div>'),
                        notification: new Ext.Template('<div class="admin-notification" style="height: 0; opacity: 0;">', '   <div class="admin-notification-inner">', '       <a class="admin-notification-remove" href="#">X</a>', '       <div class="admin-notification-content">{message}</div>', '   </div>', '</div>'),
                        error: new Ext.Template('<span>{message}</span>'),
                        publish: new Ext.XTemplate('<span style="float: right; margin-left: 30px;"><a href="#" class="admin-notification-result">See result</a> or <a href="#" class="admin-notification-publish">Publish to other locations</a></span>', '<span style="line-height: 1.5em;">', '<tpl if="contentName"> "{contentName}"</tpl> published successfully!', '</span> '),
                        general: new Ext.XTemplate('<span style="float: right; margin-left: 30px;"><a href="#" class="admin-notification-publish">Publish</a> or <a href="#" class="admin-notification-close">Close</a></span>', '<span style="line-height: 1.5em;">', '<tpl if="contentName"> "{contentName}"</tpl> saved successfully!', '</span> ')
                    };
                    console.log('notification manager construct');
                    this.timers = {
                    };
                    this.render();
                    admin.api.message.addListener('showNotification', this.showNotification, this);
                    admin.api.message.addListener('removeNotification', this.removeNotification, this);
                }
                NotificationManager.prototype.render = function () {
                    var me = this, node, pos = this.position;
                    node = this.tpl.manager.append(Ext.getBody());
                    this.el = Ext.get(node);
                    (pos[0] == 't') || (pos[0] == 'b') || (this.position = pos = 'b');
                    this.getEl().setStyle(((pos[0] == 't') ? 'top' : 'bottom'), 0);
                    this.getWrapperEl().setStyle({
                        margin: 'auto'
                    });
                };
                NotificationManager.prototype.showNotification = function (type, args, opts) {
                    var me = this;
                    me[type] ? me[type](args) : me.notify({
                    });
                };
                NotificationManager.prototype.removeNotification = function (mark) {
                    var me = this, notifications = Ext.select('.admin-notification[data-mark=' + mark + ']');
                    notifications.each(function (notificationEl) {
                        me.remove(notificationEl);
                    });
                };
                NotificationManager.prototype.notify = function (nOpts) {
                    var me = this, notificationEl, height;
                    if(me.isRendered(nOpts)) {
                        return;
                    }
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
                            if(nOpts.lifetime < 0) {
                                return;
                            }
                            me.timers[notificationEl.id] = {
                                remainingTime: (nOpts.lifetime || me.lifetime)
                            };
                            me.startTimer(notificationEl);
                        }
                    });
                };
                NotificationManager.prototype.error = function (message) {
                    var defaultMessage = 'Lost connection to server - Please wait until connection is restored', opts;
                    opts = Ext.apply({
                        message: defaultMessage,
                        backgroundColor: 'red'
                    }, Ext.isString(message) ? {
                        message: message
                    } : message);
                    opts.message = this.tpl.error.apply(opts);
                    this.notify(opts);
                };
                NotificationManager.prototype.general = function (opts) {
                    var notificationOpts = {
                        message: this.tpl.general.apply(opts),
                        backgroundColor: '#4294de',
                        listeners: []
                    };
                    if(Ext.isFunction(opts.resultCallback)) {
                        notificationOpts.listeners.push({
                            click: {
                                fn: opts.resultCallback,
                                delegate: '.admin-notification-result',
                                stopEvent: true
                            }
                        });
                    }
                    if(Ext.isFunction(opts.publishCallback)) {
                        notificationOpts.listeners.push({
                            click: {
                                fn: opts.publishCallback,
                                delegate: '.admin-notification-publish',
                                stopEvent: true
                            }
                        });
                    }
                    this.notify(notificationOpts);
                };
                NotificationManager.prototype.publish = function (opts) {
                    var notificationOpts = {
                        message: this.tpl.publish.apply(opts),
                        backgroundColor: '#669c34',
                        listeners: []
                    };
                    if(Ext.isFunction(opts.publishCallback)) {
                        notificationOpts.listeners.push({
                            click: {
                                fn: opts.publishCallback,
                                delegate: '.admin-notification-publish',
                                stopEvent: true
                            }
                        });
                    }
                    if(Ext.isFunction(opts.closeCallback)) {
                        notificationOpts.listeners.push({
                            click: {
                                fn: opts.closeCallback,
                                delegate: '.admin-notification-close',
                                stopEvent: true
                            }
                        });
                    }
                    this.notify(notificationOpts);
                };
                NotificationManager.prototype.isRendered = function (nOpts) {
                    if(nOpts.single && nOpts.mark && this.getEl().select('.admin-notification[data-mark=' + nOpts.mark + ']').getCount() > 0) {
                        return true;
                    }
                    return false;
                };
                NotificationManager.prototype.renderNotification = function (nOpts) {
                    var me = this, tpl = me.tpl.notification, style = {
                    }, notificationEl;
                    notificationEl = (me.position[0] == 't') ? tpl.insertFirst(me.getWrapperEl(), nOpts, true) : tpl.append(me.getWrapperEl(), nOpts, true);
                    if(nOpts.backgroundColor) {
                        style['backgroundColor'] = nOpts.backgroundColor;
                    }
                    (me.position[0] == 't') ? (style['marginBottom'] = me.space + 'px') : (style['marginTop'] = me.space + 'px');
                    me.getInnerEl(notificationEl).setStyle(style);
                    if(nOpts.mark) {
                        notificationEl.set({
                            'data-mark': nOpts.mark
                        });
                    }
                    return notificationEl;
                };
                NotificationManager.prototype.setNotificationListeners = function (notificationEl, nOpts) {
                    var me = this;
                    notificationEl.on({
                        click: {
                            fn: function () {
                                me.remove(notificationEl);
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
                    if(nOpts.listeners) {
                        Ext.each(nOpts.listeners, function (listener) {
                            notificationEl.on(listener);
                        });
                    }
                };
                NotificationManager.prototype.remove = function (notificationEl) {
                    var me = this;
                    Ext.isElement(notificationEl) || (notificationEl = Ext.get(notificationEl));
                    if(!notificationEl) {
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
                        }
                    });
                    delete me.timers[notificationEl.id];
                };
                NotificationManager.prototype.startTimer = function (notificationEl) {
                    var me = this, timer = me.timers[notificationEl.id];
                    if(!timer) {
                        return;
                    }
                    timer.id = setTimeout(function () {
                        me.remove(notificationEl);
                    }, timer.remainingTime);
                    timer.startTime = Date.now();
                };
                NotificationManager.prototype.stopTimer = function (notificationEl) {
                    var timer = this.timers[notificationEl.id];
                    if(!timer || !timer.id) {
                        return;
                    }
                    clearTimeout(timer.id);
                    timer.id = null;
                    timer.remainingTime -= Date.now() - timer.startTime;
                };
                NotificationManager.prototype.getEl = function () {
                    return this.el;
                };
                NotificationManager.prototype.getWrapperEl = function () {
                    return this.el.first('.admin-notification-wrapper');
                };
                NotificationManager.prototype.getInnerEl = function (notificationEl) {
                    return notificationEl.down('.admin-notification-inner');
                };
                NotificationManager.prototype.getNotificationEl = function (node) {
                    Ext.isElement(node) || (node = Ext.get(node));
                    return node.up('.admin-notification', this.getEl());
                };
                return NotificationManager;
            })();            
            var manager = new NotificationManager();
        })(api.notify || (api.notify = {}));
        var notify = api.notify;
    })(admin.api || (admin.api = {}));
    var api = admin.api;
})(admin || (admin = {}));
var admin;
(function (admin) {
    })(admin || (admin = {}));
//@ sourceMappingURL=api.js.map
