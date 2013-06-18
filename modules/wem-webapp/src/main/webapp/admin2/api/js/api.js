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
    var ElementHelper = (function () {
        function ElementHelper(element) {
            this.el = element;
        }
        ElementHelper.fromName = function fromName(name) {
            return new api_ui.ElementHelper(document.createElement(name));
        };
        ElementHelper.prototype.getHTMLElement = function () {
            return this.el;
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
        ElementHelper.prototype.appendChild = function (child) {
            this.el.appendChild(child);
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
        ElementHelper.prototype.setPosition = function (value) {
            this.el.style.position = value;
            return this;
        };
        ElementHelper.prototype.setWidth = function (value) {
            this.el.style.width = value;
            return this;
        };
        ElementHelper.prototype.setHeight = function (value) {
            this.el.style.height = value;
            return this;
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
        return ElementHelper;
    })();
    api_ui.ElementHelper = ElementHelper;    
})(api_ui || (api_ui = {}));
var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var api_ui;
(function (api_ui) {
    var ImgHelper = (function (_super) {
        __extends(ImgHelper, _super);
        function ImgHelper(element) {
                _super.call(this, element);
            this.el = element;
        }
        ImgHelper.create = function create() {
            return new api_ui.ImgHelper(document.createElement("img"));
        };
        ImgHelper.prototype.getHTMLElement = function () {
            return this.el;
        };
        ImgHelper.prototype.setSrc = function (value) {
            this.el.src = value;
            return this;
        };
        return ImgHelper;
    })(api_ui.ElementHelper);
    api_ui.ImgHelper = ImgHelper;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var Element = (function () {
        function Element(elementName, idPrefix, className, elHelper) {
            if(elHelper == null) {
                this.el = api_ui.ElementHelper.fromName(elementName);
            } else {
                this.el = elHelper;
            }
            if(idPrefix != null) {
                this.id = idPrefix + '-' + (++api_ui.Element.constructorCounter);
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
            jQuery(this.el.getHTMLElement()).prepend(child.getHTMLElement());
        };
        Element.prototype.removeChildren = function () {
            var htmlEl = this.el.getHTMLElement();
            while(htmlEl.firstChild) {
                htmlEl.removeChild(htmlEl.firstChild);
            }
        };
        return Element;
    })();
    api_ui.Element = Element;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var DivEl = (function (_super) {
        __extends(DivEl, _super);
        function DivEl(idPrefix, className) {
                _super.call(this, "div", idPrefix, className);
        }
        return DivEl;
    })(api_ui.Element);
    api_ui.DivEl = DivEl;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var H1El = (function (_super) {
        __extends(H1El, _super);
        function H1El(idPrefix, className) {
                _super.call(this, "h1", idPrefix, className);
        }
        return H1El;
    })(api_ui.Element);
    api_ui.H1El = H1El;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var H2El = (function (_super) {
        __extends(H2El, _super);
        function H2El(idPrefix, className) {
                _super.call(this, "h2", idPrefix, className);
        }
        return H2El;
    })(api_ui.Element);
    api_ui.H2El = H2El;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var H3El = (function (_super) {
        __extends(H3El, _super);
        function H3El(idPrefix, className) {
                _super.call(this, "h3", idPrefix, className);
        }
        return H3El;
    })(api_ui.Element);
    api_ui.H3El = H3El;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var H4El = (function (_super) {
        __extends(H4El, _super);
        function H4El(idPrefix, className) {
                _super.call(this, "h4", idPrefix, className);
        }
        return H4El;
    })(api_ui.Element);
    api_ui.H4El = H4El;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var UlEl = (function (_super) {
        __extends(UlEl, _super);
        function UlEl(idPrefix, className) {
                _super.call(this, "ul", idPrefix, className);
        }
        return UlEl;
    })(api_ui.Element);
    api_ui.UlEl = UlEl;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var LiEl = (function (_super) {
        __extends(LiEl, _super);
        function LiEl(idPrefix, className) {
                _super.call(this, "li", idPrefix, className);
        }
        return LiEl;
    })(api_ui.Element);
    api_ui.LiEl = LiEl;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var EmEl = (function (_super) {
        __extends(EmEl, _super);
        function EmEl(idPrefix, className) {
                _super.call(this, "em", idPrefix, className);
        }
        return EmEl;
    })(api_ui.Element);
    api_ui.EmEl = EmEl;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var ImgEl = (function (_super) {
        __extends(ImgEl, _super);
        function ImgEl(src, idPrefix, className) {
                _super.call(this, "img", idPrefix, className, api_ui.ImgHelper.create());
            this.getEl().setSrc(src);
        }
        ImgEl.prototype.getEl = function () {
            return _super.prototype.getEl.call(this);
        };
        return ImgEl;
    })(api_ui.Element);
    api_ui.ImgEl = ImgEl;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var SpanEl = (function (_super) {
        __extends(SpanEl, _super);
        function SpanEl(idPrefix, className) {
                _super.call(this, 'span', idPrefix, className);
        }
        return SpanEl;
    })(api_ui.Element);
    api_ui.SpanEl = SpanEl;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var Panel = (function (_super) {
        __extends(Panel, _super);
        function Panel(idPrefix) {
                _super.call(this, idPrefix, "panel");
        }
        return Panel;
    })(api_ui.DivEl);
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
            this.panels.splice(index, 1);
            if(this.panelShown === index) {
                if(this.panels.length < index + 1) {
                    this.panels[this.panels.length].show();
                    this.panelShown = this.panels.length - 1;
                } else {
                    this.panels[index].show();
                }
            }
            return panel;
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
        return DeckPanel;
    })(api_ui.Panel);
    api_ui.DeckPanel = DeckPanel;    
})(api_ui || (api_ui = {}));
var api_ui;
(function (api_ui) {
    var ButtonEl = (function (_super) {
        __extends(ButtonEl, _super);
        function ButtonEl(idPrefix, className) {
                _super.call(this, "button", idPrefix, className);
        }
        return ButtonEl;
    })(api_ui.Element);
    api_ui.ButtonEl = ButtonEl;    
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
    })(api_ui.DivEl);
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
    })(api_ui.ButtonEl);
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
    })(api_ui.DivEl);
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
    })(api_ui.LiEl);
    api_ui_menu.MenuItem = MenuItem;    
})(api_ui_menu || (api_ui_menu = {}));
var api_ui_detailpanel;
(function (api_ui_detailpanel) {
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
    })(api_ui.DivEl);
    api_ui_detailpanel.DetailPanel = DetailPanel;    
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
            var headerEl = new api_ui.DivEl("header", "header");
            var iconEl = api_util.ImageLoader.get(iconUrl + "?size=80", 80, 80);
            var hgroupEl = new api_ui.Element("hgroup");
            var headerTextEl = new api_ui.H1El();
            headerTextEl.getEl().setInnerHtml(title);
            hgroupEl.appendChild(headerTextEl);
            var subtitleEl = new api_ui.H4El();
            subtitleEl.getEl().setInnerHtml(subtitle);
            hgroupEl.appendChild(subtitleEl);
            headerEl.getEl().appendChild(iconEl);
            headerEl.appendChild(hgroupEl);
            headerEl.appendChild(this.createActionMenu());
            this.appendChild(headerEl);
        };
        DetailTabPanel.prototype.addCanvas = function () {
            var canvasEl = this.canvas = new api_ui.DivEl("canvas", "canvas");
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
    })(api_ui.DivEl);
    api_ui_detailpanel.DetailTabPanel = DetailTabPanel;    
    var DetailPanelTab = (function () {
        function DetailPanelTab(name) {
            this.name = name;
            this.content = new api_ui.DivEl("test-content");
            this.content.getEl().setInnerHtml(this.name);
        }
        return DetailPanelTab;
    })();
    api_ui_detailpanel.DetailPanelTab = DetailPanelTab;    
    var DetailPanelTabList = (function (_super) {
        __extends(DetailPanelTabList, _super);
        function DetailPanelTabList() {
                _super.call(this, "tab-list", "tab-list");
            this.tabs = [];
        }
        DetailPanelTabList.prototype.addTab = function (tab, clickCallback) {
            var _this = this;
            var tabEl = new api_ui.LiEl("tab");
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
    })(api_ui.UlEl);
    api_ui_detailpanel.DetailPanelTabList = DetailPanelTabList;    
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
    })(api_ui.DivEl);
    api_ui_detailpanel.DetailPanelBox = DetailPanelBox;    
})(api_ui_detailpanel || (api_ui_detailpanel = {}));
var api_ui_wizard;
(function (api_ui_wizard) {
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
        WizardPanel.prototype.addTitle = function () {
            this.titleEl = new api_ui.Element("input", "title");
            this.titleEl.getEl().addClass("title");
            this.appendChild(this.titleEl);
        };
        WizardPanel.prototype.addSubTitle = function () {
            this.subTitleEl = new api_ui.Element("input", "title");
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
    api_ui_wizard.WizardPanel = WizardPanel;    
    var WizardStepPanels = (function (_super) {
        __extends(WizardStepPanels, _super);
        function WizardStepPanels() {
                _super.call(this, "WizardStepPanels");
        }
        return WizardStepPanels;
    })(api_ui.DeckPanel);    
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
            var stepEl = new api_ui.LiEl(step.getLabel());
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
    })(api_ui.UlEl);    
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
    api_ui_wizard.WizardStep = WizardStep;    
})(api_ui_wizard || (api_ui_wizard = {}));
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
    })(api_ui.UlEl);
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
    })(api_ui.UlEl);
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
    })(api_ui.ButtonEl);
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
            var ulEl = new api_ui.UlEl();
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
            this.tabMenuButton.setLabel(tab.getLabel());
            this.menuEl.appendChild(tabMenuItem);
        };
        TabMenu.prototype.getSize = function () {
            return this.tabs.length;
        };
        TabMenu.prototype.removeTab = function (tab) {
            var tabMenuItem = tab;
            tabMenuItem.getEl().remove();
            this.tabs.splice(tab.getTabIndex());
        };
        TabMenu.prototype.selectTab = function (tab) {
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
            this.fireBeforeTabRemoved(tabMenuItem);
        };
        TabMenu.prototype.fireTabSelected = function (tab) {
            for(var i = 0; i < this.tabSelectedListeners.length; i++) {
                this.tabSelectedListeners[i].selectedTab(tab);
            }
        };
        TabMenu.prototype.fireBeforeTabRemoved = function (tab) {
            for(var i = 0; i < this.tabRemovedListeners.length; i++) {
                this.tabRemovedListeners[i].tabRemove(tab);
            }
        };
        return TabMenu;
    })(api_ui.DivEl);
    api_ui_tab.TabMenu = TabMenu;    
})(api_ui_tab || (api_ui_tab = {}));
var api_ui_tab;
(function (api_ui_tab) {
    var TabMenuButton = (function (_super) {
        __extends(TabMenuButton, _super);
        function TabMenuButton(idPrefix) {
                _super.call(this, idPrefix || "TabMenuButton");
            this.labelEl = new api_ui.SpanEl();
            this.appendChild(this.labelEl);
        }
        TabMenuButton.prototype.setTabMenu = function (tabMenu) {
            this.tabMenu = tabMenu;
        };
        TabMenuButton.prototype.setLabel = function (value) {
            jQuery(this.labelEl.getHTMLElement()).text(value);
        };
        return TabMenuButton;
    })(api_ui.DivEl);
    api_ui_tab.TabMenuButton = TabMenuButton;    
})(api_ui_tab || (api_ui_tab = {}));
var api_ui_tab;
(function (api_ui_tab) {
    var TabMenuItem = (function (_super) {
        __extends(TabMenuItem, _super);
        function TabMenuItem(label) {
            var _this = this;
                _super.call(this, "TabMenuItem", "tab-menu-item");
            this.label = label;
            this.labelEl = new api_ui.SpanEl();
            this.labelEl.getEl().setInnerHtml(label);
            this.appendChild(this.labelEl);
            var removeButton = new api_ui.ButtonEl();
            removeButton.getEl().setInnerHtml("X");
            this.appendChild(removeButton);
            this.labelEl.getEl().addEventListener("click", function () {
                _this.tabMenu.handleTabClickedEvent(_this);
            });
            removeButton.getEl().addEventListener("click", function () {
                _this.tabMenu.handleTabRemoveButtonClickedEvent(_this);
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
        return TabMenuItem;
    })(api_ui.LiEl);
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
        TabBar.prototype.getSize = function () {
            return 0;
        };
        TabBar.prototype.addTabSelectedListener = function (listener) {
        };
        TabBar.prototype.addTabRemoveListener = function (listener) {
        };
        return TabBar;
    })(api_ui.DivEl);
    api_ui_tab.TabBar = TabBar;    
})(api_ui_tab || (api_ui_tab = {}));
var api_ui_tab;
(function (api_ui_tab) {
    var TabPanelController = (function () {
        function TabPanelController(tabNavigator, deckPanel) {
            this.deckIndexByTabIndex = {
            };
            this.tabNavigator = tabNavigator;
            this.deckPanel = deckPanel;
            this.tabNavigator.addTabSelectedListener(this);
            this.tabNavigator.addTabRemoveListener(this);
        }
        TabPanelController.prototype.addPanel = function (panel, tab) {
            var tabIndex = this.tabNavigator.getSize();
            tab.setTabIndex(tabIndex);
            this.tabNavigator.addTab(tab);
            if(this.deckPanel != null) {
                this.deckIndexByTabIndex[tabIndex] = this.deckPanel.addPanel(panel);
            }
        };
        TabPanelController.prototype.tabRemove = function (tab) {
            var deckIndex = this.deckIndexByTabIndex[tab.getTabIndex()];
            if(this.deckPanel != null) {
                this.deckPanel.removePanel(deckIndex);
            }
        };
        TabPanelController.prototype.removeTab = function (tab) {
        };
        TabPanelController.prototype.selectedTab = function (tab) {
            var deckIndex = this.deckIndexByTabIndex[tab.getTabIndex()];
            if(this.deckPanel != null) {
                this.deckPanel.showPanel(deckIndex);
            }
        };
        return TabPanelController;
    })();
    api_ui_tab.TabPanelController = TabPanelController;    
})(api_ui_tab || (api_ui_tab = {}));
var api_appbar;
(function (api_appbar) {
    var AppBar = (function (_super) {
        __extends(AppBar, _super);
        function AppBar(appName) {
                _super.call(this, 'AppBar', 'appbar');
            this.appName = appName;
            this.addLauncherButton();
            this.addSeparator();
            this.addHomeButton();
            this.addUserButton();
            this.addTabMenu();
            this.addUserInfoPopup();
            this.initExt();
        }
        AppBar.prototype.initExt = function () {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        };
        AppBar.prototype.addLauncherButton = function () {
            this.launcherButton = new api_appbar.LauncherButton();
            this.appendChild(this.launcherButton);
        };
        AppBar.prototype.addSeparator = function () {
            var separator = new api_appbar.Separator();
            this.appendChild(separator);
        };
        AppBar.prototype.addHomeButton = function () {
            this.homeButton = new api_appbar.HomeButton(this.appName);
            this.appendChild(this.homeButton);
        };
        AppBar.prototype.addTabMenu = function () {
            this.tabMenu = new api_appbar.TabMenuContainer();
            this.appendChild(this.tabMenu);
        };
        AppBar.prototype.addUserButton = function () {
            this.userButton = new api_appbar.UserButton();
            this.appendChild(this.userButton);
        };
        AppBar.prototype.addUserInfoPopup = function () {
            var _this = this;
            this.userInfoPopup = new api_appbar.UserInfoPopup();
            this.userButton.getEl().addEventListener('click', function (event) {
                _this.userInfoPopup.toggle();
            });
        };
        return AppBar;
    })(api_ui.DivEl);
    api_appbar.AppBar = AppBar;    
    var LauncherButton = (function (_super) {
        __extends(LauncherButton, _super);
        function LauncherButton() {
                _super.call(this, 'LauncherButton', 'launcher-button');
            this.getEl().addEventListener('click', function (event) {
                api_appbar.AppBarActions.OPEN_APP_LAUNCHER.execute();
            });
        }
        return LauncherButton;
    })(api_ui.ButtonEl);
    api_appbar.LauncherButton = LauncherButton;    
    var Separator = (function (_super) {
        __extends(Separator, _super);
        function Separator() {
                _super.call(this, 'AppBarSeparator', 'appbar-separator');
        }
        return Separator;
    })(api_ui.SpanEl);
    api_appbar.Separator = Separator;    
    var HomeButton = (function (_super) {
        __extends(HomeButton, _super);
        function HomeButton(text) {
                _super.call(this, 'HomeButton', 'home-button');
            this.getEl().setInnerHtml(text);
            this.getEl().addEventListener('click', function (event) {
                api_appbar.AppBarActions.SHOW_APP_BROWSER_PANEL.execute();
            });
        }
        return HomeButton;
    })(api_ui.ButtonEl);
    api_appbar.HomeButton = HomeButton;    
    var TabMenuContainer = (function (_super) {
        __extends(TabMenuContainer, _super);
        function TabMenuContainer() {
                _super.call(this, 'TabMenuContainer', 'tabmenu-container');
        }
        return TabMenuContainer;
    })(api_ui.DivEl);
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
    })(api_ui.ButtonEl);
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
    })(api_ui.DivEl);
    api_appbar.UserInfoPopup = UserInfoPopup;    
})(api_appbar || (api_appbar = {}));
var api_appbar;
(function (api_appbar) {
    var OpenAppLauncherAction = (function (_super) {
        __extends(OpenAppLauncherAction, _super);
        function OpenAppLauncherAction() {
                _super.call(this, 'Start');
            this.addExecutionListener(function () {
                new api_appbar.OpenAppLauncherEvent().fire();
                console.log('api_appbar.OpenAppLauncherEvent fired.');
            });
        }
        return OpenAppLauncherAction;
    })(api_ui.Action);
    api_appbar.OpenAppLauncherAction = OpenAppLauncherAction;    
    var ShowAppBrowsePanelAction = (function (_super) {
        __extends(ShowAppBrowsePanelAction, _super);
        function ShowAppBrowsePanelAction() {
                _super.call(this, 'Home');
            this.addExecutionListener(function () {
                new api_appbar.ShowAppBrowsePanelEvent().fire();
                console.log('api_appbar.ShowAppBrowsePanelEvent fired.');
            });
        }
        return ShowAppBrowsePanelAction;
    })(api_ui.Action);
    api_appbar.ShowAppBrowsePanelAction = ShowAppBrowsePanelAction;    
    var AppBarActions = (function () {
        function AppBarActions() { }
        AppBarActions.OPEN_APP_LAUNCHER = new OpenAppLauncherAction();
        AppBarActions.SHOW_APP_BROWSER_PANEL = new ShowAppBrowsePanelAction();
        return AppBarActions;
    })();
    api_appbar.AppBarActions = AppBarActions;    
})(api_appbar || (api_appbar = {}));
var api_appbar;
(function (api_appbar) {
    var OpenAppLauncherEvent = (function (_super) {
        __extends(OpenAppLauncherEvent, _super);
        function OpenAppLauncherEvent() {
                _super.call(this, 'openAppLauncher');
        }
        return OpenAppLauncherEvent;
    })(api_event.Event);
    api_appbar.OpenAppLauncherEvent = OpenAppLauncherEvent;    
    var ShowAppBrowsePanelEvent = (function (_super) {
        __extends(ShowAppBrowsePanelEvent, _super);
        function ShowAppBrowsePanelEvent() {
                _super.call(this, 'showAppBrowsePanel');
        }
        return ShowAppBrowsePanelEvent;
    })(api_event.Event);
    api_appbar.ShowAppBrowsePanelEvent = ShowAppBrowsePanelEvent;    
})(api_appbar || (api_appbar = {}));
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
    })(api_ui.DivEl);
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
    })(api_ui.H2El);
    api_ui_dialog.ModalDialogTitle = ModalDialogTitle;    
    var ModalDialogContentPanel = (function (_super) {
        __extends(ModalDialogContentPanel, _super);
        function ModalDialogContentPanel() {
                _super.call(this, "ModalDialogContentPanel", "content-panel");
        }
        return ModalDialogContentPanel;
    })(api_ui.DivEl);
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
    })(api_ui.DivEl);
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
    })(api_ui.DivEl);
    api_delete.DeleteDialogItemList = DeleteDialogItemList;    
    var DeleteDialogItemComponent = (function (_super) {
        __extends(DeleteDialogItemComponent, _super);
        function DeleteDialogItemComponent(deleteItem) {
                _super.call(this, "DeleteDialogItem");
            this.getEl().addClass("item");
            var icon = new api_ui.ImgEl(deleteItem.getIconUrl());
            this.appendChild(icon);
            var displayName = new api_ui.H4El();
            displayName.getEl().setInnerHtml(deleteItem.getDisplayName());
            this.appendChild(displayName);
        }
        return DeleteDialogItemComponent;
    })(api_ui.DivEl);    
})(api_delete || (api_delete = {}));
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
        };
        AppBarTabMenu.prototype.selectTab = function (tab) {
            _super.prototype.selectTab.call(this, tab);
        };
        AppBarTabMenu.prototype.createTabMenuButton = function () {
            this.tabMenuButton = new api_appbar.AppBarTabMenuButton();
            return this.tabMenuButton;
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
            this.iconEl = new api_ui.SpanEl();
            this.iconEl.getEl().addClass("icon-icomoon-pencil-32");
            this.prependChild(this.iconEl);
            this.tabCountEl = new AppBarTabCount();
            this.appendChild(this.tabCountEl);
            this.setLabel("****");
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
            this.getEl().setInnerHtml("" + value);
        };
        return AppBarTabCount;
    })(api_ui.SpanEl);
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
    var FormDeckPanel = (function (_super) {
        __extends(FormDeckPanel, _super);
        function FormDeckPanel() {
                _super.call(this, "FormDeckPanel");
        }
        return FormDeckPanel;
    })(api_ui.DeckPanel);
    api.FormDeckPanel = FormDeckPanel;    
})(api || (api = {}));
var api;
(function (api) {
    var AppPanel = (function (_super) {
        __extends(AppPanel, _super);
        function AppPanel(appMainPanel, formDeckPanel) {
                _super.call(this, "AppPanel");
            this.appBrowsePanel = appMainPanel;
            this.formDeckPanel = formDeckPanel;
            this.addPanel(this.appBrowsePanel);
            this.addPanel(this.formDeckPanel);
            this.showPanel(0);
            this.initExt();
        }
        AppPanel.prototype.initExt = function () {
            this.ext = new Ext.layout.container.Card({
                id: 'AppPanel',
                title: 'AppPanel',
                closable: false,
                border: false,
                layout: 'card'
            });
            this.ext.add(this.appBrowsePanel);
            this.ext.add(this.formDeckPanel);
        };
        return AppPanel;
    })(api_ui.DeckPanel);
    api.AppPanel = AppPanel;    
})(api || (api = {}));
var api;
(function (api) {
    var AppBrowsePanel = (function (_super) {
        __extends(AppBrowsePanel, _super);
        function AppBrowsePanel(browseToolbar, grid, detailPanel, filterPanel) {
            this.browseToolbar = browseToolbar;
            this.grid = grid;
            this.detailPanel = detailPanel;
            this.filterPanel = filterPanel;
                _super.call(this, "AppBrowsePanel");
            this.initExt();
        }
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
    api.AppBrowsePanel = AppBrowsePanel;    
})(api || (api = {}));
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
