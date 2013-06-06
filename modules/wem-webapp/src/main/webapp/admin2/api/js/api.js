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
            if(value !== this.label) {
                this.label = value;
                for(var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        };
        Action.prototype.isEnabled = function () {
            return this.enabled;
        };
        Action.prototype.setEnabled = function (value) {
            if(value !== this.enabled) {
                this.enabled = value;
                for(var i in this.propertyChangeListeners) {
                    this.propertyChangeListeners[i](this);
                }
            }
        };
        Action.prototype.execute = function () {
            if(this.enabled) {
                for(var i in this.executionListeners) {
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
            if(!this.hasClass(clsName)) {
                if(this.el.className === '') {
                    this.el.className += clsName;
                } else {
                    this.el.className += ' ' + clsName;
                }
            }
        };
        HTMLElementHelper.prototype.hasClass = function (clsName) {
            return this.el.className.match(new RegExp('(\\s|^)' + clsName + '(\\s|$)')) !== null;
        };
        HTMLElementHelper.prototype.removeClass = function (clsName) {
            if(this.hasClass(clsName)) {
                var reg = new RegExp('(\\s|^)' + clsName + '(\\s|$)');
                this.el.className = this.el.className.replace(reg, '');
            }
        };
        HTMLElementHelper.prototype.addEventListener = function (eventName, f) {
            this.el.addEventListener(eventName, f);
        };
        HTMLElementHelper.prototype.appendChild = function (child) {
            this.el.appendChild(child);
        };
        HTMLElementHelper.prototype.setDisplay = function (value) {
            this.el.style.display = value;
        };
        HTMLElementHelper.prototype.setPosition = function (value) {
            this.el.style.position = value;
        };
        HTMLElementHelper.prototype.setWidth = function (value) {
            this.el.style.width = value;
        };
        HTMLElementHelper.prototype.setHeight = function (value) {
            this.el.style.height = value;
        };
        HTMLElementHelper.prototype.setTop = function (value) {
            this.el.style.top = value;
        };
        HTMLElementHelper.prototype.setLeft = function (value) {
            this.el.style.left = value;
        };
        HTMLElementHelper.prototype.setMarginLeft = function (value) {
            this.el.style.marginLeft = value;
        };
        HTMLElementHelper.prototype.setMarginRight = function (value) {
            this.el.style.marginRight = value;
        };
        HTMLElementHelper.prototype.setMarginTop = function (value) {
            this.el.style.marginTop = value;
        };
        HTMLElementHelper.prototype.setMarginBottom = function (value) {
            this.el.style.marginBottom = value;
        };
        HTMLElementHelper.prototype.setZindex = function (value) {
            this.el.style.zIndex = value.toString();
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
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var api_ui;
(function (api_ui) {
    var BodyMask = (function (_super) {
        __extends(BodyMask, _super);
        function BodyMask() {
                _super.call(this, "Mask", "div");
            this.getEl().setDisplay("none");
            this.getEl().addClass("body-mask");
            this.getEl().setZindex(30000);
            document.body.appendChild(this.getHTMLElement());
        }
        BodyMask.instance = new BodyMask();
        BodyMask.get = function get() {
            return BodyMask.instance;
        };
        BodyMask.prototype.activate = function () {
            this.getEl().setDisplay("block");
        };
        BodyMask.prototype.deActivate = function () {
            this.getEl().setDisplay("none");
        };
        return BodyMask;
    })(api_ui.Component);
    api_ui.BodyMask = BodyMask;    
})(api_ui || (api_ui = {}));
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
            if(this.hasGreedySpacer()) {
                button.setFloatRight(true);
            }
            this.components.push(button);
            return button;
        };
        Toolbar.prototype.hasGreedySpacer = function () {
            for(var i in this.components) {
                var comp = this.components[i];
                if(comp instanceof ToolbarGreedySpacer) {
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
            this.getEl().addEventListener("click", function (evt) {
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
            if(value) {
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
    var MenuItem = (function (_super) {
        __extends(MenuItem, _super);
        function MenuItem(menu, action) {
            var _this = this;
                _super.call(this, "menu-item", "li");
            this.action = action;
            this.menu = menu;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.getEl().addEventListener("click", function () {
                if(action.isEnabled()) {
                    _this.action.execute();
                }
            });
            this.setEnable(action.isEnabled());
            action.addPropertyChangeListener(function (action) {
                _this.setEnable(action.isEnabled());
            });
        }
        MenuItem.prototype.setEnable = function (value) {
            var el = this.getEl();
            el.setDisabled(!value);
            if(value) {
                el.removeClass("context-menu-item-disabled");
            } else {
                el.addClass("context-menu-item-disabled");
            }
        };
        return MenuItem;
    })(api_ui.Component);
    api_ui_menu.MenuItem = MenuItem;    
})(api_ui_menu || (api_ui_menu = {}));
var api_ui_menu;
(function (api_ui_menu) {
    var ContextMenu = (function (_super) {
        __extends(ContextMenu, _super);
        function ContextMenu() {
            var _this = this;
                _super.call(this, "context-menu", "ul");
            this.menuItems = [];
            this.getEl().addClass("context-menu");
            this.initExt();
            window.document.addEventListener("click", function (evt) {
                _this.onDocumentClick(evt);
            });
        }
        ContextMenu.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'north',
                shadow: false
            });
            this.ext.self.mixin('floating', Ext.util.Floating);
            this.ext.mixins.floating.constructor.call(this.ext);
        };
        ContextMenu.prototype.addAction = function (action) {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
        };
        ContextMenu.prototype.createMenuItem = function (action) {
            var _this = this;
            var menuItem = new api_ui_menu.MenuItem(this, action);
            menuItem.getEl().addEventListener("click", function (evt) {
                _this.hide();
            });
            this.menuItems.push(menuItem);
            return menuItem;
        };
        ContextMenu.prototype.showAt = function (x, y) {
            this.ext.showAt(x, y);
        };
        ContextMenu.prototype.hide = function () {
            this.ext.hide();
        };
        ContextMenu.prototype.onDocumentClick = function (evt) {
            var id = this.getId();
            var target = evt.target;
            for(var element = target; element; element = element.parentNode) {
                if(element.id === id) {
                    return;
                }
            }
            this.hide();
        };
        return ContextMenu;
    })(api_ui.Component);
    api_ui_menu.ContextMenu = ContextMenu;    
})(api_ui_menu || (api_ui_menu = {}));
var api_ui_menu;
(function (api_ui_menu) {
    var ActionMenu = (function (_super) {
        __extends(ActionMenu, _super);
        function ActionMenu() {
            var _this = this;
            var actions = [];
            for (var _i = 0; _i < (arguments.length - 0); _i++) {
                actions[_i] = arguments[_i + 0];
            }
                _super.call(this, "action-menu", "ul");
            this.menuItems = [];
            this.getEl().addClass("action-menu");
            this.button = new ActionMenuButton("Actions", this);
            for(var i = 0; i < actions.length; i++) {
                this.addAction(actions[i]);
            }
            window.document.addEventListener("click", function (evt) {
                _this.onDocumentClick(evt);
            });
            this.initExt();
        }
        ActionMenu.prototype.addAction = function (action) {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
        };
        ActionMenu.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
            this.ext.self.mixin('floating', Ext.util.Floating);
            this.ext.mixins.floating.constructor.call(this.ext);
        };
        ActionMenu.prototype.createMenuItem = function (action) {
            var _this = this;
            var menuItem = new api_ui_menu.MenuItem(this, action);
            menuItem.getEl().addEventListener("click", function (evt) {
                _this.hide();
            });
            this.menuItems.push(menuItem);
            return menuItem;
        };
        ActionMenu.prototype.hide = function () {
            this.ext.hide();
        };
        ActionMenu.prototype.onDocumentClick = function (evt) {
            var id = this.getId();
            var target = evt.target;
            for(var element = target; element; element = element.parentNode) {
                if(element.id === id) {
                    return;
                }
            }
            this.hide();
        };
        return ActionMenu;
    })(api_ui.Component);
    api_ui_menu.ActionMenu = ActionMenu;    
    var ActionMenuButton = (function (_super) {
        __extends(ActionMenuButton, _super);
        function ActionMenuButton(label, menu) {
                _super.call(this, "button", "button");
            this.menu = menu;
            var btn = this;
            var btnEl = this.getEl();
            btnEl.addClass("action-menu-button");
            var em = api_ui.HTMLElementHelper.fromName('em');
            em.setInnerHtml(label);
            btnEl.appendChild(em.getHTMLElement());
            btnEl.addEventListener("click", function (e) {
                menu.ext.show();
                menu.ext.getEl().alignTo(btn.ext.getEl(), 'tl-bl?', [
                    -2, 
                    0
                ]);
                if(e.stopPropagation) {
                    e.stopPropagation();
                }
                e.cancelBubble = true;
            });
            this.initExt();
        }
        ActionMenuButton.prototype.setEnabled = function (value) {
            this.getEl().setDisabled(!value);
        };
        ActionMenuButton.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        };
        return ActionMenuButton;
    })(api_ui.Component);
    api_ui_menu.ActionMenuButton = ActionMenuButton;    
})(api_ui_menu || (api_ui_menu = {}));
var api_ui_dialog;
(function (api_ui_dialog) {
    var DialogButton = (function (_super) {
        __extends(DialogButton, _super);
        function DialogButton(action) {
            var _this = this;
                _super.call(this, "DialogButton", "button");
            this.getEl().addClass("DialogButton");
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
        DialogButton.prototype.setEnable = function (value) {
            this.getEl().setDisabled(!value);
        };
        return DialogButton;
    })(api_ui.Component);
    api_ui_dialog.DialogButton = DialogButton;    
})(api_ui_dialog || (api_ui_dialog = {}));
var api_ui_dialog;
(function (api_ui_dialog) {
    var ModalDialog = (function (_super) {
        __extends(ModalDialog, _super);
        function ModalDialog(title) {
            var _this = this;
                _super.call(this, "ModalDialog", "div");
            this.width = 700;
            this.height = 500;
            this.getEl().setZindex(30001);
            this.getEl().addClass("modal-dialog");
            this.getEl().addClass("display-none");
            this.getEl().setWidth(this.width + "px");
            this.getEl().setHeight(this.height + "px");
            this.getEl().setPosition("fixed");
            this.getEl().setTop("50%");
            this.getEl().setLeft("50%");
            this.getEl().setMarginLeft("-" + (this.width / 2) + "px");
            this.getEl().setMarginTop("-" + (this.height / 2) + "px");
            this.title = new ModalDialogTitle(title);
            this.appendChild(this.title);
            this.contentPanel = new ModalDialogContentPanel();
            this.appendChild(this.contentPanel);
            this.closeAction = new api_action.Action("Close");
            this.closeAction.addExecutionListener(function () {
                _this.close();
            });
        }
        ModalDialog.prototype.addToButtonRow = function (comp) {
            this.buttonRow.appendChild(comp);
        };
        ModalDialog.prototype.close = function () {
            api_ui.BodyMask.get().deActivate();
            this.getEl().removeClass("display-block");
            this.getEl().addClass("display-none");
            Mousetrap.unbind('esc');
        };
        ModalDialog.prototype.open = function () {
            var _this = this;
            api_ui.BodyMask.get().activate();
            this.getEl().removeClass("display-none");
            this.getEl().addClass("display-block");
            Mousetrap.bind('esc', function () {
                _this.close();
            });
        };
        return ModalDialog;
    })(api_ui.Component);
    api_ui_dialog.ModalDialog = ModalDialog;    
    var ModalDialogTitle = (function (_super) {
        __extends(ModalDialogTitle, _super);
        function ModalDialogTitle(title) {
                _super.call(this, "ModalDialogTitle", "h2");
            this.getEl().setInnerHtml(title);
        }
        return ModalDialogTitle;
    })(api_ui.Component);
    api_ui_dialog.ModalDialogTitle = ModalDialogTitle;    
    var ModalDialogContentPanel = (function (_super) {
        __extends(ModalDialogContentPanel, _super);
        function ModalDialogContentPanel() {
                _super.call(this, "ModalDialogContentPanel", "div");
        }
        return ModalDialogContentPanel;
    })(api_ui.Component);
    api_ui_dialog.ModalDialogContentPanel = ModalDialogContentPanel;    
    var ModalDialogButtonRow = (function (_super) {
        __extends(ModalDialogButtonRow, _super);
        function ModalDialogButtonRow() {
                _super.call(this, "ModalDialogButtonRow", "div");
        }
        return ModalDialogButtonRow;
    })(api_ui.Component);
    api_ui_dialog.ModalDialogButtonRow = ModalDialogButtonRow;    
})(api_ui_dialog || (api_ui_dialog = {}));
var api_delete;
(function (api_delete) {
    var DeleteDialog = (function (_super) {
        __extends(DeleteDialog, _super);
        function DeleteDialog(title, deleteAction, cancelAction) {
                _super.call(this, title);
            this.deleteButton = new api_ui_dialog.DialogButton(deleteAction);
            this.cancelButton = new api_ui_dialog.DialogButton(cancelAction);
        }
        return DeleteDialog;
    })(api_ui_dialog.ModalDialog);
    api_delete.DeleteDialog = DeleteDialog;    
})(api_delete || (api_delete = {}));
var api_notify;
(function (api_notify) {
    (function (Type) {
        Type._map = [];
        Type._map[0] = "INFO";
        Type.INFO = 0;
        Type._map[1] = "ERROR";
        Type.ERROR = 1;
        Type._map[2] = "ACTION";
        Type.ACTION = 2;
    })(api_notify.Type || (api_notify.Type = {}));
    var Type = api_notify.Type;
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
    api_notify.Action = Action;    
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
            api_notify.sendNotification(this);
        };
        return Message;
    })();
    api_notify.Message = Message;    
    function newInfo(text) {
        return new Message(Type.INFO, text);
    }
    api_notify.newInfo = newInfo;
    function newError(text) {
        return new Message(Type.ERROR, text);
    }
    api_notify.newError = newError;
    function newAction(text) {
        return new Message(Type.ACTION, text);
    }
    api_notify.newAction = newAction;
})(api_notify || (api_notify = {}));
var api_notify;
(function (api_notify) {
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
            var opts = api_notify.buildOpts(message);
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
    api_notify.NotifyManager = NotifyManager;    
    function getInnerEl(notificationEl) {
        return notificationEl.down('.admin-notification-inner');
    }
    var manager = new NotifyManager();
    function sendNotification(message) {
        manager.notify(message);
    }
    api_notify.sendNotification = sendNotification;
})(api_notify || (api_notify = {}));
var api_notify;
(function (api_notify) {
    var NotifyOpts = (function () {
        function NotifyOpts() { }
        return NotifyOpts;
    })();
    api_notify.NotifyOpts = NotifyOpts;    
    function buildOpts(message) {
        var opts = new NotifyOpts();
        if(message.getType() == api_notify.Type.ERROR) {
            opts.backgroundColor = 'red';
        } else if(message.getType() == api_notify.Type.ACTION) {
            opts.backgroundColor = '#669c34';
        }
        createHtmlMessage(message, opts);
        addListeners(message, opts);
        return opts;
    }
    api_notify.buildOpts = buildOpts;
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
})(api_notify || (api_notify = {}));
var api_notify;
(function (api_notify) {
    function showFeedback(message) {
        api_notify.newInfo(message).send();
    }
    api_notify.showFeedback = showFeedback;
    function updateAppTabCount(appId, tabCount) {
    }
    api_notify.updateAppTabCount = updateAppTabCount;
})(api_notify || (api_notify = {}));
var api_content_data;
(function (api_content_data) {
    var DataId = (function () {
        function DataId(name, arrayIndex) {
            this.name = name;
            this.arrayIndex = arrayIndex;
            if(arrayIndex > 0) {
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
            if(endsWithEndBracket && containsStartBracket) {
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
            for(var i in this.dataById) {
                var data = this.dataById[i];
                if(data.getName() === name) {
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
