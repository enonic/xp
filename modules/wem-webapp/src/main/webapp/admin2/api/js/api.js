var api_util;
(function (api_util) {
    api_util.baseUri = '../../..';
    function getAbsoluteUri(uri) {
        return this.baseUri + '/' + uri;
    }

    api_util.getAbsoluteUri = getAbsoluteUri;
})(api_util || (api_util = {}));
var api_event;
(function (api_event) {
    var Event = (function () {
        function Event(name) {
            this.name = name;
        }

        Event.prototype.getName = function () {
            return this.name;
        };
        Event.prototype.fire = function () {
            api_event.fireEvent(this);
        };
        return Event;
    })();
    api_event.Event = Event;
})(api_event || (api_event = {}));
var api_event;
(function (api_event) {
    var bus = new Ext.util.Observable({
    });

    function onEvent(name, handler) {
        bus.on(name, handler);
    }

    api_event.onEvent = onEvent;
    function fireEvent(event) {
        bus.fireEvent(event.getName(), event);
    }

    api_event.fireEvent = fireEvent;
})(api_event || (api_event = {}));
var api_action;
(function (api_action) {
    var Action = (function () {
        function Action(label) {
            this.enabled = true;
            this.executionListeners = [];
            this.propertyChangeListeners = [];
            this.label = label;
        }

        Action.prototype.getLabel = function () {
            return this.label;
        };
        Action.prototype.setLabel = function (value) {
            if (value !== this.label) {
                this.label = value;
                for (var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        };
        Action.prototype.isEnabled = function () {
            return this.enabled;
        };
        Action.prototype.setEnabled = function (value) {
            if (value !== this.enabled) {
                this.enabled = value;
                for (var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        };
        Action.prototype.execute = function () {
            if (this.enabled) {
                for (var i in this.executionListeners) {
                    this.executionListeners[i](this);
                }
            }
        };
        Action.prototype.addExecutionListener = function (listener) {
            this.executionListeners.push(listener);
        };
        Action.prototype.addPropertyChangeListener = function (listener) {
            this.propertyChangeListeners.push(listener);
        };
        return Action;
    })();
    api_action.Action = Action;
})(api_action || (api_action = {}));
var api_ui;
(function (api_ui) {
    var HTMLElementHelper = (function () {
        function HTMLElementHelper(element) {
            this.el = element;
        }

        HTMLElementHelper.fromName = function fromName(name) {
            return new HTMLElementHelper(document.createElement(name));
        };
        HTMLElementHelper.prototype.getHTMLElement = function () {
            return this.el;
        };
        HTMLElementHelper.prototype.setDisabled = function (value) {
            this.el.disabled = value;
        };
        HTMLElementHelper.prototype.setId = function (value) {
            this.el.id = value;
        };
        HTMLElementHelper.prototype.setInnerHtml = function (value) {
            this.el.innerHTML = value;
        };
        HTMLElementHelper.prototype.addClass = function (clsName) {
            if (this.el.className === '') {
                this.el.className += clsName;
            } else {
                this.el.className += ' ' + clsName;
            }
        };
        HTMLElementHelper.prototype.addEventListener = function (eventName, f) {
            this.el.addEventListener(eventName, f);
        };
        HTMLElementHelper.prototype.appendChild = function (child) {
            this.el.appendChild(child);
        };
        return HTMLElementHelper;
    })();
    api_ui.HTMLElementHelper = HTMLElementHelper;
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var Component = (function () {
        function Component(name, elementName) {
            this.el = api_ui.HTMLElementHelper.fromName(elementName);
            this.id = name + '-' + (++Component.constructorCounter);
            this.el.setId(this.id);
        }

        Component.constructorCounter = 0;
        Component.prototype.getId = function () {
            return this.id;
        };
        Component.prototype.getEl = function () {
            return this.el;
        };
        Component.prototype.getHTMLElement = function () {
            return this.el.getHTMLElement();
        };
        Component.prototype.appendChild = function (child) {
            this.el.appendChild(child.getEl().getHTMLElement());
        };
        return Component;
    })();
    api_ui.Component = Component;
})(api_ui || (api_ui = {}));
var __extends = this.__extends || function (d, b) {
    function __() {
        this.constructor = d;
    }

    __.prototype = b.prototype;
    d.prototype = new __();
};
var api_ui_toolbar;
(function (api_ui_toolbar) {
    var Toolbar = (function (_super) {
        __extends(Toolbar, _super);
        function Toolbar() {
            _super.call(this, "toolbar", "div");
            this.components = [];
            this.getEl().addClass("toolbar");
            this.initExt();
        }

        Toolbar.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'north'
            });
        };
        Toolbar.prototype.addAction = function (action) {
            var button = this.doAddAction(action);
            this.appendChild(button);
        };
        Toolbar.prototype.addGreedySpacer = function () {
            var spacer = new ToolbarGreedySpacer();
            this.components.push(spacer);
        };
        Toolbar.prototype.doAddAction = function (action) {
            var button = new Button(action);
            if (this.hasGreedySpacer()) {
                button.setFloatRight(true);
            }
            this.components.push(button);
            return button;
        };
        Toolbar.prototype.hasGreedySpacer = function () {
            for (var i in this.components) {
                var comp = this.components[i];
                if (comp instanceof ToolbarGreedySpacer) {
                    return true;
                }
            }
            return false;
        };
        return Toolbar;
    })(api_ui.Component);
    api_ui_toolbar.Toolbar = Toolbar;
    var Button = (function (_super) {
        __extends(Button, _super);
        function Button(action) {
            var _this = this;
            _super.call(this, "button", "button");
            this.action = action;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.getEl().addEventListener("click", function () {
                _this.action.execute();
            });
            this.setEnable(action.isEnabled());
            action.addPropertyChangeListener(function (action) {
                _this.setEnable(action.isEnabled());
            });
        }

        Button.prototype.setEnable = function (value) {
            this.getEl().setDisabled(!value);
        };
        Button.prototype.setFloatRight = function (value) {
            if (value) {
                this.getEl().addClass('pull-right');
            }
        };
        return Button;
    })(api_ui.Component);
    var ToolbarGreedySpacer = (function () {
        function ToolbarGreedySpacer() {
        }

        return ToolbarGreedySpacer;
    })();
})(api_ui_toolbar || (api_ui_toolbar = {}));
var api_ui_menu;
(function (api_ui_menu) {
    var ContextMenu = (function (_super) {
        __extends(ContextMenu, _super);
        function ContextMenu() {
            _super.call(this, "context-menu", "ul");
            this.menuItems = [];
            this.getEl().addClass("context-menu");
            this.initExt();
        }

        ContextMenu.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'north'
            });
            this.ext.self.mixin('floating', Ext.util.Floating);
            this.ext.mixins.floating.constructor.call(this.ext);
        };
        ContextMenu.prototype.addAction = function (action) {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
        };
        ContextMenu.prototype.createMenuItem = function (action) {
            var menuItem = new MenuItem(this, action);
            this.menuItems.push(menuItem);
            return menuItem;
        };
        ContextMenu.prototype.showAt = function (x, y) {
            this.ext.showAt(x, y);
        };
        return ContextMenu;
    })(api_ui.Component);
    api_ui_menu.ContextMenu = ContextMenu;
    var MenuItem = (function (_super) {
        __extends(MenuItem, _super);
        function MenuItem(parent, action) {
            var _this = this;
            _super.call(this, "menu-item", "li");
            this.action = action;
            this.menu = parent;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.getEl().addEventListener("click", function () {
                _this.action.execute();
                _this.menu.ext.hide();
            });
            this.setEnable(action.isEnabled());
            action.addPropertyChangeListener(function (action) {
                _this.setEnable(action.isEnabled());
            });
        }

        MenuItem.prototype.setEnable = function (value) {
            this.getEl().setDisabled(!value);
        };
        return MenuItem;
    })(api_ui.Component);
})(api_ui_menu || (api_ui_menu = {}));
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
            manager: new Ext.Template('<div class="admin-notification-container">', '   <div class="admin-notification-wrapper"></div>',
                '</div>'),
            notify: new Ext.Template('<div class="admin-notification" style="height: 0; opacity: 0;">',
                '   <div class="admin-notification-inner">', '       <a class="admin-notification-remove" href="#">X</a>',
                '       <div class="admin-notification-content">{message}</div>', '   </div>', '</div>')
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
                if (opts.listeners) {
                    Ext.each(opts.listeners, function (listener) {
                        el.on({
                            'click': listener
                        });
                    });
                }
            };
            NotifyManager.prototype.remove = function (el) {
                if (!el) {
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
                if (!timer) {
                    return;
                }
                timer.id = setTimeout(function () {
                    _this.remove(el);
                }, timer.remainingTime);
                timer.startTime = Date.now();
            };
            NotifyManager.prototype.stopTimer = function (el) {
                var timer = this.timers[el.id];
                if (!timer || !timer.id) {
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
                if (opts.backgroundColor) {
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
            function NotifyOpts() {
            }

            return NotifyOpts;
        })();
        notify.NotifyOpts = NotifyOpts;
        function buildOpts(message) {
            var opts = new NotifyOpts();
            if (message.getType() == notify.Type.ERROR) {
                opts.backgroundColor = 'red';
            } else if (message.getType() == notify.Type.ACTION) {
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
            for (var i = 0; i < actions.length; i++) {
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
            if (actions.length > 0) {
                var linkHtml = '<span style="float: right; margin-left: 30px;">';
                for (var i = 0; i < actions.length; i++) {
                    if ((i > 0) && (i == (actions.length - 1))) {
                        linkHtml += ' or ';
                    } else if (i > 0) {
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
var api_content_data;
(function (api_content_data) {
    var DataId = (function () {
        function DataId(name, arrayIndex) {
            this.name = name;
            this.arrayIndex = arrayIndex;
            if (arrayIndex > 0) {
                this.refString = name + '[' + arrayIndex + ']';
            } else {
                this.refString = name;
            }
        }

        DataId.prototype.getName = function () {
            return this.name;
        };
        DataId.prototype.getArrayIndex = function () {
            return this.arrayIndex;
        };
        DataId.prototype.toString = function () {
            return this.refString;
        };
        DataId.from = function from(str) {
            var endsWithEndBracket = str.indexOf(']', str.length - ']'.length) !== -1;
            var containsStartBracket = str.indexOf('[') !== -1;
            if (endsWithEndBracket && containsStartBracket) {
                var firstBracketPos = str.indexOf('[');
                var nameStr = str.substring(0, firstBracketPos);
                var indexStr = str.substring(nameStr.length + 1, (str.length - 1));
                var index = parseInt(indexStr);
                return new DataId(nameStr, index);
            } else {
                return new DataId(str, 0);
            }
        };
        return DataId;
    })();
    api_content_data.DataId = DataId;
})(api_content_data || (api_content_data = {}));
var api_content_data;
(function (api_content_data) {
    var Data = (function () {
        function Data(name) {
            this.name = name;
        }

        Data.prototype.setArrayIndex = function (value) {
            this.arrayIndex = value;
        };
        Data.prototype.setParent = function (parent) {
            this.parent = parent;
        };
        Data.prototype.getId = function () {
            return new api_content_data.DataId(this.name, this.arrayIndex);
        };
        Data.prototype.getName = function () {
            return this.name;
        };
        Data.prototype.getParent = function () {
            return this.parent;
        };
        Data.prototype.getArrayIndex = function () {
            return this.arrayIndex;
        };
        return Data;
    })();
    api_content_data.Data = Data;
})(api_content_data || (api_content_data = {}));
var api_content_data;
(function (api_content_data) {
    var DataSet = (function (_super) {
        __extends(DataSet, _super);
        function DataSet(name) {
            _super.call(this, name);
            this.dataById = {
            };
        }

        DataSet.prototype.nameCount = function (name) {
            var count = 0;
            for (var i in this.dataById) {
                var data = this.dataById[i];
                if (data.getName() === name) {
                    count++;
                }
            }
            return count;
        };
        DataSet.prototype.addData = function (data) {
            data.setParent(this);
            var index = this.nameCount(data.getName());
            data.setArrayIndex(index);
            var dataId = new api_content_data.DataId(data.getName(), index);
            this.dataById[dataId.toString()] = data;
        };
        DataSet.prototype.getData = function (dataId) {
            return this.dataById[api_content_data.DataId.from(dataId).toString()];
        };
        return DataSet;
    })(api_content_data.Data);
    api_content_data.DataSet = DataSet;
})(api_content_data || (api_content_data = {}));
var api_content_data;
(function (api_content_data) {
    var ContentData = (function (_super) {
        __extends(ContentData, _super);
        function ContentData() {
            _super.call(this, "");
        }

        return ContentData;
    })(api_content_data.DataSet);
    api_content_data.ContentData = ContentData;
})(api_content_data || (api_content_data = {}));
var api_content_data;
(function (api_content_data) {
    var Property = (function (_super) {
        __extends(Property, _super);
        function Property(name, value, type) {
            _super.call(this, name);
            this.value = value;
            this.type = type;
        }

        Property.from = function from(json) {
            return new Property(json.name, json.value, json.type);
        };
        Property.prototype.getValue = function () {
            return this.value;
        };
        Property.prototype.getType = function () {
            return this.type;
        };
        Property.prototype.setValue = function (value) {
            this.value = value;
        };
        return Property;
    })(api_content_data.Data);
    api_content_data.Property = Property;
})(api_content_data || (api_content_data = {}));
var api_schema_content_form;
(function (api_schema_content_form) {
    var FormItem = (function () {
        function FormItem(name) {
            this.name = name;
        }

        FormItem.prototype.getName = function () {
            return this.name;
        };
        return FormItem;
    })();
    api_schema_content_form.FormItem = FormItem;
})(api_schema_content_form || (api_schema_content_form = {}));
var api_schema_content_form;
(function (api_schema_content_form) {
    var InputType = (function () {
        function InputType(json) {
            this.name = json.name;
        }

        InputType.prototype.getName = function () {
            return this.name;
        };
        return InputType;
    })();
    api_schema_content_form.InputType = InputType;
})(api_schema_content_form || (api_schema_content_form = {}));
var api_schema_content_form;
(function (api_schema_content_form) {
    var Input = (function (_super) {
        __extends(Input, _super);
        function Input(json) {
            _super.call(this, json.name);
            this.inputType = new api_schema_content_form.InputType(json.type);
            this.label = json.label;
            this.immutable = json.immutable;
            this.occurrences = new api_schema_content_form.Occurrences(json.occurrences);
            this.indexed = json.indexed;
            this.customText = json.customText;
            this.validationRegex = json.validationRegexp;
            this.helpText = json.helpText;
        }

        Input.prototype.getLabel = function () {
            return this.label;
        };
        Input.prototype.isImmutable = function () {
            return this.immutable;
        };
        Input.prototype.getOccurrences = function () {
            return this.occurrences;
        };
        Input.prototype.isIndexed = function () {
            return this.indexed;
        };
        Input.prototype.getCustomText = function () {
            return this.customText;
        };
        Input.prototype.getValidationRegex = function () {
            return this.validationRegex;
        };
        Input.prototype.getHelpText = function () {
            return this.helpText;
        };
        return Input;
    })(api_schema_content_form.FormItem);
    api_schema_content_form.Input = Input;
})(api_schema_content_form || (api_schema_content_form = {}));
var api_schema_content_form;
(function (api_schema_content_form) {
    var Occurrences = (function () {
        function Occurrences(json) {
            this.minimum = json.minimum;
            this.maximum = json.maximum;
        }

        return Occurrences;
    })();
    api_schema_content_form.Occurrences = Occurrences;
})(api_schema_content_form || (api_schema_content_form = {}));
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
