var api_util;
(function (api_util) {
    var ImageLoader = (function () {
        function ImageLoader() { }
        ImageLoader.cachedImages = [];
        ImageLoader.get = function get(url, width, height) {
            var imageFound = false;
            var returnImage;
            for(var i in ImageLoader.cachedImages) {
                if(ImageLoader.cachedImages[i].src == url) {
                    imageFound = true;
                    returnImage = ImageLoader.cachedImages[i];
                }
            }
            if(!imageFound) {
                var image = new Image(width, height);
                image.src = url;
                ImageLoader.cachedImages[ImageLoader.cachedImages.length + 1] = image;
                returnImage = image;
            }
            return returnImage;
        };
        return ImageLoader;
    })();
    api_util.ImageLoader = ImageLoader;    
})(api_util || (api_util = {}));
var api_util;
(function (api_util) {
    api_util.baseUri = '../../..';
    function getAbsoluteUri(uri) {
        return this.baseUri + '/' + uri;
    }
    api_util.getAbsoluteUri = getAbsoluteUri;
})(api_util || (api_util = {}));
var api_util;
(function (api_util) {
    var Animation = (function () {
        function Animation() { }
        Animation.DELAY = 10;
        Animation.start = function start(doStep, duration, delay) {
            var startTime = new Date().getTime();
            var id = setInterval(function () {
                var progress = Math.min(((new Date()).getTime() - startTime) / duration, 1);
                doStep(progress);
                if(progress == 1) {
                    clearInterval(id);
                }
            }, delay || api_util.Animation.DELAY);
            return id;
        };
        Animation.stop = function stop(id) {
            clearInterval(id);
        };
        return Animation;
    })();
    api_util.Animation = Animation;    
})(api_util || (api_util = {}));
var api_handler;
(function (api_handler) {
    var DeleteSpaceParamFactory = (function () {
        function DeleteSpaceParamFactory() { }
        DeleteSpaceParamFactory.create = function create(spaces) {
            var spaceNames = [];
            for(var i = 0; i < spaces.length; i++) {
                spaceNames[i] = spaces[i].data.name;
            }
            return {
                spaceName: spaceNames
            };
        };
        return DeleteSpaceParamFactory;
    })();
    api_handler.DeleteSpaceParamFactory = DeleteSpaceParamFactory;    
})(api_handler || (api_handler = {}));
var api_handler;
(function (api_handler) {
    var DeleteSpacesHandler = (function () {
        function DeleteSpacesHandler() { }
        DeleteSpacesHandler.prototype.doDelete = function (deleteSpaceParam, callback) {
            api_remote.RemoteService.space_delete(deleteSpaceParam, function (response) {
                if(response) {
                    callback.call(this, response.success, response);
                } else {
                    console.error('Error', response ? response.error : 'Unable to delete space.');
                }
            });
        };
        return DeleteSpacesHandler;
    })();
    api_handler.DeleteSpacesHandler = DeleteSpacesHandler;    
})(api_handler || (api_handler = {}));
var api_handler;
(function (api_handler) {
    var DeleteContentParamFactory = (function () {
        function DeleteContentParamFactory() { }
        DeleteContentParamFactory.create = function create(content) {
            var contentIds = [];
            for(var i = 0; i < content.length; i++) {
                contentIds[i] = content[i].data.id;
            }
            return {
                contentIds: contentIds
            };
        };
        return DeleteContentParamFactory;
    })();
    api_handler.DeleteContentParamFactory = DeleteContentParamFactory;    
})(api_handler || (api_handler = {}));
var api_handler;
(function (api_handler) {
    var DeleteContentHandler = (function () {
        function DeleteContentHandler() { }
        DeleteContentHandler.prototype.doDelete = function (deleteContentParam, callback) {
            api_remote.RemoteService.content_delete(deleteContentParam, function (response) {
                if(response) {
                    callback.call(this, response.success, response);
                } else {
                    console.error('Error', response ? response.error : 'Unable to delete content.');
                }
            });
        };
        return DeleteContentHandler;
    })();
    api_handler.DeleteContentHandler = DeleteContentHandler;    
})(api_handler || (api_handler = {}));
var api_remote;
(function (api_remote) {
    var JsonRpcProvider = (function () {
        function JsonRpcProvider(url, methods, namespace) {
            var config = {
                url: url,
                type: 'jsonrpc',
                namespace: namespace,
                methods: methods,
                enableBuffer: 20,
                alias: 'direct.jsonrpcprovider'
            };
            var remotingProvider = new Ext.direct.RemotingProvider(config);
            remotingProvider.getCallData = this.getCallData;
            remotingProvider.createEvent = this.createEvent;
            this.ext = remotingProvider;
            this.ext.isProvider = true;
            this.initAPI(methods);
        }
        JsonRpcProvider.prototype.initAPI = function (methods) {
            var namespace = this.ext.namespace;
            var methodName, length = methods.length;
            for(var i = 0; i < length; i++) {
                methodName = methods[i];
                var def = {
                    name: methodName,
                    len: 1
                };
                var method = new Ext.direct.RemotingMethod(def);
                namespace[methodName] = this.ext.createHandler(null, method);
            }
        };
        JsonRpcProvider.prototype.getCallData = function (transaction) {
            return {
                jsonrpc: '2.0',
                id: transaction.tid,
                method: transaction.method,
                params: transaction.data[0]
            };
        };
        JsonRpcProvider.prototype.createEvent = function (response) {
            var error = response.error ? true : false;
            response.tid = response.id;
            response.type = error ? 'exception' : 'rpc';
            if(error) {
                response.message = response.error.message;
            }
            return Ext.create('direct.' + response.type, response);
        };
        return JsonRpcProvider;
    })();
    api_remote.JsonRpcProvider = JsonRpcProvider;    
})(api_remote || (api_remote = {}));
var api_remote;
(function (api_remote) {
    api_remote.RemoteService;
    var RemoteServiceImpl = (function () {
        function RemoteServiceImpl() {
        }
        RemoteServiceImpl.prototype.init = function (namespace) {
            var url = api_util.getAbsoluteUri("admin/rest/jsonrpc");
            var methods = [
                "account_find", 
                "account_getGraph", 
                "account_changePassword", 
                "account_verifyUniqueEmail", 
                "account_suggestUserName", 
                "account_createOrUpdate", 
                "account_delete", 
                "account_get", 
                "util_getCountries", 
                "util_getLocales", 
                "util_getTimeZones", 
                "userstore_getAll", 
                "userstore_get", 
                "userstore_getConnectors", 
                "userstore_createOrUpdate", 
                "userstore_delete", 
                "content_createOrUpdate", 
                "content_list", 
                "contentType_get", 
                "content_tree", 
                "content_get", 
                "contentType_list", 
                "content_delete", 
                "content_validate", 
                "content_find", 
                "contentType_createOrUpdate", 
                "contentType_delete", 
                "contentType_tree", 
                "schema_list", 
                "schema_tree", 
                "system_getSystemInfo", 
                "mixin_get", 
                "mixin_createOrUpdate", 
                "mixin_delete", 
                "relationshipType_get", 
                "relationshipType_createOrUpdate", 
                "relationshipType_delete", 
                "space_list", 
                "space_get", 
                "space_delete", 
                "space_createOrUpdate", 
                "binary_create"
            ];
            var jsonRpcProvider = new api_remote.JsonRpcProvider(url, methods, namespace);
            this.provider = Ext.Direct.addProvider(jsonRpcProvider.ext);
        };
        RemoteServiceImpl.prototype.account_find = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.account_getGraph = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.account_changePassword = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.account_verifyUniqueEmail = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.account_suggestUserName = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.account_createOrUpdate = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.account_delete = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.account_get = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.util_getCountries = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.util_getLocales = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.util_getTimeZones = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.userstore_getAll = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.userstore_get = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.userstore_getConnectors = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.userstore_createOrUpdate = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.userstore_delete = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.content_createOrUpdate = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.contentType_get = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.content_list = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.content_tree = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.content_get = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.contentType_list = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.content_delete = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.content_find = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.content_validate = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.contentType_createOrUpdate = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.contentType_delete = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.contentType_tree = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.schema_tree = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.schema_list = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.system_getSystemInfo = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.mixin_get = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.mixin_createOrUpdate = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.mixin_delete = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.relationshipType_get = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.relationshipType_createOrUpdate = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.relationshipType_delete = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.space_list = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.space_get = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.space_delete = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.space_createOrUpdate = function (params, callback) {
            console.log(params, callback);
        };
        RemoteServiceImpl.prototype.binary_create = function (params, callback) {
            console.log(params, callback);
        };
        return RemoteServiceImpl;
    })();    
    var remoteServiceImpl = new RemoteServiceImpl();
    api_remote.RemoteService = remoteServiceImpl;
    remoteServiceImpl.init('api_remote.RemoteService');
})(api_remote || (api_remote = {}));
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
            var node = template.append(Ext.getBody(), {
            });
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
var api_dom;
(function (api_dom) {
    var ElementHelper = (function () {
        function ElementHelper(element) {
            this.el = element;
        }
        ElementHelper.fromName = function fromName(name) {
            return new ElementHelper(document.createElement(name));
        };
        ElementHelper.prototype.getHTMLElement = function () {
            return this.el;
        };
        ElementHelper.prototype.insertBefore = function (newEl, existingEl) {
            this.el.insertBefore(newEl.getHTMLElement(), existingEl.getHTMLElement());
        };
        ElementHelper.prototype.setDisabled = function (value) {
            this.el.disabled = value;
            return this;
        };
        ElementHelper.prototype.setId = function (value) {
            this.el.id = value;
            return this;
        };
        ElementHelper.prototype.setInnerHtml = function (value) {
            this.el.innerHTML = value;
            return this;
        };
        ElementHelper.prototype.setValue = function (value) {
            this.el.setAttribute("value", value);
            return this;
        };
        ElementHelper.prototype.addClass = function (clsName) {
            if(!this.hasClass(clsName)) {
                if(this.el.className === '') {
                    this.el.className += clsName;
                } else {
                    this.el.className += ' ' + clsName;
                }
            }
        };
        ElementHelper.prototype.hasClass = function (clsName) {
            return this.el.className.match(new RegExp('(\\s|^)' + clsName + '(\\s|$)')) !== null;
        };
        ElementHelper.prototype.removeClass = function (clsName) {
            if(this.hasClass(clsName)) {
                var reg = new RegExp('(\\s|^)' + clsName + '(\\s|$)');
                this.el.className = this.el.className.replace(reg, '');
            }
        };
        ElementHelper.prototype.addEventListener = function (eventName, f) {
            this.el.addEventListener(eventName, f);
        };
        ElementHelper.prototype.removeEventListener = function (eventName, f) {
            this.el.removeEventListener(eventName, f);
        };
        ElementHelper.prototype.appendChild = function (child) {
            this.el.appendChild(child);
            return this;
        };
        ElementHelper.prototype.setData = function (name, value) {
            var any = this.el;
            any._data[name] = value;
            return this;
        };
        ElementHelper.prototype.getData = function (name) {
            var any = this.el;
            return any._data[name];
        };
        ElementHelper.prototype.getDisplay = function () {
            return this.el.style.display;
        };
        ElementHelper.prototype.setDisplay = function (value) {
            this.el.style.display = value;
            return this;
        };
        ElementHelper.prototype.getVisibility = function () {
            return this.el.style.visibility;
        };
        ElementHelper.prototype.setVisibility = function (value) {
            this.el.style.visibility = value;
            return this;
        };
        ElementHelper.prototype.setPosition = function (value) {
            this.el.style.position = value;
            return this;
        };
        ElementHelper.prototype.setWidth = function (value) {
            this.el.style.width = value;
            return this;
        };
        ElementHelper.prototype.getWidth = function () {
            return this.el.offsetWidth;
        };
        ElementHelper.prototype.setHeight = function (value) {
            this.el.style.height = value;
            return this;
        };
        ElementHelper.prototype.getHeight = function () {
            return this.el.offsetHeight;
        };
        ElementHelper.prototype.setTop = function (value) {
            this.el.style.top = value;
            return this;
        };
        ElementHelper.prototype.setLeft = function (value) {
            this.el.style.left = value;
            return this;
        };
        ElementHelper.prototype.setMarginLeft = function (value) {
            this.el.style.marginLeft = value;
            return this;
        };
        ElementHelper.prototype.setMarginRight = function (value) {
            this.el.style.marginRight = value;
            return this;
        };
        ElementHelper.prototype.setMarginTop = function (value) {
            this.el.style.marginTop = value;
            return this;
        };
        ElementHelper.prototype.setMarginBottom = function (value) {
            this.el.style.marginBottom = value;
            return this;
        };
        ElementHelper.prototype.setZindex = function (value) {
            this.el.style.zIndex = value.toString();
            return this;
        };
        ElementHelper.prototype.setBackgroundImage = function (value) {
            this.el.style.backgroundImage = value;
            return this;
        };
        ElementHelper.prototype.remove = function () {
            var parent = this.el.parentElement;
            parent.removeChild(this.el);
        };
        ElementHelper.prototype.getOffset = function () {
            var el = this.el;
            var x = 0, y = 0;
            while(el && !isNaN(el.offsetLeft) && !isNaN(el.offsetTop)) {
                x += el.offsetLeft - el.scrollLeft;
                y += el.offsetTop - el.scrollTop;
                el = el.offsetParent;
            }
            return {
                top: y,
                left: x
            };
        };
        return ElementHelper;
    })();
    api_dom.ElementHelper = ElementHelper;    
})(api_dom || (api_dom = {}));
var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var api_dom;
(function (api_dom) {
    var ImgHelper = (function (_super) {
        __extends(ImgHelper, _super);
        function ImgHelper(element) {
                _super.call(this, element);
            this.el = element;
        }
        ImgHelper.create = function create() {
            return new ImgHelper(document.createElement("img"));
        };
        ImgHelper.prototype.getHTMLElement = function () {
            return this.el;
        };
        ImgHelper.prototype.setSrc = function (value) {
            this.el.src = value;
            return this;
        };
        return ImgHelper;
    })(api_dom.ElementHelper);
    api_dom.ImgHelper = ImgHelper;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var Element = (function () {
        function Element(elementName, idPrefix, className, elHelper) {
            if(elHelper == null) {
                this.el = api_dom.ElementHelper.fromName(elementName);
            } else {
                this.el = elHelper;
            }
            if(idPrefix != null) {
                this.id = idPrefix + '-' + (++Element.constructorCounter);
                this.el.setId(this.id);
            }
            if(className != null) {
                this.getHTMLElement().className = className;
            }
        }
        Element.constructorCounter = 0;
        Element.prototype.show = function () {
            jQuery(this.el.getHTMLElement()).show();
        };
        Element.prototype.hide = function () {
            jQuery(this.el.getHTMLElement()).hide();
        };
        Element.prototype.isVisible = function () {
            var displayed = this.el.getDisplay() != "none";
            var visible = this.el.getVisibility() != "hidden";
            var sized = this.el.getWidth() != 0 || this.el.getHeight() != 0;
            return displayed && visible && sized;
        };
        Element.prototype.empty = function () {
            this.el.setInnerHtml("");
        };
        Element.prototype.getId = function () {
            return this.id;
        };
        Element.prototype.getEl = function () {
            return this.el;
        };
        Element.prototype.getHTMLElement = function () {
            return this.el.getHTMLElement();
        };
        Element.prototype.appendChild = function (child) {
            this.el.appendChild(child.getEl().getHTMLElement());
        };
        Element.prototype.prependChild = function (child) {
            this.el.getHTMLElement().insertBefore(child.getHTMLElement(), this.el.getHTMLElement().firstChild);
        };
        Element.prototype.removeChild = function (child) {
            if(this.el.getHTMLElement().contains(child.getHTMLElement())) {
                this.el.getHTMLElement().removeChild(child.getHTMLElement());
            }
        };
        Element.prototype.removeChildren = function () {
            var htmlEl = this.el.getHTMLElement();
            while(htmlEl.firstChild) {
                htmlEl.removeChild(htmlEl.firstChild);
            }
        };
        return Element;
    })();
    api_dom.Element = Element;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var DivEl = (function (_super) {
        __extends(DivEl, _super);
        function DivEl(idPrefix, className) {
                _super.call(this, "div", idPrefix, className);
        }
        return DivEl;
    })(api_dom.Element);
    api_dom.DivEl = DivEl;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var H1El = (function (_super) {
        __extends(H1El, _super);
        function H1El(idPrefix, className) {
                _super.call(this, "h1", idPrefix, className);
        }
        return H1El;
    })(api_dom.Element);
    api_dom.H1El = H1El;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var H2El = (function (_super) {
        __extends(H2El, _super);
        function H2El(idPrefix, className) {
                _super.call(this, "h2", idPrefix, className);
        }
        return H2El;
    })(api_dom.Element);
    api_dom.H2El = H2El;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var H3El = (function (_super) {
        __extends(H3El, _super);
        function H3El(idPrefix, className) {
                _super.call(this, "h3", idPrefix, className);
        }
        return H3El;
    })(api_dom.Element);
    api_dom.H3El = H3El;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var H4El = (function (_super) {
        __extends(H4El, _super);
        function H4El(idPrefix, className) {
                _super.call(this, "h4", idPrefix, className);
        }
        return H4El;
    })(api_dom.Element);
    api_dom.H4El = H4El;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var UlEl = (function (_super) {
        __extends(UlEl, _super);
        function UlEl(idPrefix, className) {
                _super.call(this, "ul", idPrefix, className);
        }
        return UlEl;
    })(api_dom.Element);
    api_dom.UlEl = UlEl;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var LiEl = (function (_super) {
        __extends(LiEl, _super);
        function LiEl(idPrefix, className) {
                _super.call(this, "li", idPrefix, className);
        }
        return LiEl;
    })(api_dom.Element);
    api_dom.LiEl = LiEl;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var EmEl = (function (_super) {
        __extends(EmEl, _super);
        function EmEl(idPrefix, className) {
                _super.call(this, "em", idPrefix, className);
        }
        return EmEl;
    })(api_dom.Element);
    api_dom.EmEl = EmEl;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var ImgEl = (function (_super) {
        __extends(ImgEl, _super);
        function ImgEl(src, idPrefix, className) {
                _super.call(this, "img", idPrefix, className, api_dom.ImgHelper.create());
            this.getEl().setSrc(src);
        }
        ImgEl.prototype.getEl = function () {
            return _super.prototype.getEl.call(this);
        };
        return ImgEl;
    })(api_dom.Element);
    api_dom.ImgEl = ImgEl;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var SpanEl = (function (_super) {
        __extends(SpanEl, _super);
        function SpanEl(idPrefix, className) {
                _super.call(this, 'span', idPrefix, className);
        }
        return SpanEl;
    })(api_dom.Element);
    api_dom.SpanEl = SpanEl;    
})(api_dom || (api_dom = {}));
var api_dom;
(function (api_dom) {
    var ButtonEl = (function (_super) {
        __extends(ButtonEl, _super);
        function ButtonEl(idPrefix, className) {
                _super.call(this, "button", idPrefix, className);
        }
        return ButtonEl;
    })(api_dom.Element);
    api_dom.ButtonEl = ButtonEl;    
})(api_dom || (api_dom = {}));
var api_ui;
(function (api_ui) {
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
        Action.prototype.getIconClass = function () {
            return this.iconClass;
        };
        Action.prototype.setIconClass = function (value) {
            if(value !== this.iconClass) {
                this.iconClass = value;
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
    api_ui.Action = Action;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var Panel = (function (_super) {
        __extends(Panel, _super);
        function Panel(idPrefix) {
                _super.call(this, idPrefix, "panel");
        }
        return Panel;
    })(api_dom.DivEl);
    api_ui.Panel = Panel;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var DeckPanel = (function (_super) {
        __extends(DeckPanel, _super);
        function DeckPanel(idPrefix) {
                _super.call(this, idPrefix || "DeckPanel");
            this.panels = [];
            this.panelShown = -1;
        }
        DeckPanel.prototype.getSize = function () {
            return this.panels.length;
        };
        DeckPanel.prototype.addPanel = function (panel) {
            panel.hide();
            this.appendChild(panel);
            return this.panels.push(panel) - 1;
        };
        DeckPanel.prototype.getPanel = function (index) {
            return this.panels[index];
        };
        DeckPanel.prototype.removePanel = function (index) {
            var panel = this.panels[index];
            panel.getEl().remove();
            var lastPanel = this.panels.length == index + 1;
            this.panels.splice(index, 1);
            if(this.panels.length == 0) {
                return panel;
            }
            if(this.isShownPanel(index)) {
                if(!lastPanel) {
                    this.panels[this.panels.length - 1].show();
                    this.panelShown = this.panels.length - 1;
                } else {
                    this.panels[index - 1].show();
                }
            }
            return panel;
        };
        DeckPanel.prototype.isShownPanel = function (panelIndex) {
            return this.panelShown === panelIndex;
        };
        DeckPanel.prototype.showPanel = function (index) {
            for(var i = 0; i < this.panels.length; i++) {
                var panel = this.panels[i];
                if(i === index) {
                    panel.show();
                    this.panelShown = index;
                } else {
                    panel.hide();
                }
            }
        };
        DeckPanel.prototype.getPanels = function () {
            return this.panels;
        };
        return DeckPanel;
    })(api_ui.Panel);
    api_ui.DeckPanel = DeckPanel;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var BodyMask = (function (_super) {
        __extends(BodyMask, _super);
        function BodyMask() {
                _super.call(this, "Mask");
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
    })(api_dom.DivEl);
    api_ui.BodyMask = BodyMask;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var AbstractButton = (function (_super) {
        __extends(AbstractButton, _super);
        function AbstractButton(idPrefix, label) {
                _super.call(this, idPrefix);
            this.label = label;
            this.getEl().setInnerHtml(this.label);
        }
        AbstractButton.prototype.setEnable = function (value) {
            this.getEl().setDisabled(!value);
        };
        return AbstractButton;
    })(api_dom.ButtonEl);
    api_ui.AbstractButton = AbstractButton;    
})(api_ui || (api_ui = {}));
var api_ui_toolbar;
(function (api_ui_toolbar) {
    var Toolbar = (function (_super) {
        __extends(Toolbar, _super);
        function Toolbar() {
                _super.call(this, "Toolbar", "toolbar");
            this.components = [];
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
            var button = this.addActionButton(action);
            this.appendChild(button);
        };
        Toolbar.prototype.addElement = function (element) {
            if(this.hasGreedySpacer()) {
                element.getEl().addClass('pull-right');
            }
            this.appendChild(element);
        };
        Toolbar.prototype.addGreedySpacer = function () {
            var spacer = new ToolbarGreedySpacer();
            this.components.push(spacer);
        };
        Toolbar.prototype.addActionButton = function (action) {
            var button = new ToolbarButton(action);
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
    })(api_dom.DivEl);
    api_ui_toolbar.Toolbar = Toolbar;    
    var ToolbarButton = (function (_super) {
        __extends(ToolbarButton, _super);
        function ToolbarButton(action) {
            var _this = this;
                _super.call(this, "ToolbarButton", action.getLabel());
            this.action = action;
            this.getEl().addEventListener("click", function (evt) {
                _this.action.execute();
            });
            if(action.getIconClass()) {
                this.getEl().addClass(action.getIconClass());
            }
            this.setEnable(action.isEnabled());
            action.addPropertyChangeListener(function (action) {
                _this.setEnable(action.isEnabled());
            });
        }
        ToolbarButton.prototype.setFloatRight = function (value) {
            if(value) {
                this.getEl().addClass('pull-right');
            }
        };
        return ToolbarButton;
    })(api_ui.AbstractButton);    
    var ToolbarGreedySpacer = (function () {
        function ToolbarGreedySpacer() {
        }
        return ToolbarGreedySpacer;
    })();    
})(api_ui_toolbar || (api_ui_toolbar = {}));
var api_ui_toolbar;
(function (api_ui_toolbar) {
    var ToggleSlide = (function (_super) {
        __extends(ToggleSlide, _super);
        function ToggleSlide(onText, offText, initOn) {
                _super.call(this, 'ToogleSlide', 'toggle-slide');
            this.animationDuration = 300;
            this.onText = onText;
            this.offText = offText;
            this.createMarkup();
            this.calculateStyles();
            initOn ? this.turnOn() : this.turnOff();
            this.addListeners();
        }
        ToggleSlide.prototype.toggle = function () {
            this.isOn ? this.turnOff() : this.turnOn();
        };
        ToggleSlide.prototype.turnOn = function () {
            this.slideRight();
            this.isOn = true;
        };
        ToggleSlide.prototype.turnOff = function () {
            this.slideLeft();
            this.isOn = false;
        };
        ToggleSlide.prototype.isTurnedOn = function () {
            return this.isOn;
        };
        ToggleSlide.prototype.createMarkup = function () {
            this.thumb = new api_dom.DivEl(null, 'thumb');
            this.holder = new api_dom.DivEl(null, 'holder');
            this.onLabel = new api_dom.DivEl(null, 'on');
            this.offLabel = new api_dom.DivEl(null, 'off');
            var thumbEl = this.thumb.getEl(), holderEl = this.holder.getEl(), onLabelEl = this.onLabel.getEl(), offLabelEl = this.offLabel.getEl();
            this.getEl().appendChild(thumbEl.getHTMLElement()).appendChild(holderEl.getHTMLElement());
            holderEl.appendChild(onLabelEl.getHTMLElement()).appendChild(offLabelEl.getHTMLElement());
            onLabelEl.setInnerHtml(this.onText);
            offLabelEl.setInnerHtml(this.offText);
        };
        ToggleSlide.prototype.calculateStyles = function () {
            var thumbEl = this.thumb.getEl(), onLabelEl = this.onLabel.getEl(), offLabelEl = this.offLabel.getEl();
            document.body.appendChild(this.getHTMLElement());
            var onWidth = onLabelEl.getWidth(), offWidth = offLabelEl.getWidth();
            var thumbWidth = Math.max(onWidth, offWidth);
            thumbEl.setWidth((thumbWidth + 4) + 'px');
            onLabelEl.setWidth(thumbWidth + 'px');
            offLabelEl.setWidth(thumbWidth + 'px');
        };
        ToggleSlide.prototype.addListeners = function () {
            var me = this;
            me.getEl().addEventListener('click', function () {
                me.toggle();
            });
        };
        ToggleSlide.prototype.slideLeft = function () {
            var thumbEl = this.thumb.getEl(), offset = this.calculateOffset();
            this.animate(function (progress) {
                thumbEl.setLeft(offset * (1 - progress) + 'px');
            });
        };
        ToggleSlide.prototype.slideRight = function () {
            var thumbEl = this.thumb.getEl(), offset = this.calculateOffset();
            this.animate(function (progress) {
                thumbEl.setLeft(offset * progress + 'px');
            });
        };
        ToggleSlide.prototype.calculateOffset = function () {
            var toggleWidth = this.getEl().getWidth(), thumbWidth = this.thumb.getEl().getWidth();
            return toggleWidth - thumbWidth;
        };
        ToggleSlide.prototype.animate = function (step) {
            if(this.animationId) {
                api_util.Animation.stop(this.animationId);
            }
            this.animationId = api_util.Animation.start(step, this.animationDuration);
        };
        return ToggleSlide;
    })(api_dom.DivEl);
    api_ui_toolbar.ToggleSlide = ToggleSlide;    
})(api_ui_toolbar || (api_ui_toolbar = {}));
var api_ui_menu;
(function (api_ui_menu) {
    var MenuItem = (function (_super) {
        __extends(MenuItem, _super);
        function MenuItem(action) {
            var _this = this;
                _super.call(this, "menu-item");
            this.action = action;
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
                el.removeClass("disabled");
            } else {
                el.addClass("disabled");
            }
        };
        return MenuItem;
    })(api_dom.LiEl);
    api_ui_menu.MenuItem = MenuItem;    
})(api_ui_menu || (api_ui_menu = {}));
var api_ui_menu;
(function (api_ui_menu) {
    var ContextMenu = (function (_super) {
        __extends(ContextMenu, _super);
        function ContextMenu() {
            var _this = this;
            var actions = [];
            for (var _i = 0; _i < (arguments.length - 0); _i++) {
                actions[_i] = arguments[_i + 0];
            }
                _super.call(this, "context-menu", "context-menu");
            this.menuItems = [];
            var htmlEl = this.getHTMLElement();
            document.body.insertBefore(htmlEl, document.body.childNodes[0]);
            for(var i = 0; i < actions.length; i++) {
                this.addAction(actions[i]);
            }
            document.addEventListener('click', function (evt) {
                _this.hideMenuOnOutsideClick(evt);
            });
        }
        ContextMenu.prototype.addAction = function (action) {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
        };
        ContextMenu.prototype.createMenuItem = function (action) {
            var _this = this;
            var menuItem = new api_ui_menu.MenuItem(action);
            menuItem.getEl().addEventListener('click', function (evt) {
                _this.hide();
            });
            this.menuItems.push(menuItem);
            return menuItem;
        };
        ContextMenu.prototype.showAt = function (x, y) {
            this.getEl().setPosition('absolute').setZindex(20000).setLeft(x + 'px').setTop(y + 'px').setDisplay('block');
        };
        ContextMenu.prototype.hide = function () {
            this.getEl().setDisplay('none');
        };
        ContextMenu.prototype.hideMenuOnOutsideClick = function (evt) {
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
    })(api_dom.UlEl);
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
                _super.call(this, "action-menu", "action-menu");
            this.menuItems = [];
            this.button = new ActionMenuButton(this);
            for(var i = 0; i < actions.length; i++) {
                this.addAction(actions[i]);
            }
            window.document.addEventListener("click", function (evt) {
                _this.hideMenuOnOutsideClick(evt);
            });
            this.initExt();
        }
        ActionMenu.prototype.addAction = function (action) {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
        };
        ActionMenu.prototype.getExt = function () {
            return this.button.getExt();
        };
        ActionMenu.prototype.showBy = function (button) {
            this.ext.show();
            this.ext.getEl().alignTo(button.getExt().getEl(), 'tl-bl?', [
                -2, 
                0
            ]);
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
            var menuItem = new api_ui_menu.MenuItem(action);
            menuItem.getEl().addEventListener("click", function (evt) {
                _this.hide();
            });
            this.menuItems.push(menuItem);
            return menuItem;
        };
        ActionMenu.prototype.hide = function () {
            this.ext.hide();
        };
        ActionMenu.prototype.hideMenuOnOutsideClick = function (evt) {
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
    })(api_dom.UlEl);
    api_ui_menu.ActionMenu = ActionMenu;    
    var ActionMenuButton = (function (_super) {
        __extends(ActionMenuButton, _super);
        function ActionMenuButton(menu) {
            var _this = this;
                _super.call(this, "button", "action-menu-button");
            this.menu = menu;
            var btnEl = this.getEl();
            btnEl.setInnerHtml("Actions");
            btnEl.addEventListener("click", function (e) {
                menu.showBy(_this);
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
        ActionMenuButton.prototype.getExt = function () {
            return this.ext;
        };
        ActionMenuButton.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        };
        return ActionMenuButton;
    })(api_dom.ButtonEl);
    api_ui_menu.ActionMenuButton = ActionMenuButton;    
})(api_ui_menu || (api_ui_menu = {}));
var api_ui_tab;
(function (api_ui_tab) {
    var TabMenu = (function (_super) {
        __extends(TabMenu, _super);
        function TabMenu(idPrefix) {
            var _this = this;
                _super.call(this, idPrefix || "TabMenu");
            this.showingMenuItems = false;
            this.tabs = [];
            this.tabSelectedListeners = [];
            this.tabRemovedListeners = [];
            this.tabMenuButton = this.createTabMenuButton();
            this.tabMenuButton.getEl().addEventListener("click", function () {
                _this.toggleMenu();
            });
            this.appendChild(this.tabMenuButton);
            this.menuEl = this.createMenu();
            this.appendChild(this.menuEl);
            this.initExt();
        }
        TabMenu.prototype.createTabMenuButton = function () {
            return new api_ui_tab.TabMenuButton();
        };
        TabMenu.prototype.createMenu = function () {
            var ulEl = new api_dom.UlEl();
            ulEl.getEl().setZindex(19001);
            ulEl.hide();
            return ulEl;
        };
        TabMenu.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        };
        TabMenu.prototype.toggleMenu = function () {
            if(!this.showingMenuItems) {
                this.showMenu();
            } else {
                this.hideMenu();
            }
        };
        TabMenu.prototype.hideMenu = function () {
            this.menuEl.hide();
            this.showingMenuItems = false;
        };
        TabMenu.prototype.showMenu = function () {
            this.menuEl.show();
            this.showingMenuItems = true;
        };
        TabMenu.prototype.addTab = function (tab) {
            var tabMenuItem = tab;
            tabMenuItem.setTabMenu(this);
            var newLength = this.tabs.push(tabMenuItem);
            tabMenuItem.setTabIndex(newLength - 1);
            if(tab.isVisible()) {
                this.tabMenuButton.setLabel(tab.getLabel());
                this.menuEl.appendChild(tabMenuItem);
            }
        };
        TabMenu.prototype.getSize = function () {
            var size = 0;
            this.tabs.forEach(function (tab) {
                if(tab.isVisible()) {
                    size++;
                }
            });
            return size;
        };
        TabMenu.prototype.removeTab = function (tab) {
            var tabMenuItem = tab;
            tabMenuItem.getEl().remove();
            var isLastTab = this.isLastTab(tab);
            this.tabs.splice(tab.getTabIndex(), 1);
            if(this.isSelectedTab(tab)) {
            }
            if(!isLastTab) {
                for(var i = tab.getTabIndex() - 1; i < this.tabs.length; i++) {
                    this.tabs[i].setTabIndex(i);
                }
            }
        };
        TabMenu.prototype.isSelectedTab = function (tab) {
            return tab == this.selectedTab;
        };
        TabMenu.prototype.isLastTab = function (tab) {
            return tab.getTabIndex() === this.tabs.length;
        };
        TabMenu.prototype.selectTab = function (tab) {
            this.tabMenuButton.setLabel(tab.getLabel());
            this.selectedTab = tab;
        };
        TabMenu.prototype.getActiveTab = function () {
            return this.selectedTab;
        };
        TabMenu.prototype.deselectTab = function () {
            this.tabMenuButton.setLabel("");
            this.selectedTab = null;
        };
        TabMenu.prototype.addTabSelectedListener = function (listener) {
            this.tabSelectedListeners.push(listener);
        };
        TabMenu.prototype.addTabRemoveListener = function (listener) {
            this.tabRemovedListeners.push(listener);
        };
        TabMenu.prototype.handleTabClickedEvent = function (tabMenuItem) {
            this.hideMenu();
            this.fireTabSelected(tabMenuItem);
        };
        TabMenu.prototype.handleTabRemoveButtonClickedEvent = function (tabMenuItem) {
            if(this.fireTabRemoveEvent(tabMenuItem)) {
                this.removeTab(tabMenuItem);
            }
        };
        TabMenu.prototype.fireTabSelected = function (tab) {
            for(var i = 0; i < this.tabSelectedListeners.length; i++) {
                this.tabSelectedListeners[i](tab);
            }
        };
        TabMenu.prototype.fireTabRemoveEvent = function (tab) {
            for(var i = 0; i < this.tabRemovedListeners.length; i++) {
                if(!this.tabRemovedListeners[i](tab)) {
                    return false;
                }
            }
            return true;
        };
        return TabMenu;
    })(api_dom.DivEl);
    api_ui_tab.TabMenu = TabMenu;    
})(api_ui_tab || (api_ui_tab = {}));
var api_ui_tab;
(function (api_ui_tab) {
    var TabMenuButton = (function (_super) {
        __extends(TabMenuButton, _super);
        function TabMenuButton(idPrefix) {
                _super.call(this, idPrefix || "TabMenuButton");
            this.labelEl = new api_dom.SpanEl();
            this.appendChild(this.labelEl);
        }
        TabMenuButton.prototype.setTabMenu = function (tabMenu) {
            this.tabMenu = tabMenu;
        };
        TabMenuButton.prototype.setLabel = function (value) {
            this.labelEl.getEl().setInnerHtml(value);
        };
        return TabMenuButton;
    })(api_dom.DivEl);
    api_ui_tab.TabMenuButton = TabMenuButton;    
})(api_ui_tab || (api_ui_tab = {}));
var api_ui_tab;
(function (api_ui_tab) {
    var TabMenuItem = (function (_super) {
        __extends(TabMenuItem, _super);
        function TabMenuItem(label) {
            var _this = this;
                _super.call(this, "TabMenuItem", "tab-menu-item");
            this.visible = true;
            this.removable = true;
            this.label = label;
            this.labelEl = new api_dom.SpanEl();
            this.labelEl.getEl().setInnerHtml(label);
            this.appendChild(this.labelEl);
            var removeButton = new api_dom.ButtonEl();
            removeButton.getEl().setInnerHtml("&times;");
            this.appendChild(removeButton);
            this.labelEl.getEl().addEventListener("click", function () {
                _this.tabMenu.handleTabClickedEvent(_this);
            });
            removeButton.getEl().addEventListener("click", function () {
                if(_this.removable) {
                    _this.tabMenu.handleTabRemoveButtonClickedEvent(_this);
                    if(_this.tabMenu.getSize() == 0) {
                        _this.tabMenu.hideMenu();
                    }
                }
            });
        }
        TabMenuItem.prototype.setTabMenu = function (tabMenu) {
            this.tabMenu = tabMenu;
        };
        TabMenuItem.prototype.setTabIndex = function (value) {
            this.tabIndex = value;
        };
        TabMenuItem.prototype.getTabIndex = function () {
            return this.tabIndex;
        };
        TabMenuItem.prototype.getLabel = function () {
            return this.label;
        };
        TabMenuItem.prototype.isVisible = function () {
            return this.visible;
        };
        TabMenuItem.prototype.setVisible = function (value) {
            this.visible = value;
            if(!this.visible) {
                this.remove();
            }
        };
        TabMenuItem.prototype.isRemovable = function () {
            return this.removable;
        };
        TabMenuItem.prototype.setRemovable = function (value) {
            this.removable = value;
        };
        TabMenuItem.prototype.remove = function () {
            if(this.tabMenu) {
                this.tabMenu.removeChild(this);
            }
        };
        return TabMenuItem;
    })(api_dom.LiEl);
    api_ui_tab.TabMenuItem = TabMenuItem;    
})(api_ui_tab || (api_ui_tab = {}));
var api_ui_tab;
(function (api_ui_tab) {
    var TabBar = (function (_super) {
        __extends(TabBar, _super);
        function TabBar(idPrefix) {
                _super.call(this, idPrefix || "TabBar");
        }
        TabBar.prototype.addTab = function (tab) {
        };
        TabBar.prototype.removeTab = function (tab) {
        };
        TabBar.prototype.getSize = function () {
            return 0;
        };
        TabBar.prototype.getActiveTab = function () {
            return null;
        };
        TabBar.prototype.selectTab = function (tab) {
        };
        TabBar.prototype.deselectTab = function () {
        };
        TabBar.prototype.addTabSelectedListener = function (listener) {
        };
        TabBar.prototype.addTabRemoveListener = function (listener) {
        };
        return TabBar;
    })(api_dom.DivEl);
    api_ui_tab.TabBar = TabBar;    
})(api_ui_tab || (api_ui_tab = {}));
var api_ui_tab;
(function (api_ui_tab) {
    var TabbedDeckPanel = (function (_super) {
        __extends(TabbedDeckPanel, _super);
        function TabbedDeckPanel(navigator) {
            var _this = this;
                _super.call(this);
            this.navigator = navigator;
            this.navigator.addTabRemoveListener(function (tab) {
                return _this.tabRemove(tab);
            });
            this.navigator.addTabSelectedListener(function (tab) {
                _this.showTab(tab);
            });
        }
        TabbedDeckPanel.prototype.addTab = function (tab, panel) {
            this.navigator.addTab(tab);
            this.addPanel(panel);
        };
        TabbedDeckPanel.prototype.showTab = function (tab) {
            _super.prototype.showPanel.call(this, tab.getTabIndex());
            this.navigator.selectTab(tab);
        };
        TabbedDeckPanel.prototype.tabRemove = function (tab) {
            this.removePanel(tab.getTabIndex());
            return true;
        };
        return TabbedDeckPanel;
    })(api_ui.DeckPanel);
    api_ui_tab.TabbedDeckPanel = TabbedDeckPanel;    
})(api_ui_tab || (api_ui_tab = {}));
var api_ui;
(function (api_ui) {
    var Tooltip = (function (_super) {
        __extends(Tooltip, _super);
        function Tooltip(target, text, timeout, side, offset) {
                _super.call(this, "Tooltip", "tooltip");
            this.target = target;
            this.timeout = timeout !== undefined ? timeout : 1000;
            this.side = side || "bottom";
            this.offset = offset || [
                0, 
                0
            ];
            var me = this;
            var el = this.getEl();
            el.addClass(this.side);
            el.setInnerHtml(text);
            var anchorEl = new api_dom.DivEl("Tooltip", "tooltip-anchor");
            el.appendChild(anchorEl.getHTMLElement());
            el.addEventListener("mouseover", function (event) {
                me.stopTimeout();
            });
            el.addEventListener("mouseout", function (event) {
                me.startTimeout();
            });
            var targetEl = target.getEl();
            targetEl.addEventListener("mouseover", function (event) {
                me.stopTimeout();
                if(!me.isVisible()) {
                    me.show();
                }
            });
            targetEl.addEventListener("mouseout", function (event) {
                me.startTimeout();
            });
            document.body.appendChild(this.getHTMLElement());
        }
        Tooltip.prototype.show = function () {
            _super.prototype.show.call(this);
            this.positionByTarget();
        };
        Tooltip.prototype.showFor = function (ms) {
            this.show();
            this.startTimeout(ms);
        };
        Tooltip.prototype.setTimeout = function (timeout) {
            this.timeout = timeout;
        };
        Tooltip.prototype.getTimeout = function () {
            return this.timeout;
        };
        Tooltip.prototype.setSide = function (side) {
            this.side = side;
        };
        Tooltip.prototype.getSide = function () {
            return this.side;
        };
        Tooltip.prototype.positionByTarget = function () {
            var targetEl = this.target.getHTMLElement();
            var targetOffset = this.target.getEl().getOffset();
            var el = this.getHTMLElement();
            var offsetLeft, offsetTop;
            switch(this.side) {
                case "top":
                    offsetLeft = targetOffset.left + (targetEl.offsetWidth - el.offsetWidth) / 2 + this.offset[0];
                    offsetTop = targetOffset.top - el.offsetHeight + this.offset[1];
                    break;
                case "bottom":
                    offsetLeft = targetOffset.left + (targetEl.offsetWidth - el.offsetWidth) / 2 + this.offset[0];
                    offsetTop = targetOffset.top + targetEl.offsetHeight + this.offset[1];
                    break;
                case "left":
                    offsetLeft = targetOffset.left - el.offsetWidth + this.offset[0];
                    offsetTop = targetOffset.top + (targetEl.offsetHeight - el.offsetHeight) / 2 + this.offset[1];
                    break;
                case "right":
                    offsetLeft = targetOffset.left + targetEl.offsetWidth + this.offset[0];
                    offsetTop = targetOffset.top + (targetEl.offsetHeight - el.offsetHeight) / 2 + this.offset[1];
                    break;
            }
            if(offsetLeft < 0) {
                offsetLeft = 0;
            } else if(offsetLeft + el.offsetWidth > document.body.clientWidth) {
                offsetLeft = document.body.clientWidth - el.offsetWidth;
            }
            if(offsetTop < 0) {
                offsetTop = 0;
            } else if(offsetTop + el.offsetHeight > document.body.clientHeight) {
                offsetTop = document.body.clientHeight - el.offsetHeight;
            }
            jQuery(this.getHTMLElement()).offset({
                left: offsetLeft,
                top: offsetTop
            });
        };
        Tooltip.prototype.startTimeout = function (ms) {
            this.stopTimeout();
            var me = this;
            if(this.timeout > 0) {
                this.hideTimeout = setTimeout(function () {
                    me.hide();
                }, ms || this.timeout);
            } else {
                me.hide();
            }
        };
        Tooltip.prototype.stopTimeout = function () {
            if(this.hideTimeout) {
                clearTimeout(this.hideTimeout);
                this.hideTimeout = undefined;
            }
        };
        return Tooltip;
    })(api_dom.DivEl);
    api_ui.Tooltip = Tooltip;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var ProgressBar = (function (_super) {
        __extends(ProgressBar, _super);
        function ProgressBar(value) {
                _super.call(this, "ProgressBar", "progress-bar");
            this.value = value || 0;
            var progress = this.progress = new api_dom.DivEl("ProgressBar", "progress-indicator");
            this.getEl().appendChild(progress.getHTMLElement());
            this.setValue(this.value);
        }
        ProgressBar.prototype.setValue = function (value) {
            var normalizedValue = value > 0 ? this.normalizeValue(value) : 0;
            this.progress.getEl().setWidth(normalizedValue * 100 + "%");
            this.value = normalizedValue;
        };
        ProgressBar.prototype.getValue = function () {
            return this.value;
        };
        ProgressBar.prototype.normalizeValue = function (value) {
            var integralLength = Math.ceil(Math.log(value) / Math.log(10));
            var maxValue = Math.pow(10, integralLength);
            return value / maxValue;
        };
        return ProgressBar;
    })(api_dom.DivEl);
    api_ui.ProgressBar = ProgressBar;    
})(api_ui || (api_ui = {}));
var api_ui_grid;
(function (api_ui_grid) {
    var TreeGridPanel = (function () {
        function TreeGridPanel(columns, gridStore, treeStore, gridConfig, treeConfig) {
            this.keyField = 'name';
            this.activeList = "grid";
            this.gridStore = gridStore;
            this.treeStore = treeStore;
            this.columns = columns;
            this.gridConfig = gridConfig;
            this.treeConfig = treeConfig;
        }
        TreeGridPanel.GRID = "grid";
        TreeGridPanel.TREE = "tree";
        TreeGridPanel.prototype.create = function (region, renderTo) {
            this.ext = new Ext.panel.Panel({
                region: region,
                renderTo: renderTo,
                flex: 1,
                layout: 'card',
                border: false,
                activeItem: this.activeList,
                itemId: this.itemId
            });
            this.ext.add(this.createGridPanel(this.gridStore, this.gridConfig));
            this.ext.add(this.createTreePanel(this.treeStore, this.treeConfig));
            return this;
        };
        TreeGridPanel.prototype.createGridPanel = function (gridStore, gridConfig) {
            var grid = new Ext.grid.Panel(Ext.apply({
                itemId: 'grid',
                cls: 'admin-grid',
                border: false,
                hideHeaders: true,
                columns: this.columns,
                viewConfig: {
                    trackOver: true,
                    stripeRows: true,
                    loadMask: {
                        store: gridStore
                    }
                },
                store: gridStore,
                selModel: Ext.create('Ext.selection.CheckboxModel', {
                    headerWidth: 36
                }),
                plugins: [
                    new Admin.plugin.PersistentGridSelectionPlugin({
                        keyField: this.keyField
                    })
                ]
            }, gridConfig));
            grid.addDocked(new Ext.toolbar.Toolbar({
                itemId: 'selectionToolbar',
                cls: 'admin-white-toolbar',
                dock: 'top',
                store: gridStore,
                gridPanel: grid,
                resultCountHidden: true,
                plugins: [
                    'gridToolbarPlugin'
                ]
            }));
            gridStore.on('datachanged', this.fireUpdateEvent, this);
            return grid;
        };
        TreeGridPanel.prototype.createTreePanel = function (treeStore, treeConfig) {
            var treeColumns = Ext.clone(this.columns);
            treeColumns[0].xtype = 'treecolumn';
            var tree = new Ext.tree.Panel(Ext.apply({
                xtype: 'treepanel',
                cls: 'admin-tree',
                hideHeaders: true,
                itemId: 'tree',
                useArrows: true,
                border: false,
                rootVisible: false,
                viewConfig: {
                    trackOver: true,
                    stripeRows: true,
                    loadMask: {
                        store: treeStore
                    }
                },
                store: treeStore,
                columns: treeColumns,
                plugins: [
                    new Admin.plugin.PersistentGridSelectionPlugin({
                        keyField: this.keyField
                    })
                ]
            }, treeConfig));
            tree.addDocked({
                xtype: 'toolbar',
                itemId: 'selectionToolbar',
                cls: 'admin-white-toolbar',
                dock: 'top',
                store: treeStore,
                gridPanel: tree,
                resultCountHidden: true,
                countTopLevelOnly: true,
                plugins: [
                    'gridToolbarPlugin'
                ]
            });
            treeStore.on('datachanged', this.fireUpdateEvent, this);
            return tree;
        };
        TreeGridPanel.prototype.fireUpdateEvent = function (values) {
            this.ext.fireEvent('datachanged', values);
        };
        TreeGridPanel.prototype.getActiveList = function () {
            return (this.ext.getLayout()).getActiveItem();
        };
        TreeGridPanel.prototype.setActiveList = function (listId) {
            this.activeList = listId;
            if(this.ext) {
                (this.ext.getLayout()).setActiveItem(listId);
            }
        };
        TreeGridPanel.prototype.setKeyField = function (keyField) {
            this.keyField = keyField;
        };
        TreeGridPanel.prototype.getKeyField = function () {
            return this.keyField;
        };
        TreeGridPanel.prototype.setItemId = function (itemId) {
            this.itemId = itemId;
        };
        TreeGridPanel.prototype.getItemId = function () {
            return this.itemId;
        };
        TreeGridPanel.prototype.refresh = function () {
            var activeStore = this.getActiveList().getStore();
            if(this.activeList == TreeGridPanel.GRID) {
                activeStore.loadPage(activeStore.currentPage);
            } else {
                activeStore.load();
            }
        };
        TreeGridPanel.prototype.removeAll = function () {
            var activeList = this.getActiveList();
            if(this.activeList == TreeGridPanel.GRID) {
                activeList.removeAll();
            } else {
                (activeList).getRootNode().removeAll();
            }
        };
        TreeGridPanel.prototype.deselect = function (key) {
            var activeList = this.getActiveList(), selModel = activeList.getSelectionModel();
            if(!key || key === -1) {
                selModel.deselectAll();
            } else {
                var selNodes = selModel.getSelection();
                var i;
                for(i = 0; i < selNodes.length; i++) {
                    var selNode = selNodes[i];
                    if(key == selNode.get(this.keyField)) {
                        selModel.deselect([
                            selNode
                        ]);
                    }
                }
            }
        };
        TreeGridPanel.prototype.getSelection = function () {
            var selection = [], activeList = this.getActiveList(), plugin = activeList.getPlugin('persistentGridSelection');
            if(plugin) {
                selection = plugin.getSelection();
            } else {
                selection = activeList.getSelectionModel().getSelection();
            }
            return selection;
        };
        TreeGridPanel.prototype.setRemoteSearchParams = function (params) {
            var activeStore = this.getActiveList().getStore();
            (activeStore.getProxy()).extraParams = params;
        };
        TreeGridPanel.prototype.setResultCountVisible = function (visible) {
            var plugin = this.getActiveList().getDockedComponent('selectionToolbar').getPlugin('gridToolbarPlugin');
            plugin.setResultCountVisible(visible);
        };
        TreeGridPanel.prototype.updateResultCount = function (count) {
            var plugin = this.getActiveList().getDockedComponent('selectionToolbar').getPlugin('gridToolbarPlugin');
            plugin.updateResultCount(count);
        };
        return TreeGridPanel;
    })();
    api_ui_grid.TreeGridPanel = TreeGridPanel;    
})(api_ui_grid || (api_ui_grid = {}));
var api_appbar;
(function (api_appbar) {
    var AppBar = (function (_super) {
        __extends(AppBar, _super);
        function AppBar(appName, actions, tabMenu) {
            var _this = this;
                _super.call(this, 'AppBar', 'appbar');
            this.appName = appName;
            this.actions = actions;
            this.tabMenu = tabMenu;
            this.launcherButton = new api_appbar.LauncherButton(actions.showAppLauncherAction);
            this.appendChild(this.launcherButton);
            var separator = new api_appbar.Separator();
            this.appendChild(separator);
            this.homeButton = new api_appbar.HomeButton(this.appName, actions.showAppBrowsePanelAction);
            this.appendChild(this.homeButton);
            this.userButton = new api_appbar.UserButton();
            this.appendChild(this.userButton);
            if(this.tabMenu != null) {
                this.appendChild(this.tabMenu);
            } else {
                this.appendChild(new TabMenuContainer());
            }
            this.userInfoPopup = new api_appbar.UserInfoPopup();
            this.userButton.getEl().addEventListener('click', function (event) {
                _this.userInfoPopup.toggle();
            });
            this.initExt();
        }
        AppBar.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                cls: 'appbar-container',
                region: 'north'
            });
        };
        AppBar.prototype.getTabMenu = function () {
            return this.tabMenu;
        };
        return AppBar;
    })(api_dom.DivEl);
    api_appbar.AppBar = AppBar;    
    var LauncherButton = (function (_super) {
        __extends(LauncherButton, _super);
        function LauncherButton(action) {
                _super.call(this, 'LauncherButton', 'launcher-button');
            this.getEl().addEventListener('click', function (event) {
                action.execute();
            });
        }
        return LauncherButton;
    })(api_dom.ButtonEl);
    api_appbar.LauncherButton = LauncherButton;    
    var Separator = (function (_super) {
        __extends(Separator, _super);
        function Separator() {
                _super.call(this, 'AppBarSeparator', 'appbar-separator');
        }
        return Separator;
    })(api_dom.SpanEl);
    api_appbar.Separator = Separator;    
    var HomeButton = (function (_super) {
        __extends(HomeButton, _super);
        function HomeButton(text, action) {
                _super.call(this, 'HomeButton', 'home-button');
            this.getEl().setInnerHtml(text);
            this.getEl().addEventListener('click', function (event) {
                action.execute();
            });
        }
        return HomeButton;
    })(api_dom.ButtonEl);
    api_appbar.HomeButton = HomeButton;    
    var TabMenuContainer = (function (_super) {
        __extends(TabMenuContainer, _super);
        function TabMenuContainer() {
                _super.call(this, 'TabMenuContainer', 'tabmenu-container');
        }
        return TabMenuContainer;
    })(api_dom.DivEl);
    api_appbar.TabMenuContainer = TabMenuContainer;    
    var UserButton = (function (_super) {
        __extends(UserButton, _super);
        function UserButton() {
                _super.call(this, 'UserButton', 'user-button');
            var photoUrl = api_util.getAbsoluteUri('admin/resources/images/tsi-profil.jpg');
            this.setIcon(photoUrl);
        }
        UserButton.prototype.setIcon = function (photoUrl) {
            this.getEl().setBackgroundImage('url("' + photoUrl + '")');
        };
        return UserButton;
    })(api_dom.ButtonEl);
    api_appbar.UserButton = UserButton;    
})(api_appbar || (api_appbar = {}));
var api_appbar;
(function (api_appbar) {
    var UserInfoPopup = (function (_super) {
        __extends(UserInfoPopup, _super);
        function UserInfoPopup() {
                _super.call(this, 'UserInfoPopup', 'user-info-popup');
            this.isShown = false;
            this.createContent();
            this.render();
        }
        UserInfoPopup.prototype.createContent = function () {
            var userName = 'Thomas Lund Sigdestad', photoUrl = api_util.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'), qName = 'system/tsi';
            var content = '<div class="title">User</div>' + '<div class="user-name">' + userName + '</div>' + '<div class="content">' + '<div class="column">' + '<img src="' + photoUrl + '"/>' + '<button>Log Out</button>' + '</div>' + '<div class="column">' + '<span>' + qName + '</span>' + '<a href="#">View Profile</a>' + '<a href="#">Edit Profile</a>' + '<a href="#">Change User</a>' + '</div>' + '</div>';
            this.getEl().setInnerHtml(content);
        };
        UserInfoPopup.prototype.render = function () {
            this.hide();
            this.isShown = false;
            document.body.insertBefore(this.getHTMLElement());
        };
        UserInfoPopup.prototype.toggle = function () {
            this.isShown ? this.hide() : this.show();
            this.isShown = !this.isShown;
        };
        return UserInfoPopup;
    })(api_dom.DivEl);
    api_appbar.UserInfoPopup = UserInfoPopup;    
})(api_appbar || (api_appbar = {}));
var api_appbar;
(function (api_appbar) {
    var ShowAppLauncherEvent = (function (_super) {
        __extends(ShowAppLauncherEvent, _super);
        function ShowAppLauncherEvent() {
                _super.call(this, 'showAppLauncher');
        }
        ShowAppLauncherEvent.on = function on(handler) {
            api_event.onEvent('showAppLauncher', handler);
        };
        return ShowAppLauncherEvent;
    })(api_event.Event);
    api_appbar.ShowAppLauncherEvent = ShowAppLauncherEvent;    
    var ShowAppBrowsePanelEvent = (function (_super) {
        __extends(ShowAppBrowsePanelEvent, _super);
        function ShowAppBrowsePanelEvent() {
                _super.call(this, 'showAppBrowsePanel');
        }
        ShowAppBrowsePanelEvent.on = function on(handler) {
            api_event.onEvent('showAppBrowsePanel', handler);
        };
        return ShowAppBrowsePanelEvent;
    })(api_event.Event);
    api_appbar.ShowAppBrowsePanelEvent = ShowAppBrowsePanelEvent;    
})(api_appbar || (api_appbar = {}));
var api_appbar;
(function (api_appbar) {
    var AppBarTabMenu = (function (_super) {
        __extends(AppBarTabMenu, _super);
        function AppBarTabMenu(idPrefix) {
                _super.call(this, idPrefix || "AppBarTabMenu");
            this.getEl().addClass("appbar-tabmenu");
        }
        AppBarTabMenu.prototype.addTab = function (tab) {
            _super.prototype.addTab.call(this, tab);
            this.tabMenuButton.setTabCount(this.getSize());
            this.tabMenuButton.show();
        };
        AppBarTabMenu.prototype.createTabMenuButton = function () {
            this.tabMenuButton = new api_appbar.AppBarTabMenuButton();
            return this.tabMenuButton;
        };
        AppBarTabMenu.prototype.removeTab = function (tab) {
            _super.prototype.removeTab.call(this, tab);
            this.tabMenuButton.setTabCount(this.getSize());
            if(this.getSize() == 0) {
                this.tabMenuButton.setLabel("");
                this.tabMenuButton.hide();
            }
        };
        return AppBarTabMenu;
    })(api_ui_tab.TabMenu);
    api_appbar.AppBarTabMenu = AppBarTabMenu;    
})(api_appbar || (api_appbar = {}));
var api_appbar;
(function (api_appbar) {
    var AppBarTabMenuButton = (function (_super) {
        __extends(AppBarTabMenuButton, _super);
        function AppBarTabMenuButton(idPrefix) {
                _super.call(this, idPrefix || "AppBarTabMenuButton");
            this.getEl().addClass("appbar-tabmenu-button");
            this.iconEl = new api_dom.SpanEl();
            this.iconEl.getEl().addClass("icon-icomoon-pencil-32");
            this.prependChild(this.iconEl);
            this.tabCountEl = new AppBarTabCount();
            this.appendChild(this.tabCountEl);
        }
        AppBarTabMenuButton.prototype.setTabCount = function (value) {
            this.tabCountEl.setCount(value);
        };
        return AppBarTabMenuButton;
    })(api_ui_tab.TabMenuButton);
    api_appbar.AppBarTabMenuButton = AppBarTabMenuButton;    
    var AppBarTabCount = (function (_super) {
        __extends(AppBarTabCount, _super);
        function AppBarTabCount() {
                _super.call(this);
            this.getEl().addClass("tabcount");
        }
        AppBarTabCount.prototype.setCount = function (value) {
            if(value > 0) {
                this.getEl().setInnerHtml("" + value);
            } else {
                this.getEl().setInnerHtml("");
            }
        };
        return AppBarTabCount;
    })(api_dom.SpanEl);
    api_appbar.AppBarTabCount = AppBarTabCount;    
})(api_appbar || (api_appbar = {}));
var api_appbar;
(function (api_appbar) {
    var AppBarTabMenuItem = (function (_super) {
        __extends(AppBarTabMenuItem, _super);
        function AppBarTabMenuItem(label) {
                _super.call(this, label);
        }
        return AppBarTabMenuItem;
    })(api_ui_tab.TabMenuItem);
    api_appbar.AppBarTabMenuItem = AppBarTabMenuItem;    
})(api_appbar || (api_appbar = {}));
var api;
(function (api) {
    var AppPanel = (function (_super) {
        __extends(AppPanel, _super);
        function AppPanel(appBar, homePanel) {
                _super.call(this, appBar);
            this.homePanel = homePanel;
            var homePanelMenuItem = new api_appbar.AppBarTabMenuItem("home");
            homePanelMenuItem.setVisible(false);
            homePanelMenuItem.setRemovable(false);
            this.addTab(homePanelMenuItem, this.homePanel);
            this.showPanel(0);
        }
        AppPanel.prototype.showBrowsePanel = function () {
            this.showPanel(0);
        };
        return AppPanel;
    })(api_ui_tab.TabbedDeckPanel);
    api.AppPanel = AppPanel;    
})(api || (api = {}));
var api_ui_dialog;
(function (api_ui_dialog) {
    var DialogButton = (function (_super) {
        __extends(DialogButton, _super);
        function DialogButton(action) {
            var _this = this;
                _super.call(this, "DialogButton", action.getLabel());
            this.getEl().addClass("DialogButton");
            this.action = action;
            this.getEl().addEventListener("click", function () {
                _this.action.execute();
            });
            this.setEnable(action.isEnabled());
            action.addPropertyChangeListener(function (action) {
                _this.setEnable(action.isEnabled());
            });
        }
        return DialogButton;
    })(api_ui.AbstractButton);
    api_ui_dialog.DialogButton = DialogButton;    
})(api_ui_dialog || (api_ui_dialog = {}));
var api_ui_dialog;
(function (api_ui_dialog) {
    var ModalDialog = (function (_super) {
        __extends(ModalDialog, _super);
        function ModalDialog(config) {
                _super.call(this, "ModalDialog", "modal-dialog");
            this.config = config;
            var el = this.getEl();
            el.setDisplay("none");
            el.setWidth(this.config.width + "px").setHeight(this.config.height + "px");
            el.setZindex(30001);
            el.setPosition("fixed").setTop("50%").setLeft("50%").setMarginLeft("-" + (this.config.width / 2) + "px").setMarginTop("-" + (this.config.height / 2) + "px");
            this.title = new ModalDialogTitle(this.config.title);
            this.appendChild(this.title);
            this.contentPanel = new ModalDialogContentPanel();
            this.appendChild(this.contentPanel);
            this.buttonRow = new ModalDialogButtonRow();
            this.appendChild(this.buttonRow);
        }
        ModalDialog.prototype.setTitle = function (value) {
            this.title.setTitle(value);
        };
        ModalDialog.prototype.appendChildToContentPanel = function (child) {
            this.contentPanel.appendChild(child);
        };
        ModalDialog.prototype.addAction = function (action) {
            this.buttonRow.addAction(action);
        };
        ModalDialog.prototype.show = function () {
            jQuery(this.getEl().getHTMLElement()).show(100);
        };
        ModalDialog.prototype.hide = function () {
            jQuery(this.getEl().getHTMLElement()).hide(100);
        };
        ModalDialog.prototype.close = function () {
            api_ui.BodyMask.get().deActivate();
            this.hide();
            Mousetrap.unbind('esc');
        };
        ModalDialog.prototype.open = function () {
            var _this = this;
            api_ui.BodyMask.get().activate();
            this.show();
            Mousetrap.bind('esc', function () {
                _this.close();
            });
        };
        return ModalDialog;
    })(api_dom.DivEl);
    api_ui_dialog.ModalDialog = ModalDialog;    
    var ModalDialogTitle = (function (_super) {
        __extends(ModalDialogTitle, _super);
        function ModalDialogTitle(title) {
                _super.call(this, "ModalDialogTitle");
            this.getEl().setInnerHtml(title);
        }
        ModalDialogTitle.prototype.setTitle = function (value) {
            this.getEl().setInnerHtml(value);
        };
        return ModalDialogTitle;
    })(api_dom.H2El);
    api_ui_dialog.ModalDialogTitle = ModalDialogTitle;    
    var ModalDialogContentPanel = (function (_super) {
        __extends(ModalDialogContentPanel, _super);
        function ModalDialogContentPanel() {
                _super.call(this, "ModalDialogContentPanel", "content-panel");
        }
        return ModalDialogContentPanel;
    })(api_dom.DivEl);
    api_ui_dialog.ModalDialogContentPanel = ModalDialogContentPanel;    
    var ModalDialogButtonRow = (function (_super) {
        __extends(ModalDialogButtonRow, _super);
        function ModalDialogButtonRow() {
                _super.call(this, "ModalDialogButtonRow", "button-row");
        }
        ModalDialogButtonRow.prototype.addAction = function (action) {
            var button = new ModalDialogButton(action);
            this.appendChild(button);
        };
        return ModalDialogButtonRow;
    })(api_dom.DivEl);
    api_ui_dialog.ModalDialogButtonRow = ModalDialogButtonRow;    
    var ModalDialogButton = (function (_super) {
        __extends(ModalDialogButton, _super);
        function ModalDialogButton(action) {
            var _this = this;
                _super.call(this, "ModalDialogButton", action.getLabel());
            this.action = action;
            this.getEl().addEventListener("click", function () {
                _this.action.execute();
            });
            _super.prototype.setEnable.call(this, action.isEnabled());
            action.addPropertyChangeListener(function (action) {
                _this.setEnable(action.isEnabled());
            });
        }
        return ModalDialogButton;
    })(api_ui.AbstractButton);
    api_ui_dialog.ModalDialogButton = ModalDialogButton;    
    var ModalDialogCancelAction = (function (_super) {
        __extends(ModalDialogCancelAction, _super);
        function ModalDialogCancelAction() {
                _super.call(this, "Cancel");
        }
        return ModalDialogCancelAction;
    })(api_ui.Action);
    api_ui_dialog.ModalDialogCancelAction = ModalDialogCancelAction;    
})(api_ui_dialog || (api_ui_dialog = {}));
var api_delete;
(function (api_delete) {
    var DeleteItem = (function () {
        function DeleteItem(iconUrl, displayName) {
            this.iconUrl = iconUrl;
            this.displayName = displayName;
        }
        DeleteItem.prototype.getDisplayName = function () {
            return this.displayName;
        };
        DeleteItem.prototype.getIconUrl = function () {
            return this.iconUrl;
        };
        return DeleteItem;
    })();
    api_delete.DeleteItem = DeleteItem;    
})(api_delete || (api_delete = {}));
var api_delete;
(function (api_delete) {
    var DeleteDialog = (function (_super) {
        __extends(DeleteDialog, _super);
        function DeleteDialog(modelName) {
            var _this = this;
                _super.call(this, {
        title: "Delete " + modelName,
        width: 500,
        height: 300
    });
            this.cancelAction = new CancelDeleteDialogAction();
            this.itemList = new DeleteDialogItemList();
            this.modelName = modelName;
            this.getEl().addClass("delete-dialog");
            this.appendChildToContentPanel(this.itemList);
            this.addAction(this.cancelAction);
            this.cancelAction.addExecutionListener(function () {
                _this.close();
            });
        }
        DeleteDialog.prototype.setDeleteAction = function (action) {
            this.deleteAction = action;
            this.addAction(action);
        };
        DeleteDialog.prototype.setDeleteItems = function (deleteItems) {
            this.deleteItems = deleteItems;
            this.itemList.clear();
            if(deleteItems.length > 1) {
                this.setTitle("Delete " + this.modelName + "s");
            } else {
                this.setTitle("Delete " + this.modelName);
            }
            for(var i in this.deleteItems) {
                var deleteItem = this.deleteItems[i];
                this.itemList.appendChild(new DeleteDialogItemComponent(deleteItem));
            }
        };
        return DeleteDialog;
    })(api_ui_dialog.ModalDialog);
    api_delete.DeleteDialog = DeleteDialog;    
    var CancelDeleteDialogAction = (function (_super) {
        __extends(CancelDeleteDialogAction, _super);
        function CancelDeleteDialogAction() {
                _super.call(this, "Cancel");
        }
        return CancelDeleteDialogAction;
    })(api_ui.Action);
    api_delete.CancelDeleteDialogAction = CancelDeleteDialogAction;    
    var DeleteDialogItemList = (function (_super) {
        __extends(DeleteDialogItemList, _super);
        function DeleteDialogItemList() {
                _super.call(this, "DeleteDialogItemList");
            this.getEl().addClass("item-list");
        }
        DeleteDialogItemList.prototype.clear = function () {
            this.removeChildren();
        };
        return DeleteDialogItemList;
    })(api_dom.DivEl);
    api_delete.DeleteDialogItemList = DeleteDialogItemList;    
    var DeleteDialogItemComponent = (function (_super) {
        __extends(DeleteDialogItemComponent, _super);
        function DeleteDialogItemComponent(deleteItem) {
                _super.call(this, "DeleteDialogItem");
            this.getEl().addClass("item");
            var icon = new api_dom.ImgEl(deleteItem.getIconUrl());
            this.appendChild(icon);
            var displayName = new api_dom.H4El();
            displayName.getEl().setInnerHtml(deleteItem.getDisplayName());
            this.appendChild(displayName);
        }
        return DeleteDialogItemComponent;
    })(api_dom.DivEl);    
})(api_delete || (api_delete = {}));
var api_browse;
(function (api_browse) {
    var AppBrowsePanel = (function (_super) {
        __extends(AppBrowsePanel, _super);
        function AppBrowsePanel(browseToolbar, grid, detailPanel, filterPanel) {
                _super.call(this, "AppBrowsePanel");
            this.browseToolbar = browseToolbar;
            this.grid = grid;
            this.detailPanel = detailPanel;
            this.filterPanel = filterPanel;
        }
        AppBrowsePanel.prototype.init = function () {
            this.appendChild(this.browseToolbar);
            this.grid.create('center', this.getId());
            this.appendChild(this.detailPanel);
        };
        AppBrowsePanel.prototype.initExt = function () {
            var center = new Ext.container.Container({
                region: 'center',
                layout: 'border'
            });
            center.add(this.browseToolbar.ext);
            center.add(this.grid.ext);
            center.add(this.detailPanel.ext);
            this.ext = new Ext.panel.Panel({
                id: 'tab-browse',
                title: 'Browse',
                closable: false,
                border: false,
                layout: 'border',
                tabConfig: {
                    hidden: true
                }
            });
            this.ext.add(center);
            this.ext.add(this.filterPanel);
        };
        return AppBrowsePanel;
    })(api_ui.Panel);
    api_browse.AppBrowsePanel = AppBrowsePanel;    
})(api_browse || (api_browse = {}));
var api_browse;
(function (api_browse) {
    var DetailPanel = (function (_super) {
        __extends(DetailPanel, _super);
        function DetailPanel() {
                _super.call(this, "detailpanel", "detailpanel");
            this.initExt();
        }
        DetailPanel.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'south',
                split: true
            });
        };
        return DetailPanel;
    })(api_dom.DivEl);
    api_browse.DetailPanel = DetailPanel;    
    var DetailTabPanel = (function (_super) {
        __extends(DetailTabPanel, _super);
        function DetailTabPanel(model) {
            var _this = this;
                _super.call(this, "detailpanel-tab", "detailpanel-tab");
            this.tabs = [];
            this.model = model;
            this.addHeader(model.data.name, model.id, model.data.iconUrl);
            this.addNavigation();
            this.addCanvas();
            this.setTabChangeCallback(function (tab) {
                _this.setActiveTab(tab);
            });
        }
        DetailTabPanel.prototype.addHeader = function (title, subtitle, iconUrl) {
            var headerEl = new api_dom.DivEl("header", "header");
            var iconEl = api_util.ImageLoader.get(iconUrl + "?size=80", 80, 80);
            var hgroupEl = new api_dom.Element("hgroup");
            var headerTextEl = new api_dom.H1El();
            headerTextEl.getEl().setInnerHtml(title);
            hgroupEl.appendChild(headerTextEl);
            var subtitleEl = new api_dom.H4El();
            subtitleEl.getEl().setInnerHtml(subtitle);
            hgroupEl.appendChild(subtitleEl);
            headerEl.getEl().appendChild(iconEl);
            headerEl.appendChild(hgroupEl);
            headerEl.appendChild(this.createActionMenu());
            this.appendChild(headerEl);
        };
        DetailTabPanel.prototype.addCanvas = function () {
            var canvasEl = this.canvas = new api_dom.DivEl("canvas", "canvas");
            this.appendChild(canvasEl);
        };
        DetailTabPanel.prototype.setTabChangeCallback = function (callback) {
            this.tabChangeCallback = callback;
        };
        DetailTabPanel.prototype.addTab = function (tab) {
            this.tabs.push(tab);
            this.navigation.addTab(tab, this.tabChangeCallback);
        };
        DetailTabPanel.prototype.setActiveTab = function (tab) {
            this.canvas.empty();
            this.canvas.appendChild(tab.content);
        };
        DetailTabPanel.prototype.addAction = function (action) {
            this.actionMenu.addAction(action);
        };
        DetailTabPanel.prototype.createActionMenu = function () {
            this.actionMenu = new api_ui_menu.ActionMenu();
            return new api_ui_menu.ActionMenuButton(this.actionMenu);
        };
        DetailTabPanel.prototype.addNavigation = function () {
            this.navigation = new DetailPanelTabList();
            this.getEl().appendChild(this.navigation.getHTMLElement());
        };
        return DetailTabPanel;
    })(api_dom.DivEl);
    api_browse.DetailTabPanel = DetailTabPanel;    
    var DetailPanelTab = (function () {
        function DetailPanelTab(name) {
            this.name = name;
            this.content = new api_dom.DivEl("test-content");
            this.content.getEl().setInnerHtml(this.name);
        }
        return DetailPanelTab;
    })();
    api_browse.DetailPanelTab = DetailPanelTab;    
    var DetailPanelTabList = (function (_super) {
        __extends(DetailPanelTabList, _super);
        function DetailPanelTabList() {
                _super.call(this, "tab-list", "tab-list");
            this.tabs = [];
        }
        DetailPanelTabList.prototype.addTab = function (tab, clickCallback) {
            var _this = this;
            var tabEl = new api_dom.LiEl("tab");
            this.tabs.push(tabEl);
            tabEl.getEl().setInnerHtml(tab.name);
            tabEl.getEl().addEventListener("click", function (event) {
                _this.selectTab(tabEl);
                clickCallback(tab);
            });
            this.getEl().appendChild(tabEl.getHTMLElement());
        };
        DetailPanelTabList.prototype.selectTab = function (tab) {
            this.tabs.forEach(function (entry) {
                entry.getEl().removeClass("active");
            });
            tab.getEl().addClass("active");
        };
        return DetailPanelTabList;
    })(api_dom.UlEl);
    api_browse.DetailPanelTabList = DetailPanelTabList;    
    var DetailPanelBox = (function (_super) {
        __extends(DetailPanelBox, _super);
        function DetailPanelBox(model, removeCallback) {
                _super.call(this, "detailpanel-box", "detailpanel-box");
            this.model = model;
            this.setIcon(model.data.iconUrl, 32);
            this.setData(model.data.displayName, model.data.name);
            this.addRemoveButton(removeCallback);
        }
        DetailPanelBox.prototype.addRemoveButton = function (callback) {
            var _this = this;
            var removeEl = document.createElement("div");
            removeEl.className = "remove";
            removeEl.innerHTML = "&times;";
            removeEl.addEventListener("click", function (event) {
                _this.getEl().remove();
                if(callback) {
                    callback(_this);
                }
            });
            this.getEl().appendChild(removeEl);
        };
        DetailPanelBox.prototype.setIcon = function (iconUrl, size) {
            this.getEl().appendChild(api_util.ImageLoader.get(iconUrl + "?size=" + size, 32, 32));
        };
        DetailPanelBox.prototype.setData = function (title, subtitle) {
            var titleEl = document.createElement("h6");
            titleEl.innerHTML = title;
            var subtitleEl = document.createElement("small");
            subtitleEl.innerHTML = subtitle;
            titleEl.appendChild(subtitleEl);
            this.getEl().appendChild(titleEl);
            return titleEl;
        };
        DetailPanelBox.prototype.getModel = function () {
            return this.model;
        };
        return DetailPanelBox;
    })(api_dom.DivEl);
    api_browse.DetailPanelBox = DetailPanelBox;    
})(api_browse || (api_browse = {}));
var api_wizard;
(function (api_wizard) {
    var FormIcon = (function (_super) {
        __extends(FormIcon, _super);
        function FormIcon(iconUrl, iconTitle, uploadUrl) {
            var _this = this;
                _super.call(this, "FormIcon", "form-icon");
            this.iconUrl = iconUrl;
            this.iconTitle = iconTitle;
            this.uploadUrl = uploadUrl;
            var el = this.getEl();
            var me = this;
            this.tooltip = new api_ui.Tooltip(this, iconTitle, 10, "bottom", [
                0, 
                5
            ]);
            var img = this.img = new api_dom.ImgEl(this.iconUrl, "FormIcon");
            img.getEl().addEventListener("load", function () {
                _this.tooltip.showFor(10000);
            });
            el.appendChild(img.getHTMLElement());
            if(this.uploadUrl) {
                this.progress = new api_ui.ProgressBar();
                el.appendChild(this.progress.getHTMLElement());
                var firstClickHandler = function (event) {
                    if(!me.uploader) {
                        if(!plupload) {
                            console.log('FormIcon: plupload not found, check if it is included in page.');
                        } else {
                            me.uploader = me.initUploader(me.getId());
                            me.getHTMLElement().click();
                        }
                    }
                    me.getEl().removeEventListener("click", firstClickHandler);
                };
                this.getEl().addEventListener("click", firstClickHandler);
            }
            this.ext = this.initExt();
        }
        FormIcon.prototype.initExt = function () {
            var me = this;
            return new Ext.Component({
                contentEl: this.getHTMLElement()
            });
        };
        FormIcon.prototype.initUploader = function (elId) {
            var _this = this;
            var uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: false,
                browse_button: elId,
                url: this.uploadUrl,
                multipart: true,
                drop_element: elId,
                flash_swf_url: 'common/js/fileupload/plupload/js/plupload.flash.swf',
                silverlight_xap_url: 'common/js/fileupload/plupload/js/plupload.silverlight.xap',
                filters: [
                    {
                        title: 'Image files',
                        extensions: 'jpg,gif,png'
                    }
                ]
            });
            uploader.bind('Init', function (up, params) {
                console.log('uploader init', up, params);
            });
            uploader.bind('FilesAdded', function (up, files) {
                console.log('uploader files added', up, files);
            });
            uploader.bind('QueueChanged', function (up) {
                console.log('uploader queue changed', up);
                up.start();
            });
            uploader.bind('UploadFile', function (up, file) {
                console.log('uploader upload file', up, file);
                _this.progress.show();
            });
            uploader.bind('UploadProgress', function (up, file) {
                console.log('uploader upload progress', up, file);
                _this.progress.setValue(file.percent);
            });
            uploader.bind('FileUploaded', function (up, file, response) {
                console.log('uploader file uploaded', up, file, response);
                var responseObj, uploadedResUrl;
                if(response && response.status === 200) {
                    responseObj = Ext.decode(response.response);
                    uploadedResUrl = (responseObj.items && responseObj.items.length > 0) ? 'rest/upload/' + responseObj.items[0].id : 'resources/images/x-user-photo.png';
                    _this.setSrc(uploadedResUrl);
                }
                _this.progress.hide();
            });
            uploader.bind('UploadComplete', function (up, files) {
                console.log('uploader upload complete', up, files);
            });
            uploader.init();
            return uploader;
        };
        FormIcon.prototype.setSrc = function (src) {
            this.img.getEl().setSrc(src);
        };
        return FormIcon;
    })(api_dom.ButtonEl);
    api_wizard.FormIcon = FormIcon;    
})(api_wizard || (api_wizard = {}));
var api_wizard;
(function (api_wizard) {
    var WizardPanel = (function (_super) {
        __extends(WizardPanel, _super);
        function WizardPanel() {
                _super.call(this, "wizard-panel");
            this.steps = [];
            this.getEl().addClass("wizard-panel");
            this.addTitle();
            this.addSubTitle();
            this.wizardStepPanels = new WizardStepPanels();
            this.addStepContainer();
            this.appendChild(this.wizardStepPanels);
            this.initExt();
        }
        WizardPanel.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        };
        WizardPanel.prototype.setTitle = function (title) {
            this.titleEl.getEl().setValue(title);
        };
        WizardPanel.prototype.setSubtitle = function (subtitle) {
            this.subTitleEl.getEl().setValue(subtitle);
        };
        WizardPanel.prototype.addStep = function (step) {
            this.steps.push(step);
            this.stepContainer.addStep(step);
        };
        WizardPanel.prototype.addIcon = function (icon) {
            this.getEl().insertBefore(icon, this.titleEl);
        };
        WizardPanel.prototype.addToolbar = function (toolbar) {
            this.getEl().insertBefore(toolbar, this.titleEl);
        };
        WizardPanel.prototype.addTitle = function () {
            this.titleEl = new api_dom.Element("input", "title");
            this.titleEl.getEl().addClass("title");
            this.appendChild(this.titleEl);
        };
        WizardPanel.prototype.addSubTitle = function () {
            this.subTitleEl = new api_dom.Element("input", "title");
            this.subTitleEl.getEl().addClass("subtitle");
            this.appendChild(this.subTitleEl);
        };
        WizardPanel.prototype.addStepContainer = function () {
            var stepContainerEl = new WizardStepContainer(this.wizardStepPanels);
            this.stepContainer = stepContainerEl;
            this.appendChild(stepContainerEl);
        };
        return WizardPanel;
    })(api_ui.Panel);
    api_wizard.WizardPanel = WizardPanel;    
    var WizardStepPanels = (function (_super) {
        __extends(WizardStepPanels, _super);
        function WizardStepPanels() {
                _super.call(this, "WizardStepPanels");
        }
        return WizardStepPanels;
    })(api_ui.DeckPanel);
    api_wizard.WizardStepPanels = WizardStepPanels;    
    var WizardStepContainer = (function (_super) {
        __extends(WizardStepContainer, _super);
        function WizardStepContainer(deckPanel) {
                _super.call(this, "step-container", "step-container");
            this.steps = [];
            this.deckPanel = deckPanel;
        }
        WizardStepContainer.prototype.addStep = function (step) {
            var _this = this;
            this.steps.push(step);
            var panelIndex = this.deckPanel.addPanel(step.getPanel());
            if(panelIndex == 0) {
                this.deckPanel.showPanel(0);
            }
            var stepEl = new api_dom.LiEl(step.getLabel());
            step.setEl(stepEl);
            stepEl.getEl().setInnerHtml(step.getLabel());
            stepEl.getEl().addEventListener("click", function (event) {
                _this.removeActive();
                step.setActive(true);
                _this.deckPanel.showPanel(panelIndex);
            });
            if(this.steps.length == 1) {
                step.setActive(true);
            }
            this.appendChild(stepEl);
        };
        WizardStepContainer.prototype.removeActive = function () {
            this.steps.forEach(function (step) {
                step.setActive(false);
            });
        };
        return WizardStepContainer;
    })(api_dom.UlEl);
    api_wizard.WizardStepContainer = WizardStepContainer;    
    var WizardStep = (function () {
        function WizardStep(label, panel) {
            this.label = label;
            this.panel = panel;
        }
        WizardStep.prototype.setEl = function (el) {
            this.el = el;
        };
        WizardStep.prototype.setActive = function (active) {
            this.active = active;
            if(active) {
                this.el.getEl().addClass("active");
            } else {
                this.el.getEl().removeClass("active");
            }
        };
        WizardStep.prototype.isActive = function () {
            return this.active;
        };
        WizardStep.prototype.getEl = function () {
            return this.el;
        };
        WizardStep.prototype.getLabel = function () {
            return this.label;
        };
        WizardStep.prototype.getPanel = function () {
            return this.panel;
        };
        return WizardStep;
    })();
    api_wizard.WizardStep = WizardStep;    
})(api_wizard || (api_wizard = {}));
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
