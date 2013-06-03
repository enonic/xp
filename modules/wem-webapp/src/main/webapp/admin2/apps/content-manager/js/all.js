Ext.define('Admin.lib.JsonRpcProvider', {
    alias: 'direct.jsonrpcprovider',
    extend: 'Ext.direct.RemotingProvider',
    initAPI: function () {
        var methods = this.methods;
        var namespace = this.namespace;
        var methodName;
        for (var i = 0; i < methods.length; i++) {
            methodName = methods[i];
            var def = {
                name: methodName,
                len: 1
            };
            var method = new Ext.direct.RemotingMethod(def);
            namespace[methodName] = this.createHandler(null, method);
        }
    },
    getCallData: function (transaction) {
        return {
            jsonrpc: '2.0',
            id: transaction.tid,
            method: transaction.method,
            params: transaction.data[0]
        };
    },
    createEvent: function (response) {
        var error = response.error ? true : false;
        response.tid = response.id;
        response.type = error ? 'exception' : 'rpc';
        if (error) {
            response.message = response.error.message;
        }
        return Ext.create('direct.' + response.type, response);
    }
});
Ext.define('Admin.lib.RemoteService', {
    requires: [
        'Admin.lib.JsonRpcProvider'
    ],
    singleton: true,
    handlerCache: {
    },
    init: function () {
        var config = {
            "url": API.util.getAbsoluteUri("admin/rest/jsonrpc"),
            "type": "jsonrpc",
            "namespace": "Admin.lib.RemoteService",
            "methods": [
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
            ],
            "enableBuffer": 20
        };
        this.provider = Ext.Direct.addProvider(config);
    },
    account_find: function (params, callback) {
        console.log(params, callback);
    },
    account_getGraph: function (params, callback) {
        console.log(params, callback);
    },
    account_changePassword: function (params, callback) {
        console.log(params, callback);
    },
    account_verifyUniqueEmail: function (params, callback) {
        console.log(params, callback);
    },
    account_suggestUserName: function (params, callback) {
        console.log(params, callback);
    },
    account_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },
    account_delete: function (params, callback) {
        console.log(params, callback);
    },
    account_get: function (params, callback) {
        console.log(params, callback);
    },
    util_getCountries: function (params, callback) {
        console.log(params, callback);
    },
    util_getLocales: function (params, callback) {
        console.log(params, callback);
    },
    util_getTimeZones: function (params, callback) {
        console.log(params, callback);
    },
    userstore_getAll: function (params, callback) {
        console.log(params, callback);
    },
    userstore_get: function (params, callback) {
        console.log(params, callback);
    },
    userstore_getConnectors: function (params, callback) {
        console.log(params, callback);
    },
    userstore_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },
    userstore_delete: function (params, callback) {
        console.log(params, callback);
    },
    content_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },
    contentType_get: function (params, callback) {
        console.log(params, callback);
    },
    content_list: function (params, callback) {
        console.log(params, callback);
    },
    content_tree: function (params, callback) {
        console.log(params, callback);
    },
    content_get: function (params, callback) {
        console.log(params, callback);
    },
    contentType_list: function (params, callback) {
        console.log(params, callback);
    },
    content_delete: function (params, callback) {
        console.log(params, callback);
    },
    content_find: function (params, callback) {
        console.log(params, callback);
    },
    content_validate: function (params, callback) {
        console.log(params, callback);
    },
    contentType_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },
    contentType_delete: function (params, callback) {
        console.log(params, callback);
    },
    contentType_tree: function (params, callback) {
        console.log(params, callback);
    },
    schema_tree: function (params, callback) {
        console.log(params, callback);
    },
    schema_list: function (params, callback) {
        console.log(params, callback);
    },
    system_getSystemInfo: function (params, callback) {
        console.log(params, callback);
    },
    mixin_get: function (params, callback) {
        console.log(params, callback);
    },
    mixin_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },
    mixin_delete: function (params, callback) {
        console.log(params, callback);
    },
    relationshipType_get: function (params, callback) {
        console.log(params, callback);
    },
    relationshipType_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },
    relationshipType_delete: function (params, callback) {
        console.log(params, callback);
    },
    space_list: function (params, callback) {
        console.log(params, callback);
    },
    space_get: function (params, callback) {
        console.log(params, callback);
    },
    space_delete: function (params, callback) {
        console.log(params, callback);
    },
    space_createOrUpdate: function (params, callback) {
        console.log(params, callback);
    },
    binary_create: function (params, callback) {
        console.log(params, callback);
    },
    getMethod: function (name) {
        var handler = this.handlerCache[name];
        if (handler) {
            return handler;
        }
        var method = new Ext.direct.RemotingMethod({
            name: name,
            len: 1
        });
        handler = this.provider.createHandler(null, method);
        this.handlerCache[name] = handler;
        return handler;
    },
    call: function (name, params, callback) {
        var method = this.getMethod(name);
        return method(params, callback);
    }
}, function () {
    this.init();
});
Ext.define('Ext.ux.toggleslide.Thumb', {
    topZIndex: 10000,
    constructor: function (config) {
        var me = this;
        Ext.apply(me, config || {
        }, {
            cls: Ext.baseCSSPrefix + 'toggle-slide-thumb',
            constrain: false
        });
        me.callParent([
            config
        ]);
    },
    render: function () {
        var me = this;
        me.el = me.slider.el.insertFirst(me.getElConfig());
        me.onRender();
    },
    onRender: function () {
        if (this.disabled) {
            this.disable();
        }
    },
    getElConfig: function () {
        var me = this, slider = me.slider, style = {
        };
        style['left'] = 0;
        return {
            style: style,
            id: this.id,
            cls: this.cls
        };
    },
    bringToFront: function () {
        this.el.setStyle('zIndex', this.topZIndex);
    },
    sendToBack: function () {
        this.el.setStyle('zIndex', '');
        this.el.setStyle({
            visibility: 'hidden'
        });
    },
    disable: function () {
    }
});
Ext.define('Ext.ux.toggleslide.ToggleSlide', {
    extend: 'Ext.Component',
    alias: 'widget.toggleslide',
    duration: 120,
    onText: 'ON',
    offText: 'OFF',
    resizeHandle: true,
    resizeContainer: true,
    onLabelCls: 'x-toggle-slide-label-on',
    offLabelCls: 'x-toggle-slide-label-off',
    handleCls: 'x-toggle-slide-thumb',
    disabledCls: 'x-toggle-slide-disabled',
    state: false,
    booleanMode: true,
    dragging: false,
    diff: 0,
    diff2: 0,
    diff3: 0,
    frame: false,
    renderTpl: [
        '<div class="holder">',
        '<label class="{onLabelCls}">',
        '<span>{onText}</span>',
        '</label>',
        '<label class="{offLabelCls}">',
        '<span>{offText}</span>',
        '</label>',
        '</div>'
    ],
    autoEl: {
        tag: 'div',
        cls: 'x-toggle-slide-container'
    },
    initComponent: function () {
        var me = this;
        me.callParent(arguments);
        me.addEvents('beforechange', 'change');
    },
    beforeRender: function () {
        var me = this;
        me.callParent();
        Ext.applyIf(me.renderData, {
            offLabelCls: me.offLabelCls,
            offText: me.offText,
            onLabelCls: me.onLabelCls,
            onText: me.onText,
            handleCls: me.handleCls
        });
    },
    onRender: function () {
        var me = this;
        if (!me.resizeContainer) {
            me.diff = 0;
        }
        if (!me.resizeHandle) {
            me.diff2 = 3;
            me.diff3 = 5;
        }
        me.callParent(arguments);
        if (me.cls) {
            me.el.addCls(me.cls);
        }
        me.thumb = new Ext.ux.toggleslide.Thumb({
            ownerCt: me,
            slider: me,
            disabled: !!me.disabled
        });
        var holder = me.el.first();
        me.onLabel = holder.first();
        me.onSpan = me.onLabel.first();
        me.offLabel = me.onLabel.next();
        me.offSpan = me.offLabel.first();
        if (me.rendered) {
            me.thumb.render();
        }
        me.handle = me.thumb.el;
        if (me.resizeHandle) {
            me.thumb.bringToFront();
        } else {
            me.thumb.sendToBack();
        }
        me.resize();
        me.disableTextSelection();
        if (!me.disabled) {
            me.registerToggleListeners();
        } else {
            Ext.ux.toggleslide.ToggleSlide.superclass.disable.call(me);
        }
    },
    resize: function () {
        var me = this, container = me.el, offlabel = me.offLabel, onlabel = me.onLabel, handle = me.handle;
        if (me.resizeHandle) {
            var min = (onlabel.getWidth() < offlabel.getWidth()) ? onlabel.getWidth() : offlabel.getWidth();
            handle.setWidth(min);
        }
        if (me.resizeContainer) {
            var max = (onlabel.getWidth() > offlabel.getWidth()) ? onlabel.getWidth() : offlabel.getWidth();
            var expandPx = Math.ceil(container.getHeight() / 3);
            container.setWidth(max + handle.getWidth() + expandPx);
        }
        var b = handle.getWidth() / 2;
        onlabel.setWidth(container.getWidth() - b + me.diff2);
        offlabel.setWidth(container.getWidth() - b + me.diff2);
        var rightside = me.rightside = container.getWidth() - handle.getWidth() - me.diff;
        if (me.state) {
            handle.setLeft(rightside);
        } else {
            handle.setLeft(0);
        }
        me.onDrag();
    },
    disableTextSelection: function () {
        var els = [
            this.el,
            this.onLabel,
            this.offLabel,
            this.handle
        ];
        Ext.each(els, function (el) {
            el.on('mousedown', function (evt) {
                evt.preventDefault();
                return false;
            });
            if (Ext.isIE) {
                el.on('startselect', function (evt) {
                    evt.stopEvent();
                    return false;
                });
            }
        });
    },
    moveHandle: function (on, callback) {
        var me = this, runner = new Ext.util.TaskRunner(), to = on ? me.rightside : 0;
        Ext.create('Ext.fx.Anim', {
            target: me.handle,
            dynamic: true,
            easing: 'easeOut',
            duration: me.duration,
            to: {
                left: to
            },
            listeners: {
                beforeanimate: {
                    fn: function (ani) {
                        me.task = runner.newTask({
                            run: function () {
                                me.onDrag();
                            },
                            interval: 10
                        });
                        me.task.start();
                    },
                    scope: this
                },
                afteranimate: {
                    fn: function (ani) {
                        me.onDrag();
                        me.task.destroy();
                    },
                    scope: this
                }
            },
            callback: callback
        });
    },
    onDragStart: function (e) {
        var me = this;
        me.dragging = true;
        me.dd.constrainTo(me.el, {
            right: me.diff
        });
    },
    onDragEnd: function (e) {
        var me = this, hc = (me.handle.getLeft(true) + me.handle.getRight(true)) / 2, cc = (me.el.getLeft(true) + me.el.getRight(true)) /
                                                                                           2, next = hc > cc;
        (me.state != next) ? me.toggle() : me.moveHandle(next);
        me.dragging = false;
    },
    onDrag: function (e) {
        var me = this, p = me.handle.getLeft(true) - me.rightside;
        p = (me.handle.getLeft(true) == me.rightside) ? 0 : p - me.diff3;
        me.onLabel.setStyle({
            marginLeft: p + 'px'
        });
    },
    onMouseUp: function () {
        if (!this.dragging) {
            this.toggle();
        }
    },
    toggle: function () {
        var me = this, next = !this.state;
        if (!me.booleanMode) {
            next = me.state ? me.onText : me.offText;
        }
        if (me.fireEvent('beforechange', me, next) !== false) {
            me.state = !me.state;
            me.moveHandle(me.state, Ext.bind(me.fireEvent, me, [
                'change',
                me,
                me.getValue()
            ]));
        } else {
            me.moveHandle(me.state);
        }
    },
    enable: function () {
        if (this.disabled) {
            Ext.ux.toggleslide.ToggleSlide.superclass.enable.call(this);
            this.registerToggleListeners();
        }
        return this;
    },
    disable: function () {
        if (!this.disabled) {
            Ext.ux.toggleslide.ToggleSlide.superclass.disable.call(this);
            this.unregisterToggleListeners();
        }
        return this;
    },
    registerToggleListeners: function () {
        var me = this;
        me.dd = new Ext.dd.DD(me.handle);
        me.dd.startDrag = Ext.bind(me.onDragStart, me);
        me.dd.onDrag = Ext.bind(me.onDrag, me);
        me.dd.endDrag = Ext.bind(me.onDragEnd, me);
        me.el.on('mouseup', me.onMouseUp, me);
    },
    unregisterToggleListeners: function () {
        Ext.destroy(this.dd);
        this.el.un('mouseup', this.onMouseUp, this);
    },
    getValue: function () {
        var me = this;
        return me.booleanMode ? me.state : (me.state ? me.onText : me.offText);
    }
});
var admin;
(function (admin) {
    (function (app) {
        (function (handler) {
            var DeleteContentHandler = (function () {
                function DeleteContentHandler() {
                }

                DeleteContentHandler.prototype.doDelete = function (contentModels, callback) {
                    var _this = this;
                    var contentPaths = Ext.Array.map([].concat(contentModels), function (item) {
                        return item.get('path');
                    });
                    Admin.lib.RemoteService.content_delete({
                        'contentPaths': contentPaths
                    }, function (response) {
                        if (response) {
                            callback.call(_this, response.success, response.failures);
                        } else {
                            Ext.Msg.alert('Error', response ? response.error : 'Internal error occured.');
                        }
                    });
                };
                return DeleteContentHandler;
            })();
            handler.DeleteContentHandler = DeleteContentHandler;
        })(app.handler || (app.handler = {}));
        var handler = app.handler;
    })(admin.app || (admin.app = {}));
    var app = admin.app;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var DeleteContentWindow = (function () {
            function DeleteContentWindow() {
                var _this = this;
                this.title = "Delete content(s)";
                this.deleteHandler = new admin.app.handler.DeleteContentHandler();
                this.template = '<div class="delete-container">' + '<tpl for=".">' + '<div class="delete-item">' +
                                '<img class="icon" src="{data.iconUrl}"/>' + '<h4>{data.displayName}</h4>' + '<p>{data.type}</p>' +
                                '</div>' + '</tpl>' + '</div>';
                var deleteCallback = function (obj, success, result) {
                    _this.container.hide();
                };
                var ct = this.container = new Ext.container.Container();
                ct.border = false;
                ct.floating = true;
                ct.shadow = false;
                ct.width = 500;
                ct.modal = true;
                ct.autoHeight = true;
                ct.maxHeight = 600;
                ct.cls = 'admin-window';
                ct.padding = 20;
                var header = new Ext.Component();
                header.region = 'north';
                header.tpl = '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>';
                header.data = {
                    title: this.title
                };
                header.margin = '0 0 20 0';
                ct.add(header);
                var content = this.content = new Ext.Component();
                content.region = 'center';
                content.cls = 'dialog-info';
                content.border = false;
                content.height = 150;
                content.styleHtmlContent = true;
                content.tpl = this.template;
                ct.add(content);
                var buttonRow = new Ext.container.Container();
                buttonRow.layout = {
                    type: 'hbox',
                    pack: 'end'
                };
                var deleteButton = new Ext.button.Button();
                deleteButton.text = 'Delete';
                deleteButton.margin = '0 0 0 10';
                deleteButton.handler = function (btn, evt) {
                    _this.deleteHandler.doDelete(_this.data, deleteCallback);
                };
                buttonRow.add(deleteButton);
                var cancelButton = new Ext.button.Button();
                cancelButton.text = 'Cancel';
                cancelButton.margin = '0 0 0 10';
                cancelButton.handler = function (btn, evt) {
                    ct.hide();
                };
                buttonRow.add(cancelButton);
                ct.add(buttonRow);
            }

            DeleteContentWindow.prototype.setModel = function (model) {
                this.data = model;
                if (model) {
                    if (this.content) {
                        this.content.update(model);
                    }
                }
            };
            DeleteContentWindow.prototype.doShow = function () {
                this.container.show();
            };
            return DeleteContentWindow;
        })();
        ui.DeleteContentWindow = DeleteContentWindow;
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var FormComponent = (function () {
            function FormComponent() {
                var panel = new Ext.form.Panel();
                this.ext = panel;
            }

            FormComponent.prototype.getContentData = function () {
                var contentData = new API_content_data.ContentData();
                return contentData;
            };
            return FormComponent;
        })();
        ui.FormComponent = FormComponent;
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var FormItemSetComponent = (function () {
            function FormItemSetComponent() {
                var panel = new Ext.form.Panel();
                this.ext = panel;
            }

            return FormItemSetComponent;
        })();
        ui.FormItemSetComponent = FormItemSetComponent;
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var BaseInputComponent = (function () {
            function BaseInputComponent(input) {
                this.input = input;
            }

            BaseInputComponent.prototype.getInput = function () {
                return this.input;
            };
            BaseInputComponent.prototype.setValue = function (value, arrayIndex) {
                this.values[arrayIndex] = value;
            };
            return BaseInputComponent;
        })();
        ui.BaseInputComponent = BaseInputComponent;
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var __extends = this.__extends || function (d, b) {
    function __() {
        this.constructor = d;
    }

    __.prototype = b.prototype;
    d.prototype = new __();
};
var admin;
(function (admin) {
    (function (ui) {
        var TextLine = (function (_super) {
            __extends(TextLine, _super);
            function TextLine(input) {
                _super.call(this, input);
                var fieldContainer = new Ext.form.FieldContainer();
                fieldContainer.setFieldLabel('');
                fieldContainer.labelWidth = 110;
                fieldContainer.labelPad = 0;
                var textField = new Ext.form.Text();
                textField.enableKeyEvents = true;
                textField.displayNameSource = true;
                fieldContainer.add(textField);
                this.ext = fieldContainer;
            }

            TextLine.prototype.setValue = function (value, arrayIndex) {
                this.ext.down('textfield').setValue(value);
                _super.prototype.setValue.call(this, value, arrayIndex);
            };
            return TextLine;
        })(admin.ui.BaseInputComponent);
        ui.TextLine = TextLine;
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
Ext.define('Admin.plugin.PersistentGridSelectionPlugin', {
    extend: 'Ext.util.Observable',
    pluginId: 'persistentGridSelection',
    alias: 'plugin.persistentGridSelection',
    keyField: 'id',
    init: function (panel) {
        this.panel = panel;
        this.selections = [];
        this.selected = {
        };
        this.ignoreSelectionChanges = '';
        panel.on('render', function () {
            this.panel.view.un('refresh', this.panel.selModel.refresh, this.panel.selModel);
            this.panel.view.on('refresh', this.onViewRefresh, this);
            this.panel.view.on('beforeitemmousedown', function (view, record, item, index, event, eOpts) {
                var targetElement = new Ext.Element(event.target);
                var isCheckboxColumnIsClicked = targetElement.findParent('td.x-grid-cell-first') !== null;
                if (isCheckboxColumnIsClicked) {
                    var isShiftKeyPressed = event.shiftKey === true;
                    var isCtrlKeyPressed = event.ctrlKey === true;
                    if (isShiftKeyPressed || isCtrlKeyPressed) {
                        return;
                    }
                    var isChecked = this.selected[record.get(this.keyField)];
                    if (!isChecked) {
                        this.panel.selModel.select(index, true, false);
                    } else {
                        this.panel.selModel.deselect(index);
                    }
                    return false;
                }
                this.clearSelectionOnRowClick(view, record, item, index, event, eOpts);
                this.cancelItemContextClickWhenSelectionIsMultiple(view, record, item, index, event, eOpts);
            }, this);
            this.panel.view.headerCt.on('headerclick', this.onHeaderClick, this);
            this.panel.selModel.on('select', this.onRowSelect, this);
            this.panel.selModel.on('deselect', this.onRowDeselect, this);
            this.panel.getStore().on('beforeload', function () {
                this.ignoreSelectionChanges = true;
            }, this);
            this.panel.view.on('itemadd', this.onViewRefresh, this);
            var pagingToolbar = this.panel.down('pagingtoolbar');
            if (pagingToolbar !== null) {
                pagingToolbar.on('beforechange', this.pagingOnBeforeChange, this);
            }
        }, this);
    },
    getSelection: function () {
        return [].concat(this.selections);
    },
    getSelectionCount: function () {
        return this.getSelection().length;
    },
    deselect: function (record) {
        this.onRowDeselect(this.panel.selModel, record);
        var storeRecord;
        var key = record.get(this.keyField);
        if (this.panel instanceof Ext.tree.Panel) {
            storeRecord = this.panel.getRootNode().findChild(this.keyField, key);
        } else if (this.panel instanceof Ext.grid.Panel) {
            storeRecord = this.panel.getStore().findRecord(this.keyField, key);
        }
        this.panel.selModel.deselect(storeRecord);
        this.notifySelectionModelAboutSelectionChange();
    },
    selectAll: function () {
        this.panel.selModel.selectAll();
    },
    clearSelection: function () {
        this.selections = [];
        this.selected = {
        };
        this.panel.selModel.deselectAll();
        this.onViewRefresh();
        this.notifySelectionModelAboutSelectionChange();
    },
    onViewRefresh: function () {
        this.ignoreSelectionChanges = true;
        this.panel.selModel.refresh();
        var i;
        var sm = this.panel.getSelectionModel();
        if (this.panel instanceof Ext.tree.Panel) {
            var rootNode = this.panel.getRootNode(), node;
            for (var selectedItem in this.selected) {
                if (this.selected.hasOwnProperty(selectedItem) && this.selected[selectedItem]) {
                    node = rootNode.findChild(this.keyField, selectedItem, true);
                    if (node) {
                        sm.select(node, true);
                    }
                }
            }
        } else if (this.panel instanceof Ext.grid.Panel) {
            var store = this.panel.getStore(), record;
            for (var selectedItem in this.selected) {
                if (this.selected.hasOwnProperty(selectedItem) && this.selected[selectedItem]) {
                    record = store.findRecord(this.keyField, selectedItem);
                    if (record) {
                        sm.select(record, true);
                    }
                }
            }
        }
        this.ignoreSelectionChanges = false;
    },
    pagingOnBeforeChange: function () {
        this.ignoreSelectionChanges = true;
    },
    onSelectionClear: function () {
        if (!this.ignoreSelectionChanges) {
            this.selections = [];
            this.selected = {
            };
        }
    },
    onRowSelect: function (sm, rec, i, o) {
        if (!this.ignoreSelectionChanges) {
            if (!this.selected[rec.get(this.keyField)]) {
                this.selections.push(rec);
                this.selected[rec.get(this.keyField)] = true;
            }
        }
    },
    onHeaderClick: function (headerCt, header, e) {
        if (header.isCheckerHd) {
            e.stopEvent();
            var isChecked = header.el.hasCls('x-grid-hd-checker-on');
            if (isChecked) {
                this.clearSelection();
            } else {
                this.panel.selModel.selectAll();
            }
        }
        return false;
    },
    onRowDeselect: function (rowModel, record, index, eOpts) {
        if (!this.ignoreSelectionChanges) {
            if (this.selected[record.get(this.keyField)]) {
                for (var j = this.selections.length - 1; j >= 0; j--) {
                    if (this.selections[j].get(this.keyField) == record.get(this.keyField)) {
                        this.selections.splice(j, 1);
                        this.selected[record.get(this.keyField)] = false;
                        break;
                    }
                }
            }
        }
    },
    notifySelectionModelAboutSelectionChange: function () {
        this.panel.selModel.fireEvent("selectionchange", this.panel.selModel, this.selections);
    },
    cancelItemContextClickWhenSelectionIsMultiple: function (view, record, item, index, event, eOpts) {
        var isRightClick = event.button === 2;
        var recordIsSelected = this.selected[record.get(this.keyField)];
        var cancel = isRightClick && recordIsSelected && this.getSelectionCount() > 1;
        if (cancel) {
            return false;
        }
        return true;
    },
    clearSelectionOnRowClick: function (view, record, item, index, event, eOpts) {
        var targetElement = event.target;
        var isLeftClick = event.button === 0;
        var isCheckbox = targetElement.className && targetElement.className.indexOf('x-grid-row-checker') > -1;
        if (isLeftClick && !isCheckbox) {
            this.clearSelection();
        }
    }
});
Ext.define('Admin.plugin.GridToolbarPlugin', {
    extend: 'Object',
    alias: 'plugin.gridToolbarPlugin',
    pluginId: 'gridToolbarPlugin',
    constructor: function (config) {
        if (config) {
            Ext.apply(this, config);
        }
    },
    init: function (toolbar) {
        var me = this;
        me.toolbar = toolbar;
        me.resultTextItem = Ext.create('Ext.toolbar.TextItem', {
            text: '',
            hidden: !!me.toolbar.resultCountHidden
        });
        me.selectAllButton = me.createSelectAllButton();
        me.clearSelectionButton = me.createClearSelectionButton();
        me.tbFill = Ext.create('Ext.toolbar.Fill');
        me.orderByButton = me.createOrderByButton();
        me.orderByDirectionButton = me.createOrderByDirectionButton();
        if (Ext.isFunction(me.toolbar.store.getCount)) {
            me.updateResultCount(me.getCount(me.toolbar.store));
        } else if (Ext.isString(me.toolbar.store)) {
            me.toolbar.store = Ext.StoreManager.lookup(me.toolbar.store);
        }
        me.toolbar.insert(0, me.resultTextItem);
        me.toolbar.insert(1, me.selectAllButton);
        me.toolbar.insert(2, Ext.create('Ext.toolbar.TextItem', {
            text: ' | '
        }));
        me.toolbar.insert(3, me.clearSelectionButton);
        if (!(me.toolbar.store instanceof Ext.data.TreeStore)) {
            me.toolbar.insert(4, me.tbFill);
            me.toolbar.insert(5, me.orderByButton);
            me.toolbar.insert(6, me.orderByDirectionButton);
        }
        me.orderByButton.addListener('change', function () {
            me.doSort();
        });
        me.orderByDirectionButton.addListener('change', function () {
            me.doSort();
        });
        if (me.toolbar.store) {
            var loadEventName = me.toolbar.store.buffered ? 'prefetch' : 'load';
            me.toolbar.store.on(loadEventName, function (store) {
                me.updateResultCount(me.getCount(store));
            });
        }
        if (me.toolbar.gridPanel) {
            me.toolbar.gridPanel.getSelectionModel().on('selectionchange', function (model, selected, eOpts) {
                me.updateSelectAll(selected);
                me.updateClearSelection(selected);
            });
        }
    },
    createSelectAllButton: function () {
        var me = this;
        return Ext.create('Ext.Component', {
            autoEl: {
                tag: 'a',
                href: 'javascript:;',
                html: 'Select All',
                cls: 'admin-grid-toolbar-btn-none-selected'
            },
            listeners: {
                render: function (cmp) {
                    cmp.el.on('click', function () {
                        if (cmp.el.hasCls('admin-grid-toolbar-btn-none-selected')) {
                            me.toolbar.gridPanel.getSelectionModel().selectAll();
                        } else {
                            me.toolbar.gridPanel.getSelectionModel().deselectAll();
                        }
                    });
                }
            }
        });
    },
    createClearSelectionButton: function () {
        var me = this;
        return Ext.create('Ext.Component', {
            autoEl: {
                tag: 'a',
                href: 'javascript:;',
                html: ' Clear Selection',
                cls: 'admin-grid-toolbar-btn-clear-selection'
            },
            listeners: {
                render: function (cmp) {
                    cmp.el.on('click', function () {
                        if (cmp.el.hasCls('admin-grid-toolbar-btn-clear-selection')) {
                            me.toolbar.gridPanel.getSelectionModel().deselectAll();
                        }
                    });
                }
            }
        });
    },
    createOrderByButton: function () {
        var me = this;
        var menuItems = me.createOrderByMenuItems();
        return Ext.create('Ext.button.Cycle', {
            showText: true,
            prependText: 'Order by ',
            menu: {
                items: menuItems
            }
        });
    },
    createOrderByDirectionButton: function () {
        return Ext.create('Ext.button.Cycle', {
            showText: true,
            prependText: 'Direction ',
            menu: {
                items: [
                    {
                        text: 'ASC'
                    },
                    {
                        text: 'DESC'
                    }
                ]
            }
        });
    },
    createOrderByMenuItems: function () {
        var me = this;
        var gridColumns = me.toolbar.gridPanel.columns;
        var menuItems = [];
        for (var i = 0; i < gridColumns.length; i++) {
            menuItems.push({
                text: gridColumns[i].text,
                dataIndex: gridColumns[i].dataIndex
            });
        }
        return menuItems;
    },
    doSort: function () {
        var me = this;
        var sortBy = me.orderByButton.getActiveItem().dataIndex;
        var direction = me.orderByDirectionButton.getActiveItem().text;
        me.toolbar.gridPanel.getStore().sort(sortBy, direction);
    },
    updateResultCount: function (count) {
        this.resultTextItem.setText(count + ' result(s) - ');
    },
    setResultCountVisible: function (visible) {
        this.resultTextItem.setVisible(visible);
    },
    updateSelectAll: function (selected) {
        var btn = this.selectAllButton;
        var isSelectMode = btn.el.hasCls('admin-grid-toolbar-btn-none-selected');
        var areAllRecordsSelected = !Ext.isEmpty(selected) && this.getCount(this.toolbar.store) == selected.length;
        if (areAllRecordsSelected && isSelectMode) {
            btn.update('Deselect all');
            btn.el.removeCls('admin-grid-toolbar-btn-none-selected');
        } else if (!areAllRecordsSelected && !isSelectMode) {
            btn.update('Select all');
            btn.el.addCls('admin-grid-toolbar-btn-none-selected');
        }
    },
    updateClearSelection: function (selected) {
        var btn = this.clearSelectionButton;
        var count = selected.length;
        if (count > 0) {
            btn.update('Clear selection (' + selected.length + ')');
        } else {
            btn.update('Clear selection');
        }
    },
    getCount: function (store) {
        if (store instanceof Ext.data.Store) {
            return store.getTotalCount();
        } else if (store instanceof Ext.data.TreeStore) {
            return this.countTreeNodes(store.getRootNode()) - 1;
        } else {
            return undefined;
        }
    },
    countTreeNodes: function (node) {
        if (this.toolbar.countTopLevelOnly) {
            return Ext.isEmpty(node.childNodes) ? 1 : 1 + node.childNodes.length;
        } else {
            var count = 1;
            if (!Ext.isEmpty(node.childNodes)) {
                node.eachChild(function (child) {
                    count += this.countTreeNodes(child);
                }, this);
            }
            return count;
        }
    }
});
Ext.define('Admin.model.contentManager.ContentModel', {
    extend: 'Ext.data.Model',
    requires: [
        'Ext.data.UuidGenerator'
    ],
    fields: [
        'id',
        'path',
        'name',
        'type',
        'displayName',
        'owner',
        'modifier',
        'iconUrl',
        {
            name: 'modifiedTime',
            type: 'date',
            defaultValue: new Date()
        },
        {
            name: 'createdTime',
            type: 'date',
            defaultValue: new Date()
        },
        {
            name: 'editable',
            type: 'boolean'
        },
        {
            name: 'deletable',
            type: 'boolean'
        },
        {
            name: 'allowsChildren',
            type: 'boolean'
        },
        {
            name: 'hasChildren',
            type: 'boolean'
        },
        {
            name: 'leaf',
            type: 'boolean',
            convert: function (value, record) {
                return !record.get('hasChildren');
            }
        }
    ],
    idProperty: 'uuid'
});
Ext.define('Admin.model.schemaManager.ContentTypeModel', {
    extend: 'Ext.data.Model',
    fields: [
        'qualifiedName',
        'name',
        'displayName',
        'module',
        {
            name: 'createdTime',
            type: 'date',
            defaultValue: new Date()
        },
        {
            name: 'modifiedTime',
            type: 'date',
            defaultValue: new Date()
        },
        'configXML',
        'iconUrl'
    ],
    idProperty: 'qualifiedName'
});
Ext.define('Admin.store.contentManager.ContentStore', {
    extend: 'Ext.data.Store',
    model: 'Admin.model.contentManager.ContentModel',
    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.content_find,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'contents',
            totalProperty: 'total'
        }
    }
});
Ext.define('Admin.store.contentManager.ContentTreeStore', {
    extend: 'Ext.data.TreeStore',
    model: 'Admin.model.contentManager.ContentModel',
    folderSort: true,
    autoLoad: false,
    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.content_tree,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'contents',
            totalProperty: 'total'
        }
    }
});
Ext.define('Admin.store.schemaManager.ContentTypeStore', {
    extend: 'Ext.data.Store',
    model: 'Admin.model.schemaManager.ContentTypeModel',
    pageSize: 50,
    remoteSort: true,
    sorters: [
        {
            property: 'modifiedTime',
            direction: 'DESC'
        }
    ],
    autoLoad: true,
    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.contentType_list,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty: 'total'
        }
    }
});
Ext.define('Admin.store.schemaManager.ContentTypeTreeStore', {
    extend: 'Ext.data.TreeStore',
    model: 'Admin.model.schemaManager.ContentTypeModel',
    folderSort: true,
    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.contentType_tree,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty: 'total'
        }
    }
});
Ext.define('Admin.lib.UriHelper', {
    singleton: true,
    getContentManagerSearchUri: function () {
        return this.getAbsoluteUri('admin/resources/data/contentManagerStub.json');
    },
    getAbsoluteUri: function (uri) {
    }
});
Ext.define('Admin.lib.DateHelper', {
    singleton: true,
    addHours: function (date, offset) {
        date.setHours(date.getHours() + offset);
    }
});
Ext.define('Admin.view.AdminImageButton', {
    extend: 'Ext.button.Button',
    alias: 'widget.adminImageButton',
    cls: 'admin-image-button',
    scale: 'large',
    popupTpl: undefined,
    popupData: undefined,
    listeners: {
        click: function (item) {
            if (!item.popupPanel) {
                item.popupPanel = Ext.create("Ext.panel.Panel", {
                    floating: true,
                    cls: 'admin-toolbar-popup',
                    border: false,
                    tpl: item.popupTpl,
                    data: item.popupData,
                    styleHtmlContent: true,
                    renderTo: Ext.getBody(),
                    listeners: {
                        afterrender: function (cont) {
                            cont.show();
                            cont.setPagePosition(cont.el.getAlignToXY(item.el, "tr-br?"));
                        }
                    }
                });
            } else {
                if (item.popupPanel.isHidden()) {
                    item.popupPanel.show();
                } else {
                    item.popupPanel.hide();
                }
            }
        }
    }
});
Ext.define('Admin.view.TopBarMenuItem', {
    extend: 'Ext.container.Container',
    alias: 'widget.topBarMenuItem',
    cls: 'admin-topbar-menu-item',
    activeCls: 'active',
    isMenuItem: true,
    canActivate: true,
    layout: {
        type: 'hbox',
        align: 'middle'
    },
    bubbleEvents: [
        'closeMenuItem'
    ],
    initComponent: function () {
        var me = this;
        this.items = [];
        if (this.iconCls || this.iconSrc) {
            this.items.push({
                xtype: 'image',
                width: 32,
                height: 32,
                margin: '0 12px 0 0',
                cls: this.iconCls,
                src: this.iconSrc
            });
        }
        if (this.text1 || this.text2) {
            this.items.push({
                xtype: 'component',
                flex: 1,
                itemId: 'titleContainer',
                styleHtmlContent: true,
                tpl: '<strong>{text1}</strong><tpl if="text2"><br/><em>{text2}</em></tpl>',
                data: {
                    text1: this.text1,
                    text2: this.text2
                }
            });
        }
        if (this.closable !== false) {
            this.items.push({
                xtype: 'component',
                autoEl: 'a',
                cls: 'close-button icon-remove icon-large',
                margins: '0 0 0 12px',
                listeners: {
                    afterrender: function (cmp) {
                        cmp.el.on('click', function () {
                            me.deactivate();
                            me.fireEvent('closeMenuItem', me);
                        });
                    }
                }
            });
        }
        this.callParent(arguments);
        this.addEvents('activate', 'deactivate', 'click', 'closeMenuItem');
    },
    activate: function () {
        var me = this;
        if (!me.activated && me.canActivate && me.rendered && !me.isDisabled() && me.isVisible()) {
            me.el.addCls(me.activeCls);
            me.focus();
            me.activated = true;
            me.fireEvent('activate', me);
        }
    },
    deactivate: function () {
        var me = this;
        if (me.activated) {
            me.el.removeCls(me.activeCls);
            me.blur();
            me.activated = false;
            me.fireEvent('deactivate', me);
        }
    },
    onClick: function (e) {
        var me = this;
        if (!me.href) {
            e.stopEvent();
        }
        if (me.disabled) {
            return;
        }
        Ext.callback(me.handler, me.scope || me, [
            me,
            e
        ]);
        me.fireEvent('click', me, e);
        if (!me.hideOnClick) {
            me.focus();
        }
        return Ext.isEmpty(Ext.fly(e.getTarget()).findParent('.close-button'));
    },
    updateTitleContainer: function () {
        this.down('#titleContainer').update({
            text1: this.text1,
            text2: this.text2
        });
    }
});
Ext.define('Admin.view.TopBarMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.topBarMenu',
    requires: [
        'Admin.view.TopBarMenuItem'
    ],
    cls: 'admin-topbar-menu',
    showSeparator: false,
    styleHtmlContent: true,
    overflowY: 'auto',
    overflowX: 'hidden',
    width: 300,
    layout: {
        type: 'vbox',
        align: 'stretchmax'
    },
    items: [
        {
            xtype: 'container',
            itemId: 'nonClosableItems',
            defaultType: 'topBarMenuItem'
        },
        {
            xtype: 'component',
            cls: 'title',
            itemId: 'editTitle',
            hidden: true,
            html: '<span>Editing</span>'
        },
        {
            xtype: 'container',
            itemId: 'editItems',
            defaultType: 'topBarMenuItem'
        },
        {
            xtype: 'component',
            cls: 'title',
            itemId: 'viewTitle',
            hidden: true,
            html: '<span>Viewing</span>'
        },
        {
            xtype: 'container',
            itemId: 'viewItems',
            defaultType: 'topBarMenuItem'
        },
        {
            xtype: 'component',
            cls: 'info',
            itemId: 'emptyTitle',
            hidden: false,
            html: 'List is empty'
        }
    ],
    tabPanel: undefined,
    activeTab: undefined,
    initComponent: function () {
        this.scrollState = {
            left: 0,
            top: 0
        };
        this.callParent(arguments);
        this.on('closeMenuItem', this.onCloseMenuItem);
        this.on('resize', this.updatePosition);
    },
    onClick: function (e) {
        var me = this, item;
        if (me.disabled) {
            e.stopEvent();
            return;
        }
        item = (e.type === 'click') ? me.getItemFromEvent(e) : me.activeItem;
        if (item && item.isMenuItem && item.onClick(e) !== false) {
            if (me.fireEvent('click', me, item, e) !== false && this.tabPanel) {
                this.tabPanel.setActiveTab(item.card);
            }
            this.hide();
        }
    },
    onShow: function () {
        this.callParent(arguments);
        if (this.activeTab) {
            this.markActiveTab(this.activeTab);
        }
    },
    onBoxReady: function () {
        var tip = Ext.DomHelper.append(this.el, {
            tag: 'div',
            cls: 'balloon-tip'
        }, true);
        this.callParent(arguments);
    },
    onCloseMenuItem: function (item) {
        if (this.tabPanel) {
            this.tabPanel.remove(item.card);
        }
        if (this.getAllItems(false).length === 0) {
            this.hide();
        }
    },
    markActiveTab: function (item) {
        var me = this;
        var menuItem;
        if (me.isVisible()) {
            menuItem = me.el.down('.current-tab');
            if (menuItem) {
                menuItem.removeCls('current-tab');
            }
            if (item) {
                menuItem = me.down('#' + item.id);
                if (menuItem && menuItem.el) {
                    menuItem.el.addCls('current-tab');
                }
            }
        }
        me.activeTab = item;
    },
    getItemFromEvent: function (e) {
        var item = this;
        do {
            item = item.getChildByElement(e.getTarget());
        }
        while (item && Ext.isDefined(item.getChildByElement) && item.getXType() !== 'topBarMenuItem');
        return item;
    },
    getAllItems: function (includeNonClosable) {
        var items = [];
        if (includeNonClosable === false) {
            items = items.concat(this.down('#editItems').query('topBarMenuItem'));
            items = items.concat(this.down('#viewItems').query('topBarMenuItem'));
        } else {
            items = items.concat(this.query('topBarMenuItem'));
        }
        return items;
    },
    addItems: function (items) {
        if (Ext.isEmpty(items)) {
            return;
        } else if (Ext.isObject(items)) {
            items = [].concat(items);
        }
        this.saveScrollState();
        var editItems = [];
        var viewItems = [];
        var nonClosableItems = [];
        Ext.Array.each(items, function (item) {
            if (item.closable === false) {
                nonClosableItems.push(item);
            } else if (item.editing) {
                editItems.push(item);
            } else {
                viewItems.push(item);
            }
        });
        var added = [];
        if (nonClosableItems.length > 0) {
            added = added.concat(this.down("#nonClosableItems").add(nonClosableItems));
        }
        if (editItems.length > 0) {
            added = added.concat(this.down('#editItems').add(editItems));
        }
        if (viewItems.length > 0) {
            added = added.concat(this.down('#viewItems').add(viewItems));
        }
        this.updateTitles();
        this.restoreScrollState();
        return added;
    },
    removeAllItems: function (includeNonClosable) {
        var editItems = this.down('#editItems');
        var viewItems = this.down('#viewItems');
        var removed = [];
        Ext.Array.each(editItems.items.items, function (item) {
            if (item && item.closable !== false) {
                removed.push(editItems.remove(item));
            }
        });
        Ext.Array.each(viewItems.items.items, function (item) {
            if (item && item.closable !== false) {
                removed.push(viewItems.remove(item));
            }
        });
        if (includeNonClosable) {
            var nonClosableItems = this.down('#nonClosableItems');
            Ext.Array.each(nonClosableItems.items.items, function (item) {
                if (item && item.closable !== false) {
                    removed.push(nonClosableItems.remove(item));
                }
            });
        }
        this.updateTitles();
        return removed;
    },
    removeItems: function (items) {
        if (Ext.isEmpty(items)) {
            return;
        } else if (Ext.isObject(items)) {
            items = [].concat(items);
        }
        this.saveScrollState();
        var editItems = this.down('#editItems');
        var viewItems = this.down('#viewItems');
        var nonClosableItems = this.down('#nonClosableItems');
        var removed = [];
        Ext.Array.each(items, function (item) {
            if (item && item.closable !== false) {
                removed.push(editItems.remove(item));
                removed.push(viewItems.remove(item));
                removed.push(nonClosableItems.remove(item));
            }
        });
        this.updateTitles();
        this.restoreScrollState();
    },
    updateTitles: function () {
        var editCount = this.down('#editItems').items.getCount();
        var viewCount = this.down('#viewItems').items.getCount();
        var nonClosableCount = this.down('#nonClosableItems').items.getCount();
        this.down('#editTitle')[editCount > 0 ? 'show' : 'hide']();
        this.down('#viewTitle')[viewCount > 0 ? 'show' : 'hide']();
        this.down('#emptyTitle')[(viewCount || editCount || nonClosableCount) > 0 ? 'hide' : 'show']();
    },
    updatePosition: function (menu, width, height, oldWidth, oldHeight, opts) {
        this.el.move('r', ((oldWidth - width) / 2), false);
    },
    show: function () {
        var me = this, parentEl, viewHeight;
        me.maxWas = me.maxHeight;
        if (!me.rendered) {
            me.doAutoRender();
        }
        if (me.floating) {
            parentEl = Ext.fly(me.el.getScopeParent());
            viewHeight = parentEl.getViewSize().height;
            me.maxHeight = Math.min(me.maxWas || viewHeight - 50, viewHeight - 50);
        }
        me.callParent(arguments);
        return me;
    },
    hide: function () {
        var me = this;
        me.callParent();
        me.maxHeight = me.maxWas;
    },
    setVerticalPosition: function () {
    },
    saveScrollState: function () {
        if (this.rendered && !this.hidden) {
            var dom = this.body.dom, state = this.scrollState;
            state.left = dom.scrollLeft;
            state.top = dom.scrollTop;
        }
    },
    restoreScrollState: function () {
        if (this.rendered && !this.hidden) {
            var dom = this.body.dom, state = this.scrollState;
            dom.scrollLeft = state.left;
            dom.scrollTop = state.top;
        }
    }
});
Ext.define('Admin.view.TopBar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.topBar',
    requires: [
        'Admin.view.TopBarMenu',
        'Admin.view.AdminImageButton',
        'Admin.lib.UriHelper'
    ],
    buttonAlign: 'center',
    cls: 'admin-topbar-panel',
    dock: 'top',
    plain: true,
    border: false,
    initComponent: function () {
        var me = this;
        this.startButton = Ext.create('Ext.button.Button', {
            xtype: 'button',
            itemId: 'app-launcher-button',
            margins: '0 8px 0 0',
            cls: 'start-button',
            handler: function (btn, evt) {
                me.toggleHomeScreen();
            }
        });
        this.homeButton = Ext.create('Ext.button.Button', {
            text: me.appName || '&lt; app name &gt;',
            cls: 'home-button',
            handler: function (btn, evt) {
                if (me.tabPanel) {
                    me.tabPanel.setActiveTab(0);
                }
            }
        });
        this.leftContainer = Ext.create('Ext.Container', {
            flex: 5,
            padding: 6,
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            items: [
                me.startButton,
                {
                    xtype: "tbseparator",
                    width: '2px'
                },
                me.homeButton
            ]
        });
        var Templates_common_userPopUp = '<div class="title">User</div>' + '<div class="user-name">{userName}</div>' +
                                         '<div class="content">' + '<div class="column"><img src="{photoUrl}"/>' +
                                         '<button class="x-btn-red-small">Log Out</button>' + '</div>' + '<div class="column">' +
                                         '<span>{qName}</span>' + '<a href="#">View Profile</a>' + '<a href="#">Edit Profile</a>' +
                                         '<a href="#">Change User</a>' + '</div>' + '</div>';
        this.rightContainer = Ext.create('Ext.Container', {
            flex: 5,
            layout: {
                type: 'hbox',
                align: 'middle',
                pack: 'end'
            },
            items: [
                {
                    xtype: 'adminImageButton',
                    icon: Admin.lib.UriHelper.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'),
                    popupTpl: Templates_common_userPopUp,
                    popupData: {
                        userName: "Thomas Lund Sigdestad",
                        photoUrl: Admin.lib.UriHelper.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'),
                        qName: 'system/tsi'
                    }
                }
            ]
        });
        this.items = [
            me.leftContainer,
            me.rightContainer
        ];
        if (this.tabPanel) {
            this.tabMenu = Ext.create('Admin.view.TopBarMenu', {
                tabPanel: me.tabPanel
            });
            this.titleButton = Ext.create('Ext.button.Button', {
                cls: 'title-button',
                menuAlign: 't-b?',
                menu: me.tabMenu,
                scale: 'medium',
                styleHtmlContent: true,
                text: '<span class="title">Title</span><span class="count">0</span>',
                setTitle: function (title) {
                    if (this.el) {
                        this.el.down('.title').setHTML(title);
                    }
                },
                setCount: function (count) {
                    if (this.el) {
                        this.el.down('.count').setHTML(count);
                    }
                }
            });
            Ext.Array.insert(me.items, 1, [
                me.titleButton
            ]);
        }
        this.callParent(arguments);
        this.syncTabCount();
    },
    toggleHomeScreen: function () {
        var isInsideIframe = window.top !== window.self;
        if (isInsideIframe) {
        } else {
            console.error('Can not toggle home screen. Document must be loaded inside the main window');
        }
    },
    insert: function (index, cfg) {
        var added = this.tabMenu.addItems(cfg);
        this.syncTabCount();
        return added.length === 1 ? added[0] : added;
    },
    setActiveTab: function (tab) {
        this.tabMenu.markActiveTab(tab);
        var card = tab.card;
        var buttonText = tab.text1;
        var iconClass;
        if ('tab-browse' === card.id) {
            buttonText = '';
        } else if (card.tab.iconClass) {
            iconClass = card.tab.iconClass;
        } else if (card.tab.editing) {
            iconClass = 'icon-icomoon-pencil-32';
        }
        this.titleButton.setIconCls(iconClass);
        this.setTitleButtonText(buttonText);
    },
    remove: function (tab) {
        var removed = this.tabMenu.removeItems(tab);
        this.syncTabCount();
        return removed;
    },
    findNextActivatable: function (tab) {
        if (this.tabPanel) {
            return this.tabPanel.items.get(0);
        }
    },
    createMenuItemFromTab: function (item) {
        var me = this;
        var cfg = item.initialConfig || item;
        return {
            tabBar: me,
            card: item,
            disabled: cfg.disabled,
            closable: cfg.closable,
            hidden: cfg.hidden && !item.hiddenByLayout,
            iconSrc: me.getMenuItemIcon(item),
            iconClass: cfg.iconClass,
            editing: cfg.editing || false,
            text1: Ext.String.ellipsis(me.getMenuItemDisplayName(item), 26),
            text2: Ext.String.ellipsis(me.getMenuItemDescription(item), 38)
        };
    },
    syncTabCount: function () {
        if (this.tabMenu && this.titleButton) {
            var tabCount = this.tabMenu.getAllItems(false).length;
            this.titleButton.setVisible(tabCount > 0);
            this.titleButton.setCount(tabCount);
        }
    },
    getApplicationId: function () {
        var urlParamsString = document.URL.split('?'), urlParams = Ext.urlDecode(urlParamsString[urlParamsString.length - 1]);
        return urlParams.appId ? urlParams.appId.split('#')[0] : null;
    },
    getMenuItemIcon: function (card) {
        var icon;
        if (card.data && card.data instanceof Ext.data.Model) {
            icon = card.data.get('iconUrl') || card.data.get('image_url');
        }
        return icon;
    },
    getMenuItemDescription: function (card) {
        var desc;
        if (!card.isNew && card.data && card.data instanceof Ext.data.Model) {
            desc = card.data.get('path') || card.data.get('qualifiedName') || card.data.get('displayName');
        }
        if (!desc) {
            desc = card.title;
        }
        return desc;
    },
    getMenuItemDisplayName: function (card) {
        var desc;
        if (!card.isNew && card.data && card.data instanceof Ext.data.Model) {
            desc = card.data.get('displayName') || card.data.get('name');
        }
        if (!desc) {
            desc = card.title;
        }
        return desc;
    },
    setTitleButtonText: function (text) {
        this.titleButton.setTitle(text);
        var activeTab = this.titleButton.menu.activeTab;
        if (activeTab) {
            activeTab.text1 = text;
            activeTab.updateTitleContainer();
        }
    },
    getStartButton: function () {
        return this.startButton;
    },
    getLeftContainer: function () {
        return this.leftContainer;
    },
    getRightContainer: function () {
        return this.rightContainer;
    }
});
Ext.define('Admin.view.TabPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.cmsTabPanel',
    requires: [
        'Admin.view.TopBar'
    ],
    border: false,
    defaults: {
        closable: true
    },
    initComponent: function () {
        var me = this, dockedItems = [].concat(me.dockedItems || []), activeTab = me.activeTab || (me.activeTab = 0);
        me.layout = new Ext.layout.container.Card(Ext.apply({
            owner: me,
            deferredRender: me.deferredRender,
            itemCls: me.itemCls,
            activeItem: me.activeTab
        }, me.layout));
        this.tabBar = Ext.create('Admin.view.TopBar', Ext.apply({
            appName: me.appName,
            appIconCls: me.appIconCls,
            tabPanel: me
        }, me.tabBar));
        dockedItems.push(me.tabBar);
        me.dockedItems = dockedItems;
        me.addEvents('beforetabchange', 'tabchange');
        me.superclass.superclass.initComponent.apply(me, arguments);
        me.activeTab = me.getComponent(activeTab);
        if (me.activeTab) {
            me.activeTab.tab.activate(true);
            me.tabBar.setActiveTab(me.activeTab.tab);
        }
    },
    addTab: function (item, index, requestConfig) {
        var me = this;
        var tab = this.getTabById(item.id);
        if (!tab) {
            tab = this.insert(index || this.items.length, item);
            if (requestConfig) {
                this.setActiveTab(tab);
                var mask = new Ext.LoadMask(tab, {
                    msg: "Please wait..."
                });
                mask.show();
                var createTabFromResponse = requestConfig.createTabFromResponse;
                var onRequestConfigSuccess = function (response) {
                    var tabContent = createTabFromResponse(response);
                    tab.add(tabContent);
                    mask.hide();
                    tab.on('activate', function () {
                        this.doLayout();
                    }, tab, {
                        single: true
                    });
                };
                requestConfig.doTabRequest(onRequestConfigSuccess);
            }
        }
        this.setActiveTab(tab);
        return tab;
    },
    getTabById: function (id) {
        return this.getComponent(id);
    },
    removeAllOpenTabs: function () {
        var all = this.items.items;
        var last = all[this.getTabCount() - 1];
        while (this.getTabCount() > 1) {
            this.remove(last);
            last = this.items.items[this.getTabCount() - 1];
        }
    },
    getTabCount: function () {
        return this.items.items.length;
    },
    onAdd: function (item, index) {
        var me = this, cfg = item.tabConfig || {
        };
        cfg = Ext.applyIf(cfg, me.tabBar.createMenuItemFromTab(item));
        item.tab = me.tabBar.insert(index, cfg);
        item.on({
            scope: me,
            enable: me.onItemEnable,
            disable: me.onItemDisable,
            beforeshow: me.onItemBeforeShow,
            iconchange: me.onItemIconChange,
            iconclschange: me.onItemIconClsChange,
            titlechange: me.onItemTitleChange
        });
        if (item.isPanel) {
            if (me.removePanelHeader) {
                if (item.rendered) {
                    if (item.header) {
                        item.header.hide();
                    }
                } else {
                    item.header = false;
                }
            }
            if (item.isPanel && me.border) {
                item.setBorder(false);
            }
        }
    },
    doRemove: function (item, autoDestroy) {
        var me = this;
        if (me.destroying || me.items.getCount() === 1) {
            me.activeTab = null;
        } else if (me.activeTab === item) {
            var toActivate = me.tabBar.findNextActivatable(item.tab);
            if (toActivate) {
                me.setActiveTab(toActivate);
            }
        }
        this.callParent(arguments);
    },
    onRemove: function (item, destroying) {
        var me = this;
        item.un({
            scope: me,
            enable: me.onItemEnable,
            disable: me.onItemDisable,
            beforeshow: me.onItemBeforeShow
        });
        if (!me.destroying) {
            me.tabBar.remove(item.tab);
        }
    }
});
Ext.define('Admin.view.FilterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.filterPanel',
    cls: 'admin-filter',
    header: false,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    autoScroll: true,
    split: true,
    includeSearch: true,
    includeEmptyFacets: 'none',
    updateCountCriteria: 'always',
    updateCountStrategy: 'notlast',
    facetTpl: undefined,
    facetData: undefined,
    initComponent: function () {
        var me = this;
        if (!Ext.isEmpty(this.title)) {
            this.originalTitle = this.title;
        }
        Ext.applyIf(this, {
            items: [],
            facetTpl: new Ext.XTemplate('<tpl for=".">', '<div class="admin-facet-group" name="{name}">',
                '<h2>{[values.displayName || values.name]}</h2>', '<tpl for="terms">{[this.updateFacetCount(values, parent)]}',
                '<tpl if="this.shouldShowTerm(values, parent)">', '<div class="admin-facet {[values.selected ? \'checked\' : \'\']}">',
                '<input type="checkbox" id="facet-{term}" value="{name}" class="admin-facet-cb" name="{parent.name}" {[values.selected ? \'checked="true"\' : \'\']} />',
                '<label for="facet-{key}" class="admin-facet-lbl"> {[values.displayName || values.name]} ({[this.getTermCount(values)]})</label>',
                '</div>', '</tpl>', '</tpl>', '</div>', '</tpl>', {
                    updateFacetCount: function (term, facet) {
                        var isCriteria = me.updateCountCriteria == 'always' || (me.updateCountCriteria == 'query' && me.queryDirty);
                        var isStrategy = me.updateCountStrategy == 'all' ||
                                         (me.updateCountStrategy == 'notlast' && me.lastFacetName != facet.name);
                        var isDefined = Ext.isDefined(me.facetCountMap[term.name]);
                        var isDirty = me.isDirty();
                        if (!isDirty || !isDefined || (isCriteria && isStrategy)) {
                            me.facetCountMap[term.name] = term.count;
                        }
                    },
                    shouldShowTerm: function (term, facet) {
                        return me.includeEmptyFacets == 'all' ||
                               (me.includeEmptyFacets == 'last' && (!me.lastFacetName || me.lastFacetName == facet.name)) ||
                               me.facetCountMap[term.name] > 0 || term.selected || this.isSelected(term, facet);
                    },
                    getTermCount: function (term) {
                        return me.facetCountMap[term.name];
                    },
                    isSelected: function (term, facet) {
                        var terms = me.selectedValues[facet.name];
                        if (terms) {
                            return Ext.Array.contains(terms, term.name);
                        }
                        return false;
                    }
                })
        });
        this.facetContainer = Ext.create('Ext.Component', {
            xtype: 'component',
            itemId: 'facetContainer',
            tpl: me.facetTpl,
            data: me.facetData,
            listeners: {
                afterrender: function (cmp) {
                    cmp.el.on('click', me.onFacetClicked, me, {
                        delegate: '.admin-facet'
                    });
                }
            }
        });
        this.items.unshift(this.facetContainer);
        this.clearLink = Ext.create('Ext.Component', {
            xtype: 'component',
            html: '<a href="javascript:;">Clear filter</a>',
            listeners: {
                click: {
                    element: 'el',
                    fn: me.reset,
                    scope: me
                },
                afterrender: function (cmp) {
                    cmp.el.setStyle('visibility', 'hidden');
                }
            }
        });
        this.items.unshift(this.clearLink);
        if (this.includeSearch) {
            this.searchField = Ext.create('Ext.form.field.Text', {
                xtype: 'textfield',
                cls: 'admin-search-trigger',
                enableKeyEvents: true,
                bubbleEvents: [
                    'specialkey'
                ],
                itemId: 'filterText',
                margin: '0 0 10 0',
                name: 'query',
                emptyText: 'Search',
                listeners: {
                    specialkey: {
                        fn: me.onKeyPressed,
                        scope: me
                    },
                    keypress: {
                        fn: me.onKeyPressed,
                        scope: me
                    }
                }
            });
            this.items.unshift(this.searchField);
        }
        this.facetCountMap = [];
        this.callParent(arguments);
        this.addEvents('search', 'reset');
    },
    onKeyPressed: function (field, event, opts) {
        if (this.suspendEvents !== true) {
            if (event.getKey() === event.ENTER) {
                if (event.type === "keydown") {
                    this.fireEvent('search', this.getValues());
                }
            } else {
                var me = this;
                if (this.searchFilterTypingTimer !== null) {
                    window.clearTimeout(this.searchFilterTypingTimer);
                    this.searchFilterTypingTimer = null;
                }
                this.searchFilterTypingTimer = window.setTimeout(function () {
                    if (me.updateCountCriteria === 'query') {
                        me.queryDirty = true;
                    }
                    me.lastFacetName = undefined;
                    me.search();
                }, 500);
            }
        }
    },
    onFacetClicked: function (event, target, opts) {
        target = Ext.fly(target);
        var facet = target.hasCls('admin-facet') ? target : target.up('.admin-facet');
        if (facet) {
            var cb = facet.down('input[type=checkbox]', true);
            var checked = cb.hasAttribute("checked");
            if (checked) {
                cb.removeAttribute("checked");
                facet.removeCls("checked");
            } else {
                cb.setAttribute("checked", "true");
                facet.addCls("checked");
            }
            var group = facet.up('.admin-facet-group', true);
            if (group) {
                this.lastFacetName = group.getAttribute('name');
            }
            this.search();
        }
        event.stopEvent();
        return true;
    },
    updateFacets: function (facets) {
        if (facets) {
            this.selectedValues = this.getValues();
            this.down('#facetContainer').update(facets);
            this.setValues(this.selectedValues);
        }
    },
    getValues: function () {
        var selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
        var values = {
        };
        if (this.searchField) {
            var query = this.searchField.getValue();
            if (Ext.String.trim(query).length > 0) {
                values[this.searchField.name] = query;
            }
        }
        Ext.Array.each(selectedCheckboxes, function (cb) {
            var oldValue = values[cb.name];
            if (Ext.isArray(oldValue)) {
                oldValue.push(cb.value);
            } else {
                values[cb.name] = [
                    cb.value
                ];
            }
        });
        return values;
    },
    setValues: function (values) {
        var me = this;
        if (this.searchField) {
            this.searchField.setValue(values[this.searchField.name]);
        }
        var checkboxes = Ext.query('.admin-facet-group input[type=checkbox]', this.facetContainer.el.dom);
        var checkedCount = 0, facet;
        Ext.Array.each(checkboxes, function (cb) {
            var facet = Ext.fly(cb).up('.admin-facet');
            if (me.isValueChecked(cb.value, values)) {
                checkedCount++;
                cb.setAttribute('checked', 'true');
                facet.addCls('checked');
            } else {
                cb.removeAttribute('checked');
                facet.removeCls('checked');
            }
        });
        if (this.updateCountCriteria == 'query' && this.queryDirty && checkedCount === 0) {
            this.queryDirty = false;
        }
    },
    isValueChecked: function (value, values) {
        for (var facet in values) {
            if (values.hasOwnProperty(facet)) {
                var terms = [].concat(values[facet]);
                for (var i = 0; i < terms.length; i++) {
                    if (terms[i] === value) {
                        return true;
                    }
                }
            }
        }
        return false;
    },
    isDirty: function () {
        var selectedCheckboxes = [];
        var query = '';
        if (this.facetContainer && this.facetContainer.el) {
            selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
        }
        if (this.searchField) {
            query = Ext.String.trim(this.searchField.getValue());
        }
        return selectedCheckboxes.length > 0 || query.length > 0;
    },
    reset: function () {
        if (this.fireEvent('reset', this.isDirty()) !== false) {
            if (this.searchField) {
                this.searchField.reset();
            }
            var selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
            Ext.Array.each(selectedCheckboxes, function (cb) {
                cb.removeAttribute('checked');
                Ext.fly(cb).up('.admin-facet').removeCls('checked');
            });
            this.clearLink.el.setStyle('visibility', 'hidden');
            this.lastFacetName = undefined;
        }
    },
    search: function () {
        if (this.fireEvent('search', this.getValues()) !== false) {
            this.clearLink.el.setStyle('visibility', this.isDirty() ? 'visible' : 'hidden');
        }
    }
});
Ext.define('Admin.view.contentManager.FilterPanel', {
    extend: 'Admin.view.FilterPanel',
    alias: 'widget.contentFilter'
});
Ext.define('Admin.view.contentManager.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentShow',
    layout: 'border',
    border: false,
    initComponent: function () {
        var contentIsOpenedFromPortal = document.location.href.indexOf('/open') > -1;
        this.items = [
            {
                region: 'north',
                xtype: 'browseToolbar'
            },
            {
                xtype: 'contentTreeGridPanel',
                region: 'center',
                itemId: 'contentList',
                flex: 1
            },
            {
                region: 'south',
                split: true,
                collapsible: true,
                header: false,
                xtype: 'contentDetail',
                isLiveMode: contentIsOpenedFromPortal,
                showToolbar: false,
                flex: 1
            },
            {
                region: 'east',
                split: true,
                collapsible: true,
                header: false,
                xtype: 'contentDetail',
                isLiveMode: contentIsOpenedFromPortal,
                showToolbar: false,
                flex: 1,
                hidden: true,
                isVertical: true
            }
        ];
        this.callParent(arguments);
    }
});
Ext.define('Admin.view.contentManager.BrowseToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.browseToolbar',
    requires: [
        'Ext.ux.toggleslide.ToggleSlide'
    ],
    cls: 'admin-toolbar',
    border: true,
    defaults: {
        scale: 'medium',
        iconAlign: 'top',
        minWidth: 64
    },
    initComponent: function () {
        this.items = [
            {
                text: ' New',
                disabled: true,
                action: 'newContent'
            },
            {
                text: 'Edit',
                action: 'editContent'
            },
            {
                text: 'Open',
                action: 'viewContent'
            },
            {
                text: 'Delete',
                action: 'deleteContent'
            },
            {
                text: 'Duplicate',
                action: 'duplicateContent'
            },
            {
                text: 'Move',
                disabled: true,
                action: 'moveContent'
            },
            '->',
            {
                xtype: 'toggleslide',
                onText: 'Preview',
                offText: 'Details',
                action: 'toggleLive',
                state: this.isLiveMode
            },
            {
                iconCls: 'icon-toolbar-settings',
                action: 'showToolbarMenu',
                minWidth: 42,
                padding: '6 8 6 12'
            }
        ];
        this.callParent(arguments);
    }
});
Ext.define('Admin.view.BaseContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.baseContextMenu',
    cls: 'admin-context-menu',
    border: false,
    shadow: false
});
Ext.define('Admin.view.contentManager.ContextMenu', {
    extend: 'Admin.view.BaseContextMenu',
    alias: 'widget.contentManagerContextMenu',
    items: [
        {
            text: ' New',
            icon: undefined,
            action: 'newContent',
            disableOnMultipleSelection: true
        },
        {
            text: 'Edit',
            icon: undefined,
            action: 'editContent',
            disableOnMultipleSelection: false
        },
        {
            text: 'Open',
            icon: undefined,
            action: 'viewContent',
            disableOnMultipleSelection: false
        },
        {
            text: 'Delete',
            icon: undefined,
            action: 'deleteContent'
        },
        {
            text: 'Duplicate',
            icon: undefined,
            action: 'duplicateContent'
        },
        {
            text: 'Move',
            icon: undefined,
            disabled: true,
            action: 'moveContent'
        }
    ]
});
Ext.define('Admin.view.BaseTreeGridPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.treeGridPanel',
    layout: 'card',
    requires: [
        'Admin.plugin.PersistentGridSelectionPlugin',
        'Admin.plugin.GridToolbarPlugin'
    ],
    treeConf: {
    },
    gridConf: {
    },
    keyField: 'key',
    nameTemplate: '<div class="admin-{0}-thumbnail">' + '<img src="{1}"/>' + '</div>' + '<div class="admin-{0}-description">' +
                  '<h6>{2}</h6>' + '<p>{3}</p>' + '</div>',
    initComponent: function () {
        var me = this;
        var treeColumns = Ext.clone(this.columns);
        if (Ext.isEmpty(treeColumns)) {
            throw "this.columns can't be null";
        }
        treeColumns[0].xtype = 'treecolumn';
        var treeSelectionPlugin = new Admin.plugin.PersistentGridSelectionPlugin({
            keyField: me.keyField
        });
        var treePanel = {
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
                    store: me.treeStore
                }
            },
            store: this.treeStore,
            columns: treeColumns,
            plugins: [
                treeSelectionPlugin
            ]
        };
        treePanel = Ext.apply(treePanel, me.treeConf);
        var gridSelectionPlugin = new Admin.plugin.PersistentGridSelectionPlugin({
            keyField: me.keyField
        });
        var gridPanel = {
            xtype: 'grid',
            itemId: 'grid',
            cls: 'admin-grid',
            border: false,
            hideHeaders: true,
            viewConfig: {
                trackOver: true,
                stripeRows: true,
                loadMask: {
                    store: me.store
                }
            },
            store: this.store,
            columns: this.columns,
            plugins: [
                gridSelectionPlugin
            ]
        };
        gridPanel = Ext.apply(gridPanel, me.gridConf);
        this.items = [
            treePanel,
            gridPanel
        ];
        this.callParent(arguments);
        var grid = this.down('#grid');
        grid.addDocked({
            xtype: 'toolbar',
            itemId: 'selectionToolbar',
            cls: 'admin-white-toolbar',
            dock: 'top',
            store: this.store,
            gridPanel: grid,
            resultCountHidden: true,
            plugins: [
                'gridToolbarPlugin'
            ]
        });
        grid.getStore().on('datachanged', this.fireUpdateEvent, this);
        var tree = this.down('#tree');
        tree.addDocked({
            xtype: 'toolbar',
            itemId: 'selectionToolbar',
            cls: 'admin-white-toolbar',
            dock: 'top',
            store: this.treeStore,
            gridPanel: tree,
            resultCountHidden: true,
            countTopLevelOnly: true,
            plugins: [
                'gridToolbarPlugin'
            ]
        });
        this.addEvents('datachanged');
    },
    fireUpdateEvent: function (values) {
        this.fireEvent('datachanged', values);
    },
    setActiveList: function (listId) {
        this.getLayout().setActiveItem(listId);
    },
    getActiveList: function () {
        return this.getLayout().getActiveItem();
    },
    getSelection: function () {
        var selection = [], activeList = this.getActiveList(), plugin = activeList.getPlugin('persistentGridSelection');
        if (plugin) {
            selection = plugin.getSelection();
        } else {
            selection = activeList.getSelectionModel().getSelection();
        }
        return selection;
    },
    select: function (key, keepExisting) {
        var activeList = this.getActiveList();
        var selModel = activeList.getSelectionModel();
        var keys = [].concat(key);
        var i;
        if (activeList.xtype === 'treepanel') {
            var rootNode = activeList.getRootNode(), node;
            for (i = 0; i < keys.length; i++) {
                node = rootNode.findChild(this.keyField, keys[i], true);
                if (node) {
                    selModel.select(node, keepExisting);
                }
            }
        } else if (activeList.xtype === 'grid') {
            var store = activeList.getStore(), record;
            for (i = 0; i < keys.length; i++) {
                record = store.findRecord(this.keyField, keys[i]);
                if (record) {
                    selModel.select(record, keepExisting);
                }
            }
        }
    },
    deselect: function (key) {
        var activeList = this.getActiveList(), plugin = activeList.getPlugin('persistentGridSelection'), selModel = plugin ? plugin
            : activeList.getSelectionModel();
        if (!key || key === -1) {
            if (plugin) {
                plugin.clearSelection();
            } else {
                selModel.deselectAll();
            }
        } else {
            if (activeList.xtype === 'treepanel') {
                var selNodes = selModel.getSelection();
                var i;
                for (i = 0; i < selNodes.length; i++) {
                    var selNode = selNodes[i];
                    if (key == selNode.get(this.keyField)) {
                        selModel.deselect(selNode);
                    }
                }
            } else if (activeList.xtype === 'grid') {
                var record = activeList.getStore().findRecord(this.keyField, key);
                if (record) {
                    selModel.deselect(record);
                }
            }
        }
    },
    setRemoteSearchParams: function (params) {
        var activeList = this.getActiveList();
        var currentStore = activeList.store;
        currentStore.getProxy().extraParams = params;
    },
    setResultCountVisible: function (visible) {
        this.getActiveList().getDockedComponent('selectionToolbar').getPlugin('gridToolbarPlugin').setResultCountVisible(visible);
    },
    updateResultCount: function (count) {
        this.getActiveList().getDockedComponent('selectionToolbar').getPlugin('gridToolbarPlugin').updateResultCount(count);
    },
    removeAll: function () {
        var activeList = this.getActiveList();
        if (activeList.xtype === 'treepanel') {
            activeList.getRootNode().removeAll();
        } else {
            activeList.removeAll();
        }
    },
    refresh: function () {
        var activeList = this.getActiveList();
        var currentStore = activeList.store;
        if (!currentStore.loading) {
            if (activeList.xtype === 'treepanel') {
                currentStore.load();
            } else if (activeList.xtype === 'grid') {
                currentStore.loadPage(currentStore.currentPage);
            }
        }
    }
});
Ext.define('Admin.view.contentManager.TreeGridPanel', {
    extend: 'Admin.view.BaseTreeGridPanel',
    alias: 'widget.contentTreeGridPanel',
    store: 'Admin.store.contentManager.ContentStore',
    treeStore: 'Admin.store.contentManager.ContentTreeStore',
    border: false,
    keyField: 'path',
    gridConf: {
        selModel: Ext.create('Ext.selection.CheckboxModel', {
            headerWidth: 36
        })
    },
    treeConf: {
        selModel: Ext.create('Ext.selection.CheckboxModel', {
            headerWidth: 36
        })
    },
    initComponent: function () {
        var me = this;
        this.columns = [
            {
                text: 'Display Name',
                dataIndex: 'displayName',
                sortable: true,
                renderer: this.nameRenderer,
                scope: me,
                flex: 1
            },
            {
                text: 'Status',
                renderer: this.statusRenderer
            },
            {
                text: 'Modified',
                dataIndex: 'modifiedTime',
                renderer: this.prettyDateRenderer,
                scope: me,
                sortable: true
            }
        ];
        this.callParent(arguments);
    },
    nameRenderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        var account = record.data;
        var activeListType = this.getActiveList().itemId;
        var Templates_contentManager_treeGridPanelNameRenderer = '<div class="admin-{0}-thumbnail">' + '<img src="{1}"/>' + '</div>' +
                                                                 '<div class="admin-{0}-description">' + '<h6>{2}</h6>' + '<p>{3}</p>' +
                                                                 '</div>';
        return Ext.String.format(Templates_contentManager_treeGridPanelNameRenderer, activeListType, account.iconUrl, value, account.path);
    },
    statusRenderer: function () {
        return "Online";
    },
    prettyDateRenderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        try {
        }
        catch (e) {
            return value;
        }
    }
});
Ext.define('Admin.view.contentManager.DetailToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentDetailToolbar',
    cls: 'admin-toolbar',
    isLiveMode: false,
    requires: [
        'Ext.ux.toggleslide.ToggleSlide'
    ],
    defaults: {
        scale: 'medium'
    },
    initComponent: function () {
        var me = this;
        this.items = [
            {
                text: 'New',
                action: 'newContent'
            },
            {
                text: 'Edit',
                action: 'editContent'
            },
            {
                text: 'Delete',
                action: 'deleteContent'
            },
            {
                text: 'Duplicate',
                action: 'duplicateContent'
            },
            {
                text: 'Move',
                action: 'moveContent'
            },
            {
                text: 'Export'
            },
            '->',
            {
                xtype: 'cycle',
                itemId: 'deviceCycle',
                disabled: !me.isLiveMode,
                showText: true,
                prependText: 'Device: ',
                menu: {
                    items: [
                        {
                            text: 'Desktop',
                            checked: true,
                            device: 'DESKTOP'
                        },
                        {
                            text: 'iPhone 5 Vertical',
                            device: 'IPHONE_5_VERTICAL'
                        },
                        {
                            text: 'iPhone 5 Horizontal',
                            device: 'IPHONE_5_HORIZONTAL'
                        },
                        {
                            text: 'iPad 3 Vertical',
                            device: 'IPAD_3_VERTICAL'
                        },
                        {
                            text: 'iPad 3 Horizontal',
                            device: 'IPAD_3_HORIZONTAL'
                        }
                    ]
                }
            },
            {
                xtype: 'toggleslide',
                onText: 'Preview',
                offText: 'Details',
                action: 'toggleLive',
                state: me.isLiveMode,
                listeners: {
                    change: function (toggle, state) {
                        me.isLiveMode = state;
                    }
                }
            },
            {
                text: 'Close',
                action: 'closeContent'
            }
        ];
        this.callParent(arguments);
    }
});
Ext.define('Admin.view.contentManager.LivePreview', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentLive',
    bodyStyle: {
        backgroundColor: '#ccc'
    },
    html: '<div style="display:table; height:100%; width: 100%;"><div style="display:table-row"><div style="display:table-cell; height:100%; vertical-align: middle; text-align:center;"><iframe style="border: 0 none; width: 100%; height: 100%; box-shadow: 0 0 10px rgba(0, 0, 0, 0.2)"></iframe></div></div></div>',
    autoScroll: false,
    styleHtmlContent: true,
    layout: 'fit',
    iFrameLoaded: false,
    initComponent: function () {
        var me = this;
        me.on('afterrender', function () {
            if (me.actionButton) {
                me.renderActionButton();
            }
        });
        this.callParent(arguments);
    },
    getIframe: function () {
        return this.getTargetEl().down('iframe');
    },
    resizeIframe: function (dimmensions) {
        var iFrame = this.getIframe(), widthHasPercentUnit = dimmensions.width.indexOf('%') >
                                                             -1, heightHasPercentUnit = dimmensions.height.indexOf('%') >
                                                                                        -1, width = widthHasPercentUnit ? this.getWidth()
            : dimmensions.width, height = heightHasPercentUnit ? this.getHeight() : dimmensions.height;
        var animation = iFrame.animate({
            duration: 300,
            to: {
                width: width,
                height: height
            },
            listeners: {
                afteranimate: function () {
                    if (widthHasPercentUnit) {
                        iFrame.setStyle('width', dimmensions.width);
                    }
                    if (heightHasPercentUnit) {
                        iFrame.setStyle('height', dimmensions.height);
                    }
                }
            }
        });
    },
    renderActionButton: function () {
        var me = this;
        Ext.create('widget.container', {
            renderTo: me.getEl(),
            floating: true,
            shadow: false,
            padding: '5 20 0',
            style: 'width: 100%; text-align: right',
            border: 0,
            items: [
                {
                    xtype: 'tbfill'
                },
                Ext.apply(me.actionButton, {
                    border: 0
                })
            ]
        });
    },
    load: function (url, isEdit) {
        var iFrame = this.getIframe();
        isEdit = isEdit || false;
        if (!Ext.isEmpty(url)) {
            iFrame.dom.src = Admin.lib.UriHelper.getAbsoluteUri(url + "?edit=" + isEdit);
            this.iFrameLoaded = true;
        } else {
            iFrame.update("<h2 class='message'>Page can't be found.</h2>");
        }
    }
});
Ext.define('Admin.view.DropDownButton', {
    extend: 'Ext.button.Button',
    alias: 'widget.dropDownButton',
    cls: 'admin-dropdown-button',
    width: 120,
    padding: 5,
    menuItems: [],
    initComponent: function () {
        this.menu = this.createMenu();
        this.callParent(arguments);
    },
    createMenu: function () {
        var me = this;
        return Ext.create('Admin.view.BaseContextMenu', {
            width: 120,
            items: this.menuItems
        });
    }
});
Ext.define('Admin.view.IframeContainer', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.iframe',
    url: undefined,
    html: '<iframe style="border: 0 none; width: 100%; height: 100%;"></iframe>',
    autoScroll: false,
    styleHtmlContent: true,
    iFrameCls: undefined,
    minHeight: 420,
    listeners: {
        afterrender: function (panel) {
            if (this.url) {
                this.load(this.url);
            }
        }
    },
    initComponent: function () {
        this.callParent(arguments);
    },
    load: function (url, isEdit) {
        var iframe = this.getEl().down('iframe');
        if (!Ext.isEmpty(url)) {
            iframe.dom.src = Admin.lib.UriHelper.getAbsoluteUri(url);
        } else {
            iframe.update("<h2 class='message'>Page can't be found.</h2>");
        }
    }
});
Ext.define('Admin.view.BaseDetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.detailPanel',
    layout: 'card',
    cls: 'admin-preview-panel admin-detail',
    border: false,
    showToolbar: true,
    isVertical: false,
    isFullPage: false,
    keyField: 'id',
    listeners: {
        afterrender: function (detail) {
            detail.el.on('click', function (event, target, opts) {
                var key = target.attributes.getNamedItem('id').nodeValue.split('remove-from-selection-button:')[1];
                detail.fireEvent('deselect', key);
            }, this, {
                delegate: '.deselect'
            });
            detail.el.on('click', function (event, target, opts) {
                detail.fireEvent('clearselection');
            }, this, {
                delegate: '.clearSelection'
            });
            if (this.isFullPage) {
                this.hideActionButton();
            }
            if (this.singleSelection.tabs.length > 0) {
                this.changeTab(this.singleSelection.tabs[0].name);
            }
        }
    },
    initComponent: function () {
        if (this.showToolbar) {
            this.tbar = this.createToolBar();
        }
        if (this.isVertical) {
            this.cls = this.cls + 'admin-detail-vertical';
        }
        this.callParent(arguments);
    },
    createNoSelection: function () {
        return {
            itemId: 'noSelection',
            xtype: 'panel',
            styleHtmlContent: true,
            padding: 10,
            bodyStyle: {
                border: 'none'
            },
            html: '<div>Nothing selected</div>'
        };
    },
    hideActionButton: function () {
        var actionsButton = this.down('dropDownButton');
        if (actionsButton) {
            actionsButton.setVisible(false);
        }
    },
    actionButtonItems: [],
    getActionItems: function () {
        return this.actionButtonItems;
    },
    getActionButton: function () {
        var me = this;
        if (this.actionButtonItems.length < 1) {
            return {
            };
        }
        return {
            xtype: 'dropDownButton',
            text: 'Actions',
            height: 30,
            itemId: 'dropdown',
            width: 120,
            tdAttrs: {
                width: 120,
                valign: 'top',
                style: {
                    padding: '0 20px 0 0'
                }
            },
            menuItems: me.getActionItems()
        };
    },
    singleTemplate: {
        photo: '<img src="{data.iconUrl}?size=80" style="width: 64px;" alt="{name}"/>',
        header: '<h1 title="{data.displayName}">{data.displayName}</h1><span class="path" title="{data.path}">{data.path}</span>'
    },
    singleSelection: {
        tabs: [],
        tabData: {
        }
    },
    createSingleSelection: function (data) {
        var me = this;
        return {
            xtype: 'container',
            itemId: 'singleSelection',
            layout: 'border',
            defaults: {
                border: 0
            },
            overflowX: 'hidden',
            overflowY: 'hidden',
            items: [
                {
                    xtype: 'container',
                    region: 'north',
                    cls: 'north',
                    margin: '5 0',
                    height: (me.isVertical ? 100 : 64),
                    layout: {
                        type: 'table',
                        tableAttrs: {
                            style: {
                                tableLayout: 'fixed',
                                width: '100%'
                            }
                        },
                        columns: 3
                    },
                    defaults: {
                        height: 64,
                        border: 0
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: 64,
                            itemId: 'previewPhoto',
                            tpl: me.singleTemplate.photo,
                            data: data,
                            margin: '0 5 0 5',
                            tdAttrs: {
                                width: 80
                            }
                        },
                        {
                            xtype: 'component',
                            itemId: 'previewHeader',
                            tpl: me.singleTemplate.header,
                            data: data,
                            cls: 'admin-detail-header'
                        },
                        me.getActionButton(),
                        me.renderTabNavigation(me.isVertical)
                    ]
                },
                me.renderWestContainer(),
                {
                    region: 'center',
                    cls: 'center',
                    xtype: 'container',
                    itemId: 'center'
                }
            ]
        };
    },
    renderWestContainer: function () {
        var me = this;
        if (me.isVertical) {
            return {
            };
        }
        return {
            xtype: 'container',
            region: 'west',
            cls: 'west',
            width: 200,
            items: [
                me.renderTabNavigation(true)
            ]
        };
    },
    renderTabNavigation: function (doRender) {
        var me = this;
        if (!doRender) {
            return {
            };
        }
        return {
            xtype: 'component',
            cls: (me.isVertical ? 'vertical' : 'horizontal'),
            margin: (me.isVertical ? '0' : '20 0 0'),
            colspan: 3,
            tpl: Ext.create('Ext.XTemplate',
                '<ul class="admin-detail-nav">' + '<tpl for=".">' + '<li data-tab="{name}">{displayName}</li>' + '</tpl>' + '</ul>'),
            data: me.singleSelection.tabs,
            listeners: {
                click: {
                    element: 'el',
                    fn: function (evt, element) {
                        var tab = element.attributes['data-tab'].value;
                        var panels = Ext.ComponentQuery.query('contentDetail');
                        for (var i = 0; i < panels.length; i++) {
                            panels[i].changeTab(tab);
                        }
                    }
                }
            }
        };
    },
    getLargeBoxTemplate: function () {
        return [
            '<tpl for=".">' + '<div id="selected-item-box-{data.' + this.keyField + '}" class="admin-selected-item-box large clearfix">',
            '<div class="left"><img src="{data.iconUrl}?size=32" alt="{data.name}"/></div>',
            '<div class="center"><h6>{data.displayName}</h6>',
            '<tpl if="data.path">',
            '<p>{data.path}</p>',
            '<tpl elseif="data.description">',
            '<p>{data.description}</p>',
            '<tpl elseif="data.name">',
            '<p>{data.name}</p>',
            '</tpl>',
            '</div>',
            '<div class="right">',
            '<a id="remove-from-selection-button:{data.' + this.keyField +
            '}" class="deselect icon-remove icon-2x" href="javascript:;"></a>',
            '</div>',
            '</div>',
            '</tpl>'
        ];
    },
    createLargeBoxSelection: function (data) {
        return {
            itemId: 'largeBoxSelection',
            xtype: 'component',
            styleHtmlContent: true,
            padding: 10,
            bodyStyle: {
                border: 'none'
            },
            autoScroll: true,
            tpl: this.getLargeBoxTemplate(),
            data: data
        };
    },
    getSmallBoxTemplate: function () {
        return [
            '<tpl for=".">',
            '<div id="selected-item-box-{data.' + this.keyField + '}" class="admin-selected-item-box small clearfix">',
            '<div class="left"><img src="{data.iconUrl}?size=20" alt="{data.name}"/></div>',
            '<div class="center">{data.displayName}</div>',
            '<div class="right">',
            '<a id="remove-from-selection-button:{data.' + this.keyField +
            '}" class="deselect icon-remove icon-large" href="javascript:;"></a>',
            '</div>',
            '</div>',
            '</tpl>'
        ];
    },
    createSmallBoxSelection: function (data) {
        return {
            itemId: 'smallBoxSelection',
            xtype: 'component',
            styleHtmlContent: true,
            padding: 10,
            autoScroll: true,
            bodyStyle: {
                border: 'none'
            },
            tpl: this.getSmallBoxTemplate(),
            data: data
        };
    },
    createToolBar: function () {
        return {
            xtype: 'toolbar',
            itemId: 'defaultToolbar',
            cls: 'admin-white-toolbar',
            items: [
                {
                    xtype: 'tbtext',
                    itemId: 'selectionTxt',
                    text: 'Stub text'
                }
            ]
        };
    },
    setDataCallback: function (data) {
    },
    resolveActiveItem: function (data) {
        var activeItem;
        if (Ext.isEmpty(this.data)) {
            activeItem = 'noSelection';
        } else if (Ext.isObject(this.data) || this.data.length === 1) {
            activeItem = 'singleSelection';
        } else if (this.data.length > 1 && this.data.length <= 10) {
            activeItem = 'largeBoxSelection';
        } else {
            activeItem = 'smallBoxSelection';
        }
        return activeItem;
    },
    resolveActiveData: function (data) {
        var activeData;
        if (Ext.isArray(data) && data.length === 1) {
            activeData = data[0];
        } else {
            activeData = data;
        }
        return activeData;
    },
    updateActiveItem: function (data, item) {
        item = item || this.getLayout().getActiveItem();
        if ('singleSelection' === item.itemId) {
            var previewHeader = item.down('#previewHeader');
            previewHeader.update(data);
            var previewPhoto = item.down('#previewPhoto');
            previewPhoto.update(data);
            this.changeTab('traffic');
        } else if ('largeBoxSelection' === item.itemId || 'smallBoxSelection' === item.itemId) {
            item.update(data);
        }
    },
    setData: function (data) {
        this.data = data;
        var toActivate = this.resolveActiveItem(data);
        var active = this.getLayout().getActiveItem();
        if (active.itemId !== toActivate) {
            active = this.getLayout().setActiveItem(toActivate);
        }
        if (active) {
            var activeData = this.resolveActiveData(data);
            this.updateActiveItem(activeData, active);
        }
        this.setDataCallback(data);
    },
    getData: function () {
        return this.data;
    },
    getTab: function (name) {
        var tabs = this.singleSelection.tabs;
        for (var tab in tabs) {
            if (tabs[tab].name === name) {
                return tabs[tab];
            }
        }
        return null;
    },
    changeTab: function (selectedTab) {
        var currentTab = this.getTab(selectedTab);
        if (currentTab) {
            var target = this.down('#center');
            target.remove(target.child());
            if (currentTab.items) {
                target.add(currentTab.items);
                if (currentTab.callback) {
                    currentTab.callback(target);
                }
            }
            var elements = Ext.dom.Query.select('*[data-tab=' + selectedTab + ']');
            for (var i = 0; i < elements.length; i++) {
                var children = elements[i].parentElement.children;
                for (var j = 0; j < children.length; j++) {
                    children[j].className = '';
                }
                elements[i].className = 'active';
            }
        }
    }
});
Ext.define('Admin.view.contentManager.DetailPanel', {
    extend: 'Admin.view.BaseDetailPanel',
    alias: 'widget.contentDetail',
    isLiveMode: false,
    keyField: 'path',
    initComponent: function () {
        var me = this;
        this.activeItem = this.resolveActiveItem(this.data);
        this.singleSelection.tabs = [
            {
                displayName: 'Analytics',
                name: 'analytics',
                items: [
                    {
                        xtype: 'iframe',
                        url: '/dev/detailpanel/analytics.html ',
                        iFrameCls: (me.isVertical ? 'admin-detail-vertical' : '')
                    }
                ]
            },
            {
                displayName: 'Sales',
                name: 'sales',
                items: [
                    {
                        xtype: 'component',
                        html: '<h1>Sales</h1>'
                    }
                ]
            },
            {
                displayName: 'Scorecard',
                name: 'scorecard',
                items: [
                    {
                        xtype: 'component',
                        html: '<h1>Scorecard</h1>'
                    }
                ]
            },
            {
                displayName: 'History',
                name: 'history',
                items: [
                    {
                        xtype: 'component',
                        html: '<h1>History</h1>'
                    }
                ]
            }
        ];
        this.actionButtonItems = [
            {
                text: ' New',
                icon: undefined,
                action: 'newContent',
                disableOnMultipleSelection: true
            },
            {
                text: 'Edit',
                icon: undefined,
                action: 'editContent',
                disableOnMultipleSelection: false
            },
            {
                text: 'Open',
                icon: undefined,
                action: 'viewContent',
                disableOnMultipleSelection: false
            },
            {
                text: 'Delete',
                icon: undefined,
                action: 'deleteContent'
            },
            {
                text: 'Duplicate',
                icon: undefined,
                action: 'duplicateContent'
            },
            {
                text: 'Move',
                icon: undefined,
                disabled: true,
                action: 'moveContent'
            }
        ];
        this.on('afterrender', function () {
            if (this.isLiveMode) {
                var livePreview = this.down('#livePreview');
                livePreview.load(this.getLiveUrl(this.data), false);
            }
        }, this);
        this.setDataCallback = function (data) {
            if (this.isLiveMode) {
                var livePreview = this.down('#livePreview');
                livePreview.load(this.getLiveUrl(data), false);
            }
        };
        this.items = [
            this.createNoSelection(),
            this.createSingleSelection(this.data),
            this.createLargeBoxSelection(this.data),
            this.createSmallBoxSelection(this.data),
            this.createLivePreview(this.data)
        ];
        this.callParent(arguments);
    },
    getLiveUrl: function (data) {
        if (data) {
            if (data.length > 0) {
                return data[0].data.displayName.match(/frogger/gi) !== null ? '/dev/live-edit/page/frogger.jsp'
                    : '/dev/live-edit/page/bootstrap.jsp';
            } else if (data.data) {
                return data.data.displayName.match(/frogger/gi) !== null ? '/dev/live-edit/page/frogger.jsp'
                    : '/dev/live-edit/page/bootstrap.jsp';
            }
        }
        return '/dev/live-edit/page/bootstrap.jsp';
    },
    createToolBar: function () {
        var me = this;
        return Ext.createByAlias('widget.contentDetailToolbar', {
            isLiveMode: me.isLiveMode
        });
    },
    createLivePreview: function (data) {
        var me = this;
        return {
            itemId: 'livePreview',
            xtype: 'contentLive',
            actionButton: (me.isFullPage ? undefined : me.getActionButton())
        };
    },
    resolveActiveItem: function (data) {
        var activeItem;
        if (Ext.isEmpty(this.data)) {
            activeItem = 'noSelection';
        } else if (Ext.isObject(this.data) || this.data.length === 1) {
            if (this.isLiveMode) {
                activeItem = 'livePreview';
            } else {
                activeItem = 'singleSelection';
            }
        } else if (this.data.length > 1 && this.data.length <= 10) {
            activeItem = 'largeBoxSelection';
        } else {
            activeItem = 'smallBoxSelection';
        }
        return activeItem;
    },
    toggleLive: function () {
        this.isLiveMode = !this.isLiveMode;
        this.setData(this.data, false);
    }
});
Ext.define('Admin.view.BaseDialogWindow', {
    extend: 'Ext.container.Container',
    alias: 'widget.baseDialogWindow',
    border: false,
    floating: true,
    shadow: false,
    width: 500,
    modal: true,
    autoHeight: true,
    maxHeight: 600,
    cls: 'admin-window',
    closeAction: 'hide',
    padding: 20,
    data: undefined,
    modelData: undefined,
    dialogTitle: 'Base dialog',
    dialogSubTitle: '',
    dialogInfoTpl: '<div>' + '<div class="admin-user-info clearfix">' + '<div class="admin-user-photo west admin-left">' +
                   '<div class="photo-placeholder">' + '<img src="{[values.image_url]}?size=100" alt="{name}"/>' + '</div>' + '</div>' +
                   '<div class="admin-left">' + '<h2>{displayName}</h2>({qualifiedName})<br/>' + '<a href="mailto:{email}:">{email}</a>' +
                   '</div>' + '</div>' + '</div>',
    buttonItems: [],
    buttonRow: function () {
        var i;
        if (arguments.length !== 0) {
            this.buttonItems = [];
        }
        for (i = 0; i < arguments.length; i++) {
            this.buttonItems.push(arguments[i]);
        }
        return {
            region: 'south',
            margin: '20 0 0 0',
            border: false,
            layout: {
                type: 'hbox',
                pack: 'end'
            },
            defaults: {
                xtype: 'button',
                margin: '0 0 0 10'
            },
            items: this.buttonItems
        };
    },
    header: function (title, subtitle) {
        return {
            region: 'north',
            xtype: 'component',
            tpl: '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>',
            data: {
                title: title,
                subtitle: subtitle
            },
            margin: '0 0 20 0'
        };
    },
    listeners: {
        show: function (cmp) {
            var form = cmp.down('form');
            if (form) {
                form.getForm().reset();
                form.doLayout();
                var firstField = form.down('field');
                if (firstField) {
                    firstField.focus();
                }
            }
        },
        resize: function (window) {
            if (this.getHeight() > this.maxHeight) {
                this.setHeight(this.maxHeight);
            }
            this.center();
        }
    },
    initComponent: function () {
        var me = this;
        if (!this.dockedItems) {
            this.dockedItems = [];
        }
        Ext.Array.insert(this.dockedItems, 0, [
            {
                xtype: 'toolbar',
                dock: 'right',
                autoHeight: true,
                items: [
                    {
                        itemId: 'closeButton',
                        scale: 'medium',
                        iconAlign: 'top',
                        text: 'Close',
                        action: 'close',
                        iconCls: 'icon-close',
                        listeners: {
                            click: function (btn, evt) {
                                me.close();
                            }
                        }
                    }
                ]
            }
        ]);
        if (!this.items) {
            this.items = [];
        }
        if (this.dialogTitle) {
            this.setDialogHeader(this.dialogTitle);
        }
        if (this.dialogSubTitle) {
            this.setDialogSubHeader(this.dialogSubTitle);
        }
        if (this.dialogInfoTpl) {
            this.setDialogInfo(this.dialogInfoTpl);
        }
        this.callParent(arguments);
    },
    filterItem: function (id) {
        return Ext.Array.filter(this.items, function (item) {
            return item.itemId !== id;
        });
    },
    setDialogHeader: function (title) {
        var headerItems = [];
        headerItems.push(this.createTitle(title));
        Ext.Array.each(this.buttons, function (b, i) {
            headerItems.push(this.buttons[i]);
        });
        headerItems.push(this.createCloseButton());
        this.items = this.filterItem('dialogTitle');
        Ext.Array.insert(this.items, 0, [
            {
                xtype: 'container',
                itemId: 'dialogTitle',
                cls: 'admin-window-header',
                padding: '5 0 5 5',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                defaults: {
                    margin: '0 5 0 0'
                },
                items: headerItems
            }
        ]);
        this.doLayout();
    },
    setDialogSubHeader: function (title) {
        var i = this.dialogTitle ? 1 : 0;
        this.items = this.filterItem('dialogSubTitle');
        Ext.Array.insert(this.items, i, [
            {
                xtype: 'component',
                itemId: 'dialogSubTitle',
                cls: 'admin-window-subheader',
                html: title
            }
        ]);
        this.doLayout();
    },
    setDialogInfo: function (tpl) {
        var i = 0;
        if (this.dialogTitle) {
            i++;
        }
        if (this.dialogSubTitle) {
            i++;
        }
        this.items = this.filterItem('dialogInfo');
        Ext.Array.insert(this.items, i, [
            {
                itemId: 'dialogInfo',
                cls: 'dialog-info',
                xtype: 'component',
                border: false,
                autoHeight: true,
                styleHtmlContent: true,
                tpl: new Ext.XTemplate(tpl)
            }
        ]);
    },
    setDialogInfoTpl: function (tpl) {
        var dialogInfo = this.down('#dialogInfo');
        if (dialogInfo) {
            dialogInfo.tpl = new Ext.XTemplate(tpl);
        } else {
            this.setDialogInfo(tpl);
        }
    },
    setDialogInfoData: function (model) {
        if (model) {
            this.data = model;
            this.modelData = model.data;
            var info = this.down('#dialogInfo');
            if (info) {
                info.update(this.modelData);
            }
        }
    },
    doShow: function (model) {
        this.setDialogInfoData(model);
        this.show();
    },
    doHide: function () {
        this.x = -this.width;
        this.hide();
    },
    close: function () {
        this.destroy();
    },
    createTitle: function (title) {
        return {
            xtype: 'component',
            flex: 1,
            cls: this.iconCls,
            autoEl: {
                tag: 'h1',
                html: title
            }
        };
    },
    createCloseButton: function () {
        var me = this;
        return {
            xtype: 'button',
            ui: 'grey',
            text: 'Close',
            handler: function (btn) {
                me.close();
            }
        };
    }
});
Ext.define('Admin.view.contentManager.NewContentWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.newContentWindow',
    requires: [
        'Admin.model.schemaManager.ContentTypeModel',
        'Admin.store.schemaManager.ContentTypeTreeStore'
    ],
    dialogTitle: undefined,
    dialogSubTitle: undefined,
    dialogInfoTpl: undefined,
    width: 800,
    height: 560,
    layout: 'border',
    defaultType: 'container',
    recentCount: 5,
    cookieKey: 'Admin.view.contentManager.NewContentWindow',
    cookieSeparator: '|',
    dataViewItemTemplate: '<tpl for=".">' + '<div class="admin-data-view-row">' + '<div class="admin-data-view-thumbnail">' +
                          '<img src="{iconUrl}?size=32"/>' + '</div>' + '<div class="admin-data-view-description">' +
                          '<h6>{displayName}</h6>' + '<p>{qualifiedName}</p>' + '</div>' + '<div class="x-clear"></div>' + '</div>' +
                          '</tpl>',
    initComponent: function () {
        var me = this;
        var baseDataViewConfig = {
            xtype: 'dataview',
            cls: 'admin-data-view',
            tpl: me.dataViewItemTemplate,
            itemSelector: '.admin-data-view-row',
            trackOver: true,
            overItemCls: 'x-item-over',
            listeners: {
                itemclick: function (dataview, record, item, index, e, opts) {
                    me.updateRecentCookies(record);
                    me.fireEvent('contentTypeSelected', me, record);
                }
            }
        };
        var recentContentTypesStore = Ext.create('Ext.data.Store', {
            model: 'Admin.model.schemaManager.ContentTypeModel'
        });
        this.updateRecentItems(recentContentTypesStore);
        var recentDataView = Ext.apply({
            store: recentContentTypesStore,
            emptyText: 'No recent content types'
        }, baseDataViewConfig);
        var recommendedContentTypesStore = Ext.create('Ext.data.Store', {
            model: 'Admin.model.schemaManager.ContentTypeModel',
            data: [
                {
                    iconUrl: '/enonic/admin/rest/schema/image/ContentType:system:structured',
                    name: 'Advanced Data',
                    qualifiedName: 'path/1'
                }
            ],
            autoLoad: true
        });
        this.updateRecommendedItems(recommendedContentTypesStore, recentContentTypesStore);
        var recommendedDataView = Ext.apply({
            store: recommendedContentTypesStore,
            emptyText: 'No recommendations yet'
        }, baseDataViewConfig);
        var allContentTypesStore = Ext.create('Admin.store.schemaManager.ContentTypeStore', {
            remoteSort: false,
            sorters: [
                {
                    property: 'name',
                    direction: 'ASC'
                }
            ]
        });
        var allDataView = Ext.apply({
            store: allContentTypesStore,
            emptyText: 'No matching content types'
        }, baseDataViewConfig);
        this.items = [
            me.header('Select Content Type', 'parent/of/new/content'),
            {
                region: 'west',
                width: 300,
                margin: '0 20 0 0',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                defaults: {
                    autoScroll: true,
                    xtype: 'panel',
                    border: false,
                    cls: 'admin-box'
                },
                items: [
                    {
                        title: 'Recommended',
                        autoHeight: true,
                        margin: '0 0 20 0',
                        items: [
                            recommendedDataView
                        ]
                    },
                    {
                        title: 'Recent',
                        flex: 1,
                        items: [
                            recentDataView
                        ]
                    }
                ]
            },
            {
                region: 'center',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [
                    {
                        xtype: 'textfield',
                        emptyText: 'Content Type Search',
                        margin: '0 0 20 0',
                        enableKeyEvents: true,
                        listeners: {
                            keyup: function (field, event, opts) {
                                var value = field.getValue();
                                if (!Ext.isEmpty(value, false)) {
                                    allContentTypesStore.clearFilter(true);
                                    allContentTypesStore.filter({
                                        property: "name",
                                        value: value,
                                        anyMatch: true
                                    });
                                } else {
                                    allContentTypesStore.clearFilter();
                                }
                            }
                        }
                    },
                    {
                        flex: 1,
                        overflowY: 'auto',
                        border: false,
                        items: [
                            allDataView
                        ]
                    }
                ]
            },
            me.buttonRow({
                xtype: 'button',
                text: 'Cancel',
                handler: function (btn, evt) {
                    me.close();
                }
            })
        ];
        this.callParent(arguments);
        this.addEvents('contentTypeSelected');
    },
    updateRecentCookies: function (contentType) {
        var cookies = Ext.util.Cookies.get(this.cookieKey);
        var recentArray = cookies ? cookies.split(this.cookieSeparator) : [];
        var recentItem = this.serializeContentType(contentType);
        if (recentArray.length === 0 || recentArray[0] !== recentItem) {
            recentArray.unshift(recentItem);
        }
        if (recentArray.length > this.recentCount) {
            recentArray = recentArray.slice(0, this.recentCount);
        }
        Ext.util.Cookies.set(this.cookieKey, recentArray.join(this.cookieSeparator));
    },
    updateRecentItems: function (recentStore) {
        recentStore.removeAll(true);
        var me = this;
        var cookies = Ext.util.Cookies.get(this.cookieKey);
        if (cookies) {
            var recentRecords = [];
            var recentArray = cookies.split(this.cookieSeparator);
            Ext.Array.each(recentArray, function (item, index, all) {
                recentRecords.push(me.parseContentType(item));
            });
            if (recentRecords.length > 0) {
                recentStore.loadData(recentRecords);
            }
        }
    },
    updateRecommendedItems: function (recommendedStore, recentStore) {
        recommendedStore.removeAll(true);
        var recommendedCount = 0;
        var recommendedRecord;
        var qualifiedNames = recentStore.collect('qualifiedName');
        var qualifiedRecords;
        for (var i = 0; i < qualifiedNames.length; i++) {
            qualifiedRecords = recentStore.queryBy((function (index) {
                return function (recentRecord, id) {
                    return recentRecord.get('qualifiedName') === qualifiedNames[index];
                };
            })(i));
            if (qualifiedRecords.getCount() > recommendedCount) {
                recommendedRecord = qualifiedRecords.get(0);
                recommendedCount = qualifiedRecords.getCount();
            }
        }
        if (recommendedRecord) {
            recommendedStore.loadRecords([
                recommendedRecord
            ]);
        }
    },
    serializeContentType: function (contentType) {
        return Ext.JSON.encode(contentType.data);
    },
    parseContentType: function (string) {
        var json = Ext.JSON.decode(string, true);
        return Ext.create('Admin.model.schemaManager.ContentTypeModel', json);
    }
});
Ext.define('Admin.controller.BaseController', {
    extend: 'Ext.app.Controller',
    stores: [],
    models: [],
    views: [],
    init: function () {
    },
    getCmsTabPanel: function () {
        return Ext.ComponentQuery.query('cmsTabPanel')[0];
    },
    getTopBar: function () {
        return Ext.ComponentQuery.query('topBar')[0];
    },
    getMainViewport: function () {
        var parent = window.parent || window;
    }
});
Ext.define('Admin.controller.Controller', {
    extend: 'Admin.controller.BaseController',
    stores: [],
    models: [],
    init: function () {
        var me = this;
        me.control({
            'browseToolbar *[action=newContent], contentManagerContextMenu *[action=newContent], contentDetail *[action=newContent]': {
                click: function (button, event) {
                    this.getNewContentWindow().doShow();
                }
            },
            'browseToolbar *[action=viewContent], contentManagerContextMenu *[action=viewContent], contentDetail *[action=viewContent]': {
                click: function (button, event) {
                    this.viewContent();
                }
            },
            'browseToolbar *[action=editContent], contentManagerContextMenu *[action=editContent], contentDetail *[action=editContent]': {
                click: function (button, event) {
                    this.editContent();
                }
            },
            'browseToolbar *[action=deleteContent], contentManagerContextMenu *[action=deleteContent], contentDetail *[action=deleteContent]': {
                click: function (button, event) {
                    this.deleteContent();
                }
            },
            'browseToolbar *[action=duplicateContent], contentManagerContextMenu *[action=duplicateContent], contentDetail *[action=duplicateContent]': {
                click: function (button, event) {
                    this.duplicateContent();
                }
            }
        });
        me.application.on({
        });
    },
    getNewContentWindow: function () {
        var win = Ext.ComponentQuery.query('newContentWindow')[0];
        if (!win) {
            win = Ext.create('widget.newContentWindow');
        }
        return win;
    },
    generateTabId: function (content, isEdit) {
        return 'tab-' + (isEdit ? 'edit' : 'preview') + '-content-' + content.get('path');
    },
    viewContent: function (contentModels, callback, contentOpenedFromLiveEdit) {
        var me = this;
        if (!contentModels) {
            var showPanel = this.getContentTreeGridPanel();
            contentModels = showPanel.getSelection();
        } else {
            contentModels = [].concat(contentModels);
        }
        var tabs = this.getCmsTabPanel();
        var i;
        if (tabs) {
            for (i = 0; i < contentModels.length; i += 1) {
                var activeTab = tabs.setActiveTab(me.generateTabId(contentModels[i], true));
                if (!activeTab) {
                    var tabItem = {
                        xtype: 'contentDetail',
                        id: me.generateTabId(contentModels[i], false),
                        isLiveMode: contentOpenedFromLiveEdit || me.getContentDetailPanel().isLiveMode,
                        data: contentModels[i],
                        title: contentModels[i].get('displayName'),
                        isFullPage: true
                    };
                    tabs.addTab(tabItem);
                }
            }
        }
    },
    editContent: function (contentModel, callback) {
        var me = this;
        if (!contentModel) {
            var showPanel = this.getContentTreeGridPanel();
            contentModel = showPanel.getSelection();
        } else {
            contentModel = [].concat(contentModel);
        }
        var tabs = this.getCmsTabPanel();
        var createContentTabFn = function (response) {
            if (Ext.isFunction(callback)) {
                callback();
            }
            return {
                xtype: 'contentLiveEditPanel',
                title: response.content.displayName,
                isLiveMode: me.getContentDetailPanel().isLiveMode,
                contentType: response.contentType,
                content: response.content,
                data: {
                }
            };
        };
        var openEditContentTabFn = function (selectedContent) {
            var requestConfig = {
                doTabRequest: function (handleRpcResponse) {
                    var getContentTypeResponse, getContentResponse;
                    var getContentTypeCommand = {
                        format: 'JSON',
                        contentType: selectedContent.get('type'),
                        mixinReferencesToFormItems: true
                    };
                    Admin.lib.RemoteService.contentType_get(getContentTypeCommand, function (rpcResponse) {
                        getContentTypeResponse = rpcResponse;
                        if (getContentTypeResponse && getContentTypeResponse.success && getContentResponse && getContentResponse.success) {
                            getContentTypeResponse.content = getContentResponse.content;
                            handleRpcResponse(getContentTypeResponse);
                        }
                    });
                    var getContentCommand = {
                        path: selectedContent.get('path')
                    };
                    Admin.lib.RemoteService.content_get(getContentCommand, function (rpcResponse) {
                        getContentResponse = rpcResponse;
                        if (getContentResponse && getContentResponse.success && getContentTypeResponse && getContentTypeResponse.success) {
                            getContentTypeResponse.content = getContentResponse.content;
                            handleRpcResponse(getContentTypeResponse);
                        }
                    });
                },
                createTabFromResponse: createContentTabFn
            };
            var tabItem = {
                id: me.generateTabId(contentModel[i], true),
                title: selectedContent.get('displayName'),
                data: selectedContent,
                closable: true,
                editing: true,
                layout: 'fit'
            };
            var index = tabs.items.indexOfKey(me.generateTabId(selectedContent, false));
            if (index >= 0) {
                tabs.remove(index);
            }
            tabs.addTab(tabItem, index >= 0 ? index : undefined, requestConfig);
        };
        var i;
        if (tabs) {
            for (i = 0; i < contentModel.length; i += 1) {
                var data = contentModel[i];
                openEditContentTabFn(data);
            }
        }
    },
    createContent: function (type, qualifiedContentType, contentTypeName) {
        var tabs = this.getCmsTabPanel();
        if (tabs) {
            var tab;
            var treeGridSelection = this.getContentTreeGridPanel().getSelection();
            switch (type) {
            case 'contentType':
                Admin.lib.RemoteService.contentType_get({
                    format: 'JSON',
                    contentType: qualifiedContentType,
                    mixinReferencesToFormItems: true
                }, function (rpcResponse) {
                    if (rpcResponse.success) {
                        var createContentTabFn = function (response) {
                            return {
                                xtype: 'contentLiveEditPanel',
                                title: '[New ' + response.contentType.displayName + ']',
                                content: {
                                    iconUrl: response.iconUrl
                                },
                                contentType: response.contentType,
                                contentParent: treeGridSelection.length > 0 ? treeGridSelection[0].data : undefined,
                                data: {
                                }
                            };
                        };
                        var requestConfig = {
                            doTabRequest: function (handleRpcResponse) {
                                handleRpcResponse(rpcResponse);
                            },
                            createTabFromResponse: createContentTabFn
                        };
                        var tabItem = {
                            id: 'tab-new-content-' + qualifiedContentType,
                            data: {
                                name: contentTypeName,
                                content: {
                                    iconUrl: rpcResponse.iconUrl
                                }
                            },
                            title: '[New ' + contentTypeName + ']',
                            closable: true,
                            editing: true,
                            layout: 'fit'
                        };
                        tabs.addTab(tabItem, undefined, requestConfig);
                    }
                });
                break;
            case 'site':
                tab = {
                    xtype: 'panel',
                    html: 'New site wizard here',
                    title: 'New Site'
                };
                tabs.addTab(tab);
                break;
            }
        }
    },
    deleteContent: function (content) {
        if (!content) {
            var showPanel = this.getContentTreeGridPanel();
            content = showPanel.getSelection();
        } else {
            content = [].concat(content);
        }
        if (content && content.length > 0) {
            this.getDeleteContentWindow().doShow(content);
        }
    },
    duplicateContent: function (content) {
        if (!content) {
            var showPanel = this.getContentTreeGridPanel();
            content = showPanel.getSelection();
        } else {
            content = [].concat(content);
        }
        var selection = content[0];
        if (selection) {
            Admin.MessageBus.showFeedback({
                title: selection.get('name') + ' duplicated into /path/to/content-copy',
                message: 'Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.',
                opts: {
                }
            });
        }
    },
    updateDetailPanel: function (selected) {
        var selection = this.getContentTreeGridPanel().getSelection();
        this.getContentDetailPanel().setData(selection);
    },
    updateToolbarButtons: function (selected) {
        var toolbar = this.getContentBrowseToolbar();
        var contextMenu = this.getContentManagerContextMenu();
        var detailPanel = this.getContentDetailPanel();
        var newContentButton = toolbar.down('*[action=newContent]');
        newContentButton.setDisabled(Ext.isEmpty(selected) || selected.length !== 1 || (!selected[0].get('allowsChildren')));
        var deleteContentButton = toolbar.down('*[action=deleteContent]');
        var disabled = false;
        var i;
        for (i = 0; i < selected.length; i++) {
            var deletable = selected[i].get('deletable');
            if (!deletable) {
                disabled = true;
                break;
            }
        }
        deleteContentButton.setDisabled(disabled);
        deleteContentButton = contextMenu.down('*[action=deleteContent]');
        deleteContentButton.setDisabled(disabled);
        deleteContentButton = detailPanel.down('*[action=deleteContent]');
        if (deleteContentButton) {
            deleteContentButton.setDisabled(disabled);
        }
    },
    loadContentAndFacets: function (values) {
        var me = this, filter = this.getContentFilter(), params = this.createLoadContentParams(values || filter.getValues());
        Admin.lib.RemoteService.content_find(params, function (response) {
            if (response && response.success) {
                me.getContentFilter().updateFacets(response.facets);
                var ids = Ext.Array.pluck(response.contents,
                    'id'), treeGridPanel = me.getContentTreeGridPanel(), filterDirty = filter.isDirty();
                treeGridPanel.setResultCountVisible(filterDirty);
                if (!filterDirty) {
                    treeGridPanel.setRemoteSearchParams({
                    });
                    treeGridPanel.refresh();
                } else {
                    if (ids.length > 0) {
                        treeGridPanel.setRemoteSearchParams({
                            contentIds: ids
                        });
                        treeGridPanel.refresh();
                    } else {
                        treeGridPanel.removeAll();
                        treeGridPanel.updateResultCount(0);
                    }
                }
            }
        });
    },
    getDimensionsForDevice: function (device) {
        var dimensions;
        switch (device) {
        case 'DESKTOP':
            dimensions = {
                width: '100%',
                height: '100%'
            };
            break;
        case 'IPHONE_5_VERTICAL':
            dimensions = {
                width: '320px',
                height: '568px'
            };
            break;
        case 'IPHONE_5_HORIZONTAL':
            dimensions = {
                width: '568px',
                height: '320px'
            };
            break;
        case 'IPAD_3_VERTICAL':
            dimensions = {
                width: '768px',
                height: '1024px'
            };
            break;
        case 'IPAD_3_HORIZONTAL':
            dimensions = {
                width: '1024px',
                height: '768px'
            };
            break;
        default:
            dimensions = {
                width: '100%',
                height: '100%'
            };
        }
        return dimensions;
    },
    createLoadContentParams: function (values) {
        var now = new Date();
        var oneDayAgo = new Date();
        var oneWeekAgo = new Date();
        var oneHourAgo = new Date();
        oneDayAgo.setDate(now.getDate() - 1);
        oneWeekAgo.setDate(now.getDate() - 7);
        Admin.lib.DateHelper.addHours(oneHourAgo, -1);
        var facets = {
            "space": {
                "terms": {
                    "field": "space",
                    "size": 10,
                    "all_terms": true,
                    "order": "term"
                }
            },
            "contentType": {
                "terms": {
                    "field": "contentType",
                    "size": 10,
                    "all_terms": true,
                    "order": "term"
                }
            },
            "< 1 day": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneDayAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            },
            "< 1 hour": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneHourAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            },
            "< 1 week": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneWeekAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            }
        };
        var ranges = [];
        if (values.ranges) {
            for (var i = 0; i < values.ranges.length; i++) {
                var lower;
                switch (values.ranges[i]) {
                case '< 1 day':
                    lower = oneDayAgo;
                    break;
                case '< 1 hour':
                    lower = oneHourAgo;
                    break;
                case '< 1 week':
                    lower = oneWeekAgo;
                    break;
                default:
                    lower = null;
                    break;
                }
                ranges.push({
                    lower: lower,
                    upper: null
                });
            }
        }
        return {
            fulltext: values.query || '',
            contentTypes: values.contentType || [],
            spaces: values.space || [],
            ranges: ranges || [],
            facets: facets || {
            },
            include: true
        };
    },
    getContentFilter: function () {
        return Ext.ComponentQuery.query('contentFilter')[0];
    },
    getContentShowPanel: function () {
        return Ext.ComponentQuery.query('contentShow')[0];
    },
    getContentBrowseToolbar: function () {
        return this.getContentShowPanel().down('browseToolbar');
    },
    getContentManagerContextMenu: function () {
        var menu = Ext.ComponentQuery.query('contentManagerContextMenu')[0];
        if (!menu) {
            menu = Ext.create('widget.contentManagerContextMenu');
        }
        return menu;
    },
    getContentTreeGridPanel: function () {
        return this.getContentShowPanel().down('contentTreeGridPanel');
    },
    getContentDetailPanel: function () {
        var contentDetail = Ext.ComponentQuery.query('contentDetail');
        var vertical = contentDetail[0].isVisible();
        return Ext.ComponentQuery.query('contentDetail')[vertical ? 0 : 1];
    },
    getPersistentGridSelectionPlugin: function () {
        return this.getContentGridPanel().getPlugin('persistentGridSelection');
    },
    getDeleteContentWindow: function () {
        var win = Ext.ComponentQuery.query('deleteContentWindow')[0];
        if (!win) {
            win = Ext.create('widget.deleteContentWindow');
        }
        return win;
    },
    getLiveEditTestWindow: function () {
        var win = Ext.ComponentQuery.query('liveEditTestWindow')[0];
        if (!win) {
            win = Ext.create('widget.liveEditTestWindow');
        }
        return win;
    }
});
Ext.define('Admin.controller.TopBarController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    init: function () {
        this.application.on({
            loadApplication: {
                fn: this.loadApplication,
                scope: this
            }
        });
    },
    loadDefaultApplication: function () {
        var defaultItem = {
            id: 'app-1000',
            name: 'Blank',
            description: 'Ei veri vituperata cum, no habeo dicta diceret vis. Eirmod audiam efficiendi quo ne, et duo decore epicurei. In veri liber movet usu, posse lorem erroribus at usu. Mea in vidisse mentitum repudiare, invidunt sensibus adipiscing ea nec. Illud munere te sit, ad singulis definitionem his.',
            appUrl: 'blank.html'
        };
        this.loadApplication(defaultItem);
    },
    loadApplication: function (appData, urlHash) {
        var me = this, parent = this.getParentFrame(), iFrames = parent.Ext.DomQuery.select('iframe');
        var iFrameExist = false;
        Ext.each(iFrames, function (iframe, index, allIFrames) {
            if (iframe.id === 'iframe-' + appData.id) {
                iFrameExist = true;
                iframe.style.display = 'block';
            } else {
                iframe.style.display = 'none';
            }
        });
        if (!iFrameExist) {
            me.appendIframe(parent, appData, urlHash);
            me.showLoadMask();
        }
        me.setStartButton(appData);
    },
    getParentFrame: function () {
        return window.parent.parent || window.parent;
    },
    appendIframe: function (parent, appData, urlHash) {
        var url = appData.appUrl + '?appId=' + appData.id;
        if (urlHash) {
            url += urlHash;
        }
        var iFrameSpec = parent.Ext.core.DomHelper.append('admin-application-frames', {
            tag: 'iframe',
            src: url,
            id: 'iframe-' + appData.id,
            style: 'width: 100%; height: 100%; border: 0'
        }, false);
    },
    showLoadMask: function () {
    },
    setStartButton: function (selectedMenuItem) {
        var topBar = this.getTopBar();
        if (topBar) {
            var startButton = this.getStartButton();
            startButton.setText(selectedMenuItem.title);
            startButton.setIconCls(selectedMenuItem.iconCls);
        }
    }
});
Ext.define('Admin.controller.GridPanelController', {
    extend: 'Admin.controller.Controller',
    stores: [
        'Admin.store.contentManager.ContentStore',
        'Admin.store.contentManager.ContentTreeStore'
    ],
    models: [
        'Admin.model.contentManager.ContentModel'
    ],
    init: function () {
        this.control({
            'contentTreeGridPanel treepanel, grid': {
                selectionchange: function (panel, selected, opts) {
                    this.updateDetailPanel(selected);
                    this.updateToolbarButtons(selected);
                },
                itemcontextmenu: this.popupMenu,
                itemdblclick: function (grid, record, el, index, event, opts) {
                    this.editContent(record);
                }
            },
            'contentShow': {
                afterrender: function () {
                    if (document.location.hash.indexOf('/cm/open/') > -1) {
                        Admin.MessageBus.liveEditOpenContent();
                    }
                }
            }
        });
    },
    popupMenu: function (view, rec, node, index, e) {
        e.stopEvent();
        this.getContentManagerContextMenu().showAt(e.getXY());
        return false;
    }
});
Ext.define('Admin.controller.DetailPanelController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    init: function () {
        this.control({
            'contentDetail': {
                deselect: this.deselectRecord,
                clearselection: this.clearSelection
            }
        });
    },
    deselectRecord: function (key) {
        this.getContentTreeGridPanel().deselect(key);
    },
    clearSelection: function () {
        this.getContentTreeGridPanel().deselect(-1);
    }
});
Ext.define('Admin.controller.FilterPanelController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    init: function () {
        this.control({
            'contentFilter': {
                afterrender: function (cmp) {
                    this.loadContentAndFacets({
                    });
                },
                search: this.doSearch,
                reset: this.doReset
            }
        });
    },
    doSearch: function (values) {
        this.getCmsTabPanel().setActiveTab(0);
        this.loadContentAndFacets(values);
    },
    doReset: function (dirty) {
        if (!dirty) {
            return false;
        }
        this.loadContentAndFacets({
        });
    }
});
Ext.define('Admin.controller.BrowseToolbarController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    init: function () {
        this.control({
            'browseToolbar *[action=showToolbarMenu]': {
                click: function (button, event) {
                    this.showToolbarMenu(button, event);
                }
            },
            'browseToolbar *[action=toggleLive]': {
                change: function (slider, state) {
                    this.getContentDetailPanel().toggleLive();
                }
            },
            'contentManagerToolbarMenu *[action=moveDetailPanel]': {
                click: function (button, event) {
                    this.moveDetailPanel(button, event);
                }
            }
        });
    },
    showToolbarMenu: function (button, event) {
        event.stopEvent();
        var menu = this.getContentManagerToolbarMenu();
        var contentDetail = Ext.ComponentQuery.query('contentDetail');
        var vertical = contentDetail[0].isVisible();
        menu.items.items[0].setText('Details Pane ' + (vertical ? 'Right' : 'Bottom'));
        menu.showAt(event.getX(), button.getEl().getY() + button.getEl().getHeight());
    },
    moveDetailPanel: function (button, event) {
        var contentDetail = Ext.ComponentQuery.query('contentDetail');
        var vertical = contentDetail[0].isVisible();
        var toHide = contentDetail[vertical ? 0 : 1];
        var toShow = contentDetail[vertical ? 1 : 0];
        toHide.setVisible(false);
        toShow.setVisible(true);
        if (toShow.isLiveMode != toHide.isLiveMode) {
            toShow.toggleLive();
        }
        var showPanel = this.getContentTreeGridPanel();
        var selected = showPanel.getSelection();
        this.updateDetailPanel(selected);
        this.updateToolbarButtons(selected);
    },
    getContentManagerToolbarMenu: function () {
        var menu = Ext.ComponentQuery.query('contentManagerToolbarMenu')[0];
        if (!menu) {
            menu = Ext.create('widget.contentManagerToolbarMenu');
        }
        return menu;
    }
});
Ext.define('Admin.controller.DetailToolbarController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    init: function () {
        this.control({
            'contentDetailToolbar *[action=newContent]': {
                click: function (el, e) {
                    this.getNewContentWindow().doShow();
                }
            },
            'contentDetailToolbar *[action=editContent]': {
                click: function (el, e) {
                    this.editContent();
                }
            },
            'contentDetailToolbar *[action=deleteContent]': {
                click: function (el, e) {
                    this.deleteContent();
                }
            },
            'contentDetailToolbar *[action=duplicateContent]': {
                click: function (el, e) {
                    this.duplicateContent();
                }
            },
            'contentDetailToolbar *[action=moveContent]': {
                click: function (el, e) {
                }
            },
            'contentDetailToolbar *[action=relations]': {
                click: function (el, e) {
                }
            },
            'contentDetailToolbar *[action=closeContent]': {
                click: function (el, e) {
                    this.getCmsTabPanel().getActiveTab().close();
                }
            },
            'contentDetailToolbar *[action=toggleLive]': {
                change: function (slider, state) {
                    slider.up().down('#deviceCycle').setDisabled(!state);
                }
            },
            'contentDetailToolbar #deviceCycle': {
                change: function (cycle, item) {
                    this.application.fireEvent('toggleDeviceContext', item.device);
                }
            }
        });
    }
});
Ext.define('Admin.controller.ContentController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    views: [],
    init: function () {
    },
    remoteCreateOrUpdateContent: function (contentParams, callback) {
        Admin.lib.RemoteService.content_createOrUpdate(contentParams, function (r) {
            if (r && r.success) {
                callback(r.created, r.updated, r.contentPath, r.contentId);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Internal error occured.");
            }
        });
    },
    remoteDeleteContent: function (contents, callback) {
        var me = this;
        var contentPaths = Ext.Array.map([].concat(contents), function (item) {
            return item.get('path');
        });
        Admin.lib.RemoteService.content_delete({
            "contentPaths": contentPaths
        }, function (r) {
            if (r) {
                callback.call(me, r.success, r.failures);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Internal error occured.");
            }
        });
    }
});
Ext.define('Admin.controller.ContentWizardController', {
    extend: 'Admin.controller.ContentController',
    stores: [],
    models: [],
    init: function () {
        var me = this;
        me.control({
            'contentLiveEditPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            'contentLiveEditPanel *[action=saveContent]': {
                click: function (el, e) {
                    me.saveContent(this.getContentWizardPanel(), false);
                }
            },
            'contentLiveEditPanel *[action=previewContent]': {
                click: function (el, e) {
                    me.previewContent(this.getContentWizardPanel());
                }
            },
            'contentWizardPanel *[action=publishContent]': {
                click: function (el, e) {
                    me.publishContent(this.getContentWizardPanel(), true);
                }
            },
            'contentWizardToolbar *[action=publishContent]': {
                click: function (el, e) {
                    me.publishContent(this.getContentWizardPanel(), false);
                }
            },
            'contentWizardPanel': {
                finished: function (wizard, data) {
                    me.saveContent(this.getContentWizardPanel(), true);
                }
            },
            'contentWizardToolbar *[action=duplicateContent]': {
                click: function (el, e) {
                    me.duplicateContent(this.getContentWizardTab().data);
                }
            },
            'contentWizardToolbar *[action=deleteContent]': {
                click: function (el, e) {
                    this.deleteContent(this.getContentWizardTab().data);
                }
            },
            'contentWizardToolbar toggleslide': {
                change: this.toggleLiveWizard
            },
            'contentWizardPanel wizardHeader': {
                displaynamechange: this.onDisplayNameChanged,
                displaynameoverride: this.onDisplayNameOverriden,
                nameoverride: this.onNameOverridden,
                scope: this
            },
            'contentWizardPanel *[displayNameSource]': {
                change: this.onDisplayNameSourceChanged
            }
        });
        me.application.on({
        });
    },
    onDisplayNameChanged: function (newName, oldName) {
        this.getTopBar().setTitleButtonText(newName);
    },
    onDisplayNameOverriden: function (overriden) {
        var wizard = this.getContentWizardPanel();
        wizard.evaluateDisplayName = wizard.isNewContent() && !overriden;
    },
    onNameOverridden: function (overridden) {
        var wizard = this.getContentWizardPanel();
        wizard.contentNameOverridden = overridden;
    },
    onDisplayNameSourceChanged: function (field, event, opts) {
        var wizard = this.getContentWizardPanel();
        var evaluateFn = wizard.data && wizard.contentType && wizard.contentType.contentDisplayNameScript;
        if (wizard.evaluateDisplayName && !Ext.isEmpty(evaluateFn)) {
            var rawData = wizard.getData().contentData;
            var contentData = {
            };
            var key;
            for (key in rawData) {
                if (rawData.hasOwnProperty(key)) {
                    contentData[key.replace(/\[0\]/g, '')] = rawData[key];
                }
            }
            var displayName = 'temp';
            wizard.getWizardHeader().setDisplayName(displayName);
        }
    },
    toggleLiveWizard: function (enabled) {
        this.getContentLiveEditPanel().toggleLive();
    },
    closeWizard: function (el, e) {
        var tab = this.getContentWizardTab();
        var contentWizard = this.getContentWizardPanel();
        if (contentWizard.isWizardDirty) {
            Ext.Msg.confirm('Close wizard', 'There are unsaved changes, do you want to close it anyway ?', function (answer) {
                if ('yes' === answer) {
                    tab.close();
                }
            });
        } else {
            tab.close();
        }
    },
    saveContent: function (contentWizard, closeWizard) {
        var me = this;
        var contentType = contentWizard.contentType;
        var content = contentWizard.content;
        var contentParent = contentWizard.contentParent;
        var contentWizardData = contentWizard.getData();
        var contentData = contentWizardData.contentData;
        var displayName = contentWizardData.displayName;
        var isNewContent = !content.path;
        var contentName = !isNewContent || contentWizard.contentNameOverridden ? contentWizardData.name : null;
        var contentParams = {
            contentData: contentData,
            qualifiedContentTypeName: contentType.qualifiedName,
            contentId: isNewContent ? null : content.id,
            contentPath: isNewContent ? null : content.path,
            contentName: contentName,
            parentContentPath: isNewContent ? contentParent.path : null,
            displayName: displayName
        };
        var onUpdateContentSuccess = function (created, updated, contentPath, contentId) {
            var lastSlashIndex, contentName;
            if (contentPath) {
                if (content.path !== contentPath) {
                    lastSlashIndex = contentPath.lastIndexOf('/');
                    if (lastSlashIndex >= 0) {
                        contentName = contentPath.substring(lastSlashIndex + 1);
                        contentWizard.getWizardHeader().setName(contentName);
                    }
                }
                content.path = contentPath;
            }
            if (contentId) {
                content.id = contentId;
            }
            if (created || updated) {
                if (closeWizard) {
                    me.getContentWizardTab().close();
                }
                var displayName = contentParams.displayName || displayName;
                Admin.MessageBus.showGeneral(displayName, function () {
                    alert('publish link callback');
                }, function () {
                    alert('close link callback');
                });
                if (Ext.isFunction(contentWizard.washDirtyForms)) {
                    contentWizard.washDirtyForms();
                }
                me.loadContentAndFacets();
            }
        };
        this.remoteCreateOrUpdateContent(contentParams, onUpdateContentSuccess);
    },
    previewContent: function (panel) {
        var previewUrl = '/dev/live-edit/page/frogger.jsp';
        window.open(Admin.lib.UriHelper.getAbsoluteUri(previewUrl));
    },
    publishContent: function (contentWizard, closeWizard) {
        var me = this;
        var displayName = contentWizard.content.displayName;
        if (closeWizard) {
            me.getContentWizardTab().close();
        }
        Admin.MessageBus.showPublish(displayName, function () {
            alert('result link callback');
        }, function () {
            alert('publish link callback');
        });
    },
    getContentWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },
    getContentWizardPanel: function () {
        return this.getContentWizardTab().down('contentWizardPanel');
    },
    getContentLiveEditPanel: function () {
        return this.getContentWizardTab().down('contentLiveEditPanel');
    }
});
Ext.define('Admin.controller.ContentPreviewController', {
    extend: 'Admin.controller.ContentController',
    stores: [],
    models: [],
    views: [],
    init: function () {
        this.control({
            'contentDetailToolbar *[action=duplicateContent]': {
                click: function (el, e) {
                    this.duplicateContent(this.getContentPreviewPanel().data);
                }
            },
            'contentDetailToolbar *[action=deleteContent]': {
                click: function (el, e) {
                    this.deleteContent(this.getContentPreviewPanel().data);
                }
            },
            'contentDetailToolbar toggleslide': {
                change: this.toggleLiveDetail
            }
        });
        this.application.on({
            toggleDeviceContext: function (device) {
                var previewPanel = this.getContentPreviewPanel().down('#livePreview');
                previewPanel.resizeIframe(this.getDimensionsForDevice(device));
            },
            scope: this
        });
    },
    toggleLiveDetail: function (el, e) {
        this.getContentPreviewPanel().toggleLive();
    },
    getContentPreviewTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },
    getContentPreviewPanel: function () {
        return this.getContentPreviewTab();
    }
});
Ext.define('Admin.controller.DialogWindowController', {
    extend: 'Admin.controller.ContentController',
    stores: [],
    models: [],
    init: function () {
        this.control({
            'deleteContentWindow *[action=deleteContent]': {
                click: this.doDelete
            },
            'newContentWindow': {
                contentTypeSelected: function (window, contentType) {
                    if (window) {
                        window.close();
                    }
                    if (contentType) {
                        this.createContent('contentType', contentType.get('qualifiedName'), contentType.get('name'));
                    }
                }
            }
        });
        this.application.on({
        });
    },
    doDelete: function (el, e) {
        var win = this.getDeleteContentWindow();
        console.log(win);
        var me = this;
        var content = win.data;
        var onContentDeleted = function (success, details) {
            win.close();
            if (success) {
                Admin.MessageBus.showFeedback({
                    title: 'Content was deleted',
                    message: Ext.isArray(content) && content.length > 1 ? content.length + ' contents were deleted'
                        : '1 content was deleted',
                    opts: {
                    }
                });
            } else {
                var message = '';
                var i;
                for (i = 0; i < details.length; i++) {
                    message += details[0].reason + "\n";
                }
                Admin.MessageBus.showFeedback({
                    title: 'Content was not deleted',
                    message: message,
                    opts: {
                    }
                });
            }
            me.getContentTreeGridPanel().refresh();
        };
        this.remoteDeleteContent(content, onContentDeleted);
    }
});
Ext.application({
    name: 'CM',
    appFolder: 'resources/app',
    controllers: [
        'Admin.controller.BaseController',
        'Admin.controller.Controller',
        'Admin.controller.TopBarController',
        'Admin.controller.GridPanelController',
        'Admin.controller.DetailPanelController',
        'Admin.controller.FilterPanelController',
        'Admin.controller.BrowseToolbarController',
        'Admin.controller.DetailToolbarController',
        'Admin.controller.ContentWizardController',
        'Admin.controller.ContentPreviewController',
        'Admin.controller.DialogWindowController'
    ],
    launch: function () {
        Ext.create('Ext.container.Viewport', {
            layout: 'fit',
            cls: 'admin-viewport',
            items: [
                {
                    xtype: 'cmsTabPanel',
                    appName: 'Content Manager',
                    appIconCls: 'icon-metro-content-manager-24',
                    items: [
                        {
                            id: 'tab-browse',
                            title: 'Browse',
                            closable: false,
                            xtype: 'panel',
                            layout: 'border',
                            tabConfig: {
                                hidden: true
                            },
                            items: [
                                {
                                    region: 'west',
                                    xtype: 'contentFilter',
                                    width: 200
                                },
                                {
                                    region: 'center',
                                    xtype: 'contentShow'
                                }
                            ]
                        }
                    ]
                }
            ]
        });
    }
});
//@ sourceMappingURL=all.js.map
