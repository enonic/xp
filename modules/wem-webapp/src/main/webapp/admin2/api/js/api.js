var API;
(function (API) {
    (function (util) {
        util.baseUri;
        function getAbsoluteUri(uri) {
            return this.baseUri + '/' + uri;
        }
        util.getAbsoluteUri = getAbsoluteUri;
    })(API.util || (API.util = {}));
    var util = API.util;
})(API || (API = {}));
var API;
(function (API) {
    (function (event) {
        var Event = (function () {
            function Event(name) {
                this.name = name;
            }
            Event.prototype.getName = function () {
                return this.name;
            };
            Event.prototype.fire = function () {
                event.fireEvent(this);
            };
            return Event;
        })();
        event.Event = Event;        
    })(API.event || (API.event = {}));
    var event = API.event;
})(API || (API = {}));
var API;
(function (API) {
    (function (event) {
        var bus = new Ext.util.Observable({
        });
        function onEvent(name, handler) {
            bus.on(name, handler);
        }
        event.onEvent = onEvent;
        function fireEvent(event) {
            bus.fireEvent(event.getName(), event);
        }
        event.fireEvent = fireEvent;
    })(API.event || (API.event = {}));
    var event = API.event;
})(API || (API = {}));
var API;
(function (API) {
    (function (notify) {
        (function (Type) {
            Type._map = [];
            Type._map[0] = "INFO";
            Type.INFO = 0;
            Type._map[1] = "ERROR";
            Type.ERROR = 1;
            Type._map[2] = "ACTION";
            Type.ACTION = 2;
        })(notify.Type || (notify.Type = {}));
        var Type = notify.Type;
        var Action = (function () {
            function Action(name, handler) {
                this.name = name;
                this.handler = handler;
            }
            Action.prototype.getName = function () {
                return this.name;
            };
            Action.prototype.getHandler = function () {
                return this.handler;
            };
            return Action;
        })();
        notify.Action = Action;        
        var Message = (function () {
            function Message(type, text) {
                this.type = type;
                this.text = text;
                this.actions = [];
            }
            Message.prototype.getType = function () {
                return this.type;
            };
            Message.prototype.getText = function () {
                return this.text;
            };
            Message.prototype.getActions = function () {
                return this.actions;
            };
            Message.prototype.addAction = function (name, handler) {
                this.actions.push(new Action(name, handler));
            };
            Message.prototype.send = function () {
                notify.sendNotification(this);
            };
            return Message;
        })();
        notify.Message = Message;        
        function newInfo(text) {
            return new Message(Type.INFO, text);
        }
        notify.newInfo = newInfo;
        function newError(text) {
            return new Message(Type.ERROR, text);
        }
        notify.newError = newError;
        function newAction(text) {
            return new Message(Type.ACTION, text);
        }
        notify.newAction = newAction;
    })(API.notify || (API.notify = {}));
    var notify = API.notify;
})(API || (API = {}));
var API;
(function (API) {
    (function (notify) {
        var space = 3;
        var lifetime = 5000;
        var slideDuration = 1000;
        var templates = {
            manager: new Ext.Template('<div class="admin-notification-container">', '   <div class="admin-notification-wrapper"></div>', '</div>'),
            notify: new Ext.Template('<div class="admin-notification" style="height: 0; opacity: 0;">', '   <div class="admin-notification-inner">', '       <a class="admin-notification-remove" href="#">X</a>', '       <div class="admin-notification-content">{message}</div>', '   </div>', '</div>')
        };
        var NotifyManager = (function () {
            function NotifyManager() {
                this.timers = {
                };
                this.render();
            }
            NotifyManager.prototype.render = function () {
                var template = templates.manager;
                var node = template.append(Ext.getBody());
                this.el = Ext.get(node);
                this.el.setStyle('bottom', 0);
                this.getWrapperEl().setStyle({
                    margin: 'auto'
                });
            };
            NotifyManager.prototype.getWrapperEl = function () {
                return this.el.first('.admin-notification-wrapper');
            };
            NotifyManager.prototype.notify = function (message) {
                var opts = notify.buildOpts(message);
                this.doNotify(opts);
            };
            NotifyManager.prototype.doNotify = function (opts) {
                var _this = this;
                var notificationEl = this.renderNotification(opts);
                var height = getInnerEl(notificationEl).getHeight();
                this.setListeners(notificationEl, opts);
                notificationEl.animate({
                    duration: slideDuration,
                    to: {
                        height: height + space,
                        opacity: 1
                    },
                    callback: function () {
                        _this.timers[notificationEl.id] = {
                            remainingTime: lifetime
                        };
                        _this.startTimer(notificationEl);
                    }
                });
            };
            NotifyManager.prototype.setListeners = function (el, opts) {
                var _this = this;
                el.on({
                    'click': {
                        fn: function () {
                            _this.remove(el);
                        },
                        stopEvent: true
                    },
                    'mouseover': function () {
                        _this.stopTimer(el);
                    },
                    'mouseleave': function () {
                        _this.startTimer(el);
                    }
                });
                if(opts.listeners) {
                    Ext.each(opts.listeners, function (listener) {
                        el.on({
                            'click': listener
                        });
                    });
                }
            };
            NotifyManager.prototype.remove = function (el) {
                if(!el) {
                    return;
                }
                el.animate({
                    duration: slideDuration,
                    to: {
                        height: 0,
                        opacity: 0
                    },
                    callback: function () {
                        Ext.removeNode(el.dom);
                    }
                });
                delete this.timers[el.id];
            };
            NotifyManager.prototype.startTimer = function (el) {
                var _this = this;
                var timer = this.timers[el.id];
                if(!timer) {
                    return;
                }
                timer.id = setTimeout(function () {
                    _this.remove(el);
                }, timer.remainingTime);
                timer.startTime = Date.now();
            };
            NotifyManager.prototype.stopTimer = function (el) {
                var timer = this.timers[el.id];
                if(!timer || !timer.id) {
                    return;
                }
                clearTimeout(timer.id);
                timer.id = null;
                timer.remainingTime -= Date.now() - timer.startTime;
            };
            NotifyManager.prototype.renderNotification = function (opts) {
                var style = {
                };
                var template = templates.notify;
                var notificationEl = template.append(this.getWrapperEl(), opts, true);
                if(opts.backgroundColor) {
                    style['backgroundColor'] = opts.backgroundColor;
                }
                style['marginTop'] = space + 'px';
                getInnerEl(notificationEl).setStyle(style);
                return notificationEl;
            };
            return NotifyManager;
        })();
        notify.NotifyManager = NotifyManager;        
        function getInnerEl(notificationEl) {
            return notificationEl.down('.admin-notification-inner');
        }
        var manager = new NotifyManager();
        function sendNotification(message) {
            manager.notify(message);
        }
        notify.sendNotification = sendNotification;
    })(API.notify || (API.notify = {}));
    var notify = API.notify;
})(API || (API = {}));
var API;
(function (API) {
    (function (notify) {
        var NotifyOpts = (function () {
            function NotifyOpts() { }
            return NotifyOpts;
        })();
        notify.NotifyOpts = NotifyOpts;        
        function buildOpts(message) {
            var opts = new NotifyOpts();
            if(message.getType() == notify.Type.ERROR) {
                opts.backgroundColor = 'red';
            } else if(message.getType() == notify.Type.ACTION) {
                opts.backgroundColor = '#669c34';
            }
            createHtmlMessage(message, opts);
            addListeners(message, opts);
            return opts;
        }
        notify.buildOpts = buildOpts;
        function addListeners(message, opts) {
            opts.listeners = [];
            var actions = message.getActions();
            for(var i = 0; i < actions.length; i++) {
                opts.listeners.push({
                    fn: actions[i].getHandler(),
                    delegate: 'notify_action_' + i,
                    stopEvent: true
                });
            }
        }
        function createHtmlMessage(message, opts) {
            var actions = message.getActions();
            opts.message = '<span>' + message.getText() + '</span>';
            if(actions.length > 0) {
                var linkHtml = '<span style="float: right; margin-left: 30px;">';
                for(var i = 0; i < actions.length; i++) {
                    if((i > 0) && (i == (actions.length - 1))) {
                        linkHtml += ' or ';
                    } else if(i > 0) {
                        linkHtml += ', ';
                    }
                    linkHtml += '<a href="#" class="notify_action_"' + i + '">';
                    linkHtml += actions[i].getName() + "</a>";
                }
                linkHtml += '</span>';
                opts.message = linkHtml + opts.message;
            }
        }
    })(API.notify || (API.notify = {}));
    var notify = API.notify;
})(API || (API = {}));
var API;
(function (API) {
    (function (notify) {
        function showFeedback(message) {
            notify.newInfo(message).send();
        }
        notify.showFeedback = showFeedback;
        function updateAppTabCount(appId, tabCount) {
        }
        notify.updateAppTabCount = updateAppTabCount;
    })(API.notify || (API.notify = {}));
    var notify = API.notify;
})(API || (API = {}));
Ext.Loader.setConfig({
    enabled: false,
    disableCaching: false
});
Ext.override(Ext.LoadMask, {
    floating: {
        shadow: false
    },
    msg: undefined,
    cls: 'admin-load-mask',
    msgCls: 'admin-load-text',
    maskCls: 'admin-mask-white'
});
//@ sourceMappingURL=api.js.map
