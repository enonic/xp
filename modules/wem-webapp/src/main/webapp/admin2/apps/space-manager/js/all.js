Ext.define('Admin.lib.JsonRpcProvider', {
    alias: 'direct.jsonrpcprovider',
    extend: 'Ext.direct.RemotingProvider',
    initAPI: function () {
        var methods = this.methods;
        var namespace = this.namespace;
        for(var i = 0; i < methods.length; i++) {
            var def = {
                name: methods[i],
                len: 1
            };
            var method = new Ext.direct.RemotingMethod(def);
            namespace[method.name] = this.createHandler(null, method);
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
        if(error) {
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
            ]
        };
        this.provider = Ext.Direct.addProvider(config);
        Ext.direct.RemotingProvider.enableBuffer = 20;
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
        if(handler) {
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
var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var APP;
(function (APP) {
    (function (event) {
        var DELETED = 'deleted';
        var DeletedEvent = (function (_super) {
            __extends(DeletedEvent, _super);
            function DeletedEvent() {
                        _super.call(this, DELETED);
            }
            return DeletedEvent;
        })(API.event.Event);
        event.DeletedEvent = DeletedEvent;        
        function onDeleted(handler) {
            API.event.onEvent(DELETED, handler);
        }
        event.onDeleted = onDeleted;
    })(APP.event || (APP.event = {}));
    var event = APP.event;
})(APP || (APP = {}));
var APP;
(function (APP) {
    (function (event) {
        var DELETE_PROMPT = 'deletePrompt';
        var DeletePromptEvent = (function (_super) {
            __extends(DeletePromptEvent, _super);
            function DeletePromptEvent(model) {
                this.model = model;
                        _super.call(this, DELETE_PROMPT);
            }
            DeletePromptEvent.prototype.getModel = function () {
                return this.model;
            };
            return DeletePromptEvent;
        })(API.event.Event);
        event.DeletePromptEvent = DeletePromptEvent;        
        function onDeletePrompt(handler) {
            API.event.onEvent(DELETE_PROMPT, handler);
        }
        event.onDeletePrompt = onDeletePrompt;
    })(APP.event || (APP.event = {}));
    var event = APP.event;
})(APP || (APP = {}));
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
                if(isCheckboxColumnIsClicked) {
                    var isShiftKeyPressed = event.shiftKey === true;
                    var isCtrlKeyPressed = event.ctrlKey === true;
                    if(isShiftKeyPressed || isCtrlKeyPressed) {
                        return;
                    }
                    var isChecked = this.selected[record.get(this.keyField)];
                    if(!isChecked) {
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
            if(pagingToolbar !== null) {
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
        if(this.panel instanceof Ext.tree.Panel) {
            storeRecord = this.panel.getRootNode().findChild(this.keyField, key);
        } else if(this.panel instanceof Ext.grid.Panel) {
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
        if(this.panel instanceof Ext.tree.Panel) {
            var rootNode = this.panel.getRootNode(), node;
            for(var selectedItem in this.selected) {
                if(this.selected.hasOwnProperty(selectedItem) && this.selected[selectedItem]) {
                    node = rootNode.findChild(this.keyField, selectedItem, true);
                    if(node) {
                        sm.select(node, true);
                    }
                }
            }
        } else if(this.panel instanceof Ext.grid.Panel) {
            var store = this.panel.getStore(), record;
            for(var selectedItem in this.selected) {
                if(this.selected.hasOwnProperty(selectedItem) && this.selected[selectedItem]) {
                    record = store.findRecord(this.keyField, selectedItem);
                    if(record) {
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
        if(!this.ignoreSelectionChanges) {
            this.selections = [];
            this.selected = {
            };
        }
    },
    onRowSelect: function (sm, rec, i, o) {
        if(!this.ignoreSelectionChanges) {
            if(!this.selected[rec.get(this.keyField)]) {
                this.selections.push(rec);
                this.selected[rec.get(this.keyField)] = true;
            }
        }
    },
    onHeaderClick: function (headerCt, header, e) {
        if(header.isCheckerHd) {
            e.stopEvent();
            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
            if(isChecked) {
                this.clearSelection();
            } else {
                this.panel.selModel.selectAll();
            }
        }
        return false;
    },
    onRowDeselect: function (rowModel, record, index, eOpts) {
        if(!this.ignoreSelectionChanges) {
            if(this.selected[record.get(this.keyField)]) {
                for(var j = this.selections.length - 1; j >= 0; j--) {
                    if(this.selections[j].get(this.keyField) == record.get(this.keyField)) {
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
        if(cancel) {
            return false;
        }
        return true;
    },
    clearSelectionOnRowClick: function (view, record, item, index, event, eOpts) {
        var targetElement = event.target;
        var isLeftClick = event.button === 0;
        var isCheckbox = targetElement.className && targetElement.className.indexOf('x-grid-row-checker') > -1;
        if(isLeftClick && !isCheckbox) {
            this.clearSelection();
        }
    }
});
Ext.define('Admin.plugin.GridToolbarPlugin', {
    extend: 'Object',
    alias: 'plugin.gridToolbarPlugin',
    pluginId: 'gridToolbarPlugin',
    constructor: function (config) {
        if(config) {
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
        if(Ext.isFunction(me.toolbar.store.getCount)) {
            me.updateResultCount(me.getCount(me.toolbar.store));
        } else if(Ext.isString(me.toolbar.store)) {
            me.toolbar.store = Ext.StoreManager.lookup(me.toolbar.store);
        }
        me.toolbar.insert(0, me.resultTextItem);
        me.toolbar.insert(1, me.selectAllButton);
        me.toolbar.insert(2, Ext.create('Ext.toolbar.TextItem', {
            text: ' | '
        }));
        me.toolbar.insert(3, me.clearSelectionButton);
        if(!(me.toolbar.store instanceof Ext.data.TreeStore)) {
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
        if(me.toolbar.store) {
            var loadEventName = me.toolbar.store.buffered ? 'prefetch' : 'load';
            me.toolbar.store.on(loadEventName, function (store) {
                me.updateResultCount(me.getCount(store));
            });
        }
        if(me.toolbar.gridPanel) {
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
                        if(cmp.el.hasCls('admin-grid-toolbar-btn-none-selected')) {
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
                        if(cmp.el.hasCls('admin-grid-toolbar-btn-clear-selection')) {
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
        for(var i = 0; i < gridColumns.length; i++) {
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
        if(areAllRecordsSelected && isSelectMode) {
            btn.update('Deselect all');
            btn.el.removeCls('admin-grid-toolbar-btn-none-selected');
        } else if(!areAllRecordsSelected && !isSelectMode) {
            btn.update('Select all');
            btn.el.addCls('admin-grid-toolbar-btn-none-selected');
        }
    },
    updateClearSelection: function (selected) {
        var btn = this.clearSelectionButton;
        var count = selected.length;
        if(count > 0) {
            btn.update('Clear selection (' + selected.length + ')');
        } else {
            btn.update('Clear selection');
        }
    },
    getCount: function (store) {
        if(store instanceof Ext.data.Store) {
            return store.getTotalCount();
        } else if(store instanceof Ext.data.TreeStore) {
            return this.countTreeNodes(store.getRootNode()) - 1;
        } else {
            return undefined;
        }
    },
    countTreeNodes: function (node) {
        if(this.toolbar.countTopLevelOnly) {
            return Ext.isEmpty(node.childNodes) ? 1 : 1 + node.childNodes.length;
        } else {
            var count = 1;
            if(!Ext.isEmpty(node.childNodes)) {
                node.eachChild(function (child) {
                    count += this.countTreeNodes(child);
                }, this);
            }
            return count;
        }
    }
});
Ext.define('Admin.plugin.fileupload.FileUploadGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.fileUploadGrid',
    width: 300,
    height: 150,
    initComponent: function () {
        if(!window['plupload']) {
            alert('FileUploadGrid requires Plupload!');
        }
        this.createStore();
        this.createToolbar();
        this.columns = [
            {
                header: 'Name',
                dataIndex: 'fileName',
                flex: 2
            }, 
            {
                header: 'Size',
                dataIndex: 'fileSize',
                flex: 1
            }
        ];
        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode: 'MULTI'
        });
        this.on('afterrender', function () {
            this.addBodyListeners();
        }, this);
        this.callParent(arguments);
    },
    createToolbar: function () {
        var grid = this;
        grid.tbar = Ext.create('Ext.toolbar.Toolbar', {
            items: [
                {
                    xtype: 'button',
                    text: 'Browse...',
                    iconCls: 'icon-browse',
                    itemId: 'browseButton'
                }, 
                {
                    xtype: 'button',
                    text: 'Upload',
                    iconCls: 'icon-upload',
                    disabled: true,
                    itemId: 'uploadButton',
                    handler: function () {
                        Ext.Msg.alert('TODO', 'Upload');
                    }
                }, 
                {
                    xtype: 'button',
                    text: 'Remove',
                    iconCls: 'icon-remove',
                    disabled: true,
                    itemId: 'removeButton',
                    handler: function () {
                        grid.removeSelectedFiles(grid.getSelectionModel().getSelection());
                    }
                }
            ],
            listeners: {
                afterrender: {
                    fn: grid.initUploader,
                    scope: grid
                }
            }
        });
        grid.getStore().on('datachanged', this.onStoreDataChanged, grid);
        grid.on('selectionchange', this.onSelectionChange, grid);
    },
    initUploader: function () {
        var store = this.getStore();
        var browseButtonHtmlElementId = this.down('toolbar').down('button[itemId=browseButton]').getEl().id;
        var gridHtmlElementId = this.getEl().dom.id;
        this.uploader = new plupload.Uploader({
            runtimes: 'html5,flash,silverlight',
            multi_selection: true,
            browse_button: browseButtonHtmlElementId,
            url: 'data/user/photo',
            multipart: true,
            drop_element: gridHtmlElementId,
            flash_swf_url: 'common/js/fileupload/plupload/js/plupload.flash.swf',
            silverlight_xap_url: 'common/js/fileupload/plupload/js/plupload.silverlight.xap'
        });
        this.uploader.bind('FilesAdded', function (up, files) {
            var file = null;
            var i;
            for(i = 0; i < files.length; i += 1) {
                file = files[i];
                store.add({
                    'fileId': file.id,
                    'fileName': file.name,
                    'fileSize': file.size
                });
            }
        });
        this.uploader.bind('UploadProgress', function (up, file) {
        });
        this.uploader.bind('UploadComplete', function (up, files) {
        });
        this.uploader.bind('Init', function (up, params) {
        });
        this.uploader.init();
    },
    removeSelectedFiles: function (selected) {
        var store = this.getStore(), fileRecord = null;
        var i;
        for(i = 0; i < selected.length; i += 1) {
            fileRecord = selected[i];
            store.remove(fileRecord);
            this.removeFileFromUploaderQueue(fileRecord.data);
        }
    },
    removeFileFromUploaderQueue: function (recordData) {
        var uploaderFiles = this.uploader.files;
        var j;
        for(j = 0; j < uploaderFiles.length; j += 1) {
            if(uploaderFiles[j].id === recordData.fileId) {
                this.uploader.removeFile(uploaderFiles[j]);
            }
        }
    },
    createStore: function () {
        this.store = Ext.create('Ext.data.Store', {
            fields: [
                'fileName', 
                'fileSize', 
                'fileId'
            ],
            data: {
                'items': []
            },
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json',
                    root: 'items'
                }
            }
        });
    },
    onStoreDataChanged: function (store, eOpts) {
        var uploadButton = this.down('toolbar').down('button[itemId=uploadButton]');
        uploadButton.setDisabled(store.data.items.length === 0);
    },
    onSelectionChange: function (model, selected, eOpts) {
        var removeButton = this.down('toolbar').down('button[itemId=removeButton]');
        removeButton.setDisabled(selected.length === 0);
    },
    addBodyListeners: function () {
        var bodyElement = Ext.getBody();
        var gridHtmlElement = this.getEl();
        function cancelEvent(event) {
            if(event.preventDefault) {
                event.preventDefault();
            }
            return false;
        }
        function addDragOverCls() {
            gridHtmlElement.addCls('admin-file-upload-drop-target');
        }
        function removeDragOverCls() {
            gridHtmlElement.removeCls('admin-file-upload-drop-target');
        }
        bodyElement.on('dragover', function (event) {
            addDragOverCls();
            cancelEvent(event);
        });
        bodyElement.on('dragenter', function (event) {
            addDragOverCls();
            cancelEvent(event);
        });
        bodyElement.on('dragleave', function (event) {
            removeDragOverCls();
            cancelEvent(event);
        });
        bodyElement.on('drop', function (event) {
            removeDragOverCls();
            cancelEvent(event);
        });
        bodyElement.on('dragend', function (event) {
            removeDragOverCls();
            cancelEvent(event);
        });
    }
});
Ext.define('Admin.plugin.fileupload.PhotoUploadButton', {
    extend: 'Ext.Component',
    alias: 'widget.photoUploadButton',
    width: 132,
    height: 132,
    uploadUrl: 'rest/upload',
    progressBarHeight: 8,
    tpl: new Ext.XTemplate('<div id="{id}" title="{title}" class="admin-image-upload-button-container" style="width:{width}px;height:{height}px; margin: 0">' + '<img src="{photoUrl}" class="admin-image-upload-button-image" style="width:{width - 2}px;height:{height - 2}px"/>' + '<div class="admin-image-upload-button-progress-bar-container" style="width:{width - 3}px">' + '<div class="admin-image-upload-button-progress-bar" style="height:{progressBarHeight}px"><!-- --></div>' + '</div>' + '</div>'),
    initComponent: function () {
        if(!window['plupload']) {
            alert('ImageUploadButton requires Plupload!');
        }
        var me = this;
        this.addEvents("fileuploaded", "dirtychange");
        this.addListener("fileuploaded", function () {
            me.fireEvent('dirtychange', me, true);
        });
    },
    onRender: function () {
        this.callParent(arguments);
        var buttonElementId = Ext.id(null, 'image-upload-button-');
        var width = this.width;
        var height = this.height;
        var title = this.title;
        var progressBarHeight = this.progressBarHeight;
        var photoUrl = this.photoUrl || 'resources/images/x-user-photo.png';
        this.update({
            id: buttonElementId,
            width: width,
            height: height,
            progressBarHeight: progressBarHeight,
            photoUrl: photoUrl,
            title: title
        });
        this.buttonElementId = buttonElementId;
    },
    afterRender: function () {
        this.initUploader();
        this.addBodyMouseEventListeners();
    },
    initUploader: function () {
        var uploadButton = this;
        var buttonId = this.getId();
        var uploader = new plupload.Uploader({
            runtimes: 'html5,flash,silverlight',
            multi_selection: false,
            browse_button: buttonId,
            url: this.uploadUrl,
            multipart: true,
            drop_element: buttonId,
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
        });
        uploader.bind('FilesAdded', function (up, files) {
        });
        uploader.bind('QueueChanged', function (up) {
            up.start();
        });
        uploader.bind('UploadFile', function (up, file) {
            uploadButton.showProgressBar();
        });
        uploader.bind('UploadProgress', function (up, file) {
            uploadButton.updateProgressBar(file);
        });
        uploader.bind('FileUploaded', function (up, file, response) {
            var responseObj, uploadedResUrl;
            if(response && response.status === 200) {
                responseObj = Ext.decode(response.response);
                uploadedResUrl = (responseObj.items && responseObj.items.length > 0) ? 'rest/upload/' + responseObj.items[0].id : 'resources/images/x-user-photo.png';
                uploadButton.updateImage(uploadedResUrl);
            }
            uploadButton.hideProgressBar();
            uploadButton.fireEvent('fileuploaded', this, responseObj);
        });
        uploader.bind('UploadComplete', function (up, files) {
        });
        setTimeout(function () {
            uploader.init();
        }, 1);
    },
    updateImage: function (src) {
        this.getImageElement().src = src;
    },
    showProgressBar: function () {
        this.getProgressBarContainerElement().style.opacity = 1;
        this.getProgressBarContainerElement().style.visibility = 'visible';
    },
    updateProgressBar: function (file) {
        var progressBar = this.getProgressBarElement();
        var percent = file.percent || 0;
        progressBar.style.width = percent + '%';
    },
    hideProgressBar: function () {
        this.getProgressBarElement().style.width = '0';
        this.getProgressBarContainerElement().style.visibility = 'hidden';
    },
    getImageElement: function () {
        return Ext.DomQuery.select('#' + this.buttonElementId + ' .admin-image-upload-button-image')[0];
    },
    getProgressBarContainerElement: function () {
        return Ext.DomQuery.select('#' + this.buttonElementId + ' .admin-image-upload-button-progress-bar-container')[0];
    },
    getProgressBarElement: function () {
        return Ext.DomQuery.select('#' + this.buttonElementId + ' .admin-image-upload-button-progress-bar')[0];
    },
    addBodyMouseEventListeners: function () {
        var me = this;
        var bodyElement = Ext.getBody();
        var dropTarget = Ext.get(this.buttonElementId);
        var border = Ext.get(this.buttonElementId + '-over-border');
        function cancelEvent(event) {
            if(event.preventDefault) {
                event.preventDefault();
            }
            return false;
        }
        function highlightDropTarget() {
            dropTarget.addCls('admin-file-upload-drop-target');
        }
        function removeHighlightFromDropTarget() {
            dropTarget.dom.className = dropTarget.dom.className.replace(/ admin-file-upload-drop-target/, '');
        }
        dropTarget.on('mouseenter', function (event) {
            highlightDropTarget();
            me.fireEvent('mouseenter');
            cancelEvent(event);
        });
        dropTarget.on('mouseleave', function (event) {
            removeHighlightFromDropTarget();
            me.fireEvent('mouseleave');
            cancelEvent(event);
        });
        dropTarget.on('dragenter', function (event) {
            cancelEvent(event);
        });
        bodyElement.on('dragover', function (event) {
            highlightDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('dragenter', function (event) {
            highlightDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('dragleave', function (event) {
            removeHighlightFromDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('drop', function (event) {
            removeHighlightFromDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('dragend', function (event) {
            removeHighlightFromDropTarget();
            cancelEvent(event);
        });
    }
});
Ext.define('Lib.plugin.fileupload.PhotoUploadWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.photoUploadWindow',
    modal: true,
    title: 'Photo Upload Window',
    width: 250,
    height: 332,
    resizable: false,
    bodyStyle: {
        background: '#fff',
        padding: '10px'
    },
    listeners: {
        resize: function () {
            var slider = this.down('slider');
            var frameWidth = this.getFrame().getWidth();
            slider.setWidth(frameWidth);
        }
    },
    imageInitialWidth: null,
    imageInitialHeight: null,
    initComponent: function () {
        var me = this;
        var uploadButton = {
            xtype: 'filefield',
            name: 'photo',
            buttonOnly: true,
            msgTarget: 'side',
            allowBlank: false,
            anchor: '100%',
            buttonText: 'Choose',
            listeners: {
                change: function () {
                    me.onChangePhoto();
                }
            }
        };
        var cancelButton = {
            xtype: 'button',
            text: 'Cancel',
            handler: function () {
                me.close();
            }
        };
        var setButton = {
            xtype: 'button',
            text: 'Set',
            handler: function () {
                me.set();
            }
        };
        var previewContainer = {
            xtype: 'component',
            itemId: 'preview-frame',
            autoEl: {
                tag: 'div',
                style: {
                    border: '1px solid #ccc',
                    height: '235px',
                    overflow: 'hidden',
                    position: 'relative'
                },
                children: [
                    {
                        tag: 'img',
                        src: '',
                        style: {
                            position: 'absolute',
                            visibility: 'hidden',
                            cursor: 'move'
                        }
                    }
                ]
            }
        };
        var slider = {
            xtype: 'slider',
            value: 100,
            minValue: 0,
            maxValue: 100,
            useTips: false,
            listeners: {
                change: function (slider, newValue) {
                    var image = me.getImage();
                    var newWidth = me.imageInitialWidth * newValue / 100;
                    image.setWidth(newWidth);
                    var newHeight = me.imageInitialHeight * (image.getWidth() / me.imageInitialWidth);
                    image.setHeight(newHeight);
                }
            }
        };
        this.tbar = [
            uploadButton, 
            '->', 
            cancelButton, 
            setButton
        ];
        this.items = [
            previewContainer, 
            slider
        ];
        this.callParent(arguments);
    },
    onChangePhoto: function () {
        var me = this;
        var file = me.getSelectedFiles()[0];
        var previewImage = me.getImage();
        previewImage.on('load', function () {
            me.displayImage(true);
            me.setInitialPhotoSize();
            me.centerImage();
            var previewImageWidth = previewImage.getWidth();
            var frameWidth = me.getFrame().getWidth();
            var sliderNewMinVal = Math.ceil(previewImageWidth * frameWidth / 100 / 10);
            me.getSlider().setMinValue(sliderNewMinVal);
            var dd = new Ext.dd.DD(previewImage.dom.id, 'carsDDGroup', {
                isTarget: false,
                moveOnly: true,
                maintainOffset: false,
                scroll: false
            });
            dd.onDrag = function () {
                dd.resetConstraints();
            };
        });
        previewImage.set({
            src: URL.createObjectURL(file)
        });
    },
    setInitialPhotoSize: function () {
        var frame = this.getFrame();
        var image = this.getImage();
        var fw = frame.getWidth();
        var fh = frame.getHeight();
        var iw = image.getWidth();
        var ih = image.getHeight();
        image.setWidth('');
        image.setHeight('');
        if(iw > fw && ih > fh) {
            if(ih > iw) {
                image.setWidth(frame.getWidth() + 20);
            } else {
                image.setHeight(frame.getHeight() + 20);
            }
        } else if(ih > fh) {
            image.setHeight(frame.getHeight());
        } else {
            image.setWidth(frame.getWidth());
        }
        this.imageInitialWidth = image.getWidth();
        this.imageInitialHeight = image.getHeight();
    },
    centerImage: function () {
        var frame = this.getFrame();
        var image = this.getImage();
        var frameWidth = frame.getWidth();
        var frameHeight = frame.getWidth();
        var imageWidth = image.getWidth();
        var imageHeight = image.getHeight();
        var imageCenterX = (frameWidth - imageWidth) / 2 - 2;
        var imageCenterY = (frameHeight - imageHeight) / 2 - 2;
        image.setLeft(imageCenterX + 'px');
        image.setTop(imageCenterY + 'px');
    },
    getFrame: function () {
        return this.down('#preview-frame');
    },
    getImage: function () {
        return this.getFrame().getEl().down('img');
    },
    getSelectedFiles: function () {
        return this.getEl().down('input[type=file]').dom.files;
    },
    getSlider: function () {
        return this.down('slider');
    },
    displayImage: function (show) {
        if(show) {
            this.getImage().show();
        } else {
            this.getImage().hide();
        }
    },
    set: function () {
        this.close();
    },
    open: function () {
        this.show();
    }
});
Ext.define('Admin.model.SpaceModel', {
    extend: 'Ext.data.Model',
    fields: [
        'name', 
        'displayName', 
        'iconUrl', 
        'rootContentId', 
        {
            name: 'createdTime',
            type: 'date',
            default: new Date()
        }, 
        {
            name: 'modifiedTime',
            type: 'date',
            default: new Date()
        }
    ],
    idProperty: 'name'
});
Ext.define('Admin.store.SpaceStore', {
    extend: 'Ext.data.Store',
    model: 'Admin.model.SpaceModel',
    pageSize: 100,
    autoLoad: true,
    proxy: {
        type: 'direct',
        directFn: Admin.lib.RemoteService.space_list,
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'spaces',
            totalProperty: 'total'
        }
    }
});
var admin;
(function (admin) {
    (function (app) {
        (function (handler) {
            var DeleteSpacesHandler = (function () {
                function DeleteSpacesHandler() { }
                DeleteSpacesHandler.prototype.doDelete = function (spaceModels, callback) {
                    var spaceNames = Ext.Array.map([].concat(spaceModels), function (item) {
                        if(!item) {
                            console.error('No spaces selected');
                        }
                        return item.get('name');
                    });
                    Admin.lib.RemoteService.space_delete({
                        'spaceName': spaceNames
                    }, function (response) {
                        if(response) {
                            callback.call(this, response.success, response);
                        } else {
                            Ext.Msg.alert('Error', response ? response.error : 'Unable to delete space.');
                        }
                    });
                };
                return DeleteSpacesHandler;
            })();
            handler.DeleteSpacesHandler = DeleteSpacesHandler;            
        })(app.handler || (app.handler = {}));
        var handler = app.handler;
    })(admin.app || (admin.app = {}));
    var app = admin.app;
})(admin || (admin = {}));
Ext.define('Admin.view.WizardLayout', {
    extend: 'Ext.layout.container.Card',
    alias: 'layout.wizard',
    mixins: [
        'Ext.util.Animate'
    ],
    deferredRender: false,
    renderHidden: false,
    animation: 'slide',
    easing: 'easeOut',
    duration: 500,
    setActiveItem: function (item) {
        var owner = this.owner;
        var oldCard = this.activeItem;
        var oldIndex = owner.items.indexOf(oldCard);
        var newCard = this.parseActiveItem(item);
        var newIndex = owner.items.indexOf(newCard);
        if(oldCard !== newCard) {
            owner.fireEvent("animationstarted", newCard, oldCard);
            if(newCard.rendered && this.animation && this.animation !== "none") {
                this.syncFx();
                var target = this.getRenderTarget();
                newCard.setWidth(target.getWidth() - target.getPadding("lr") - Ext.getScrollBarWidth());
                switch(this.animation) {
                    case 'fade':
                        newCard.el.setStyle({
                            position: 'absolute',
                            opacity: 0,
                            top: this.getRenderTarget().getPadding('t') + 'px'
                        });
                        newCard.show();
                        if(oldCard) {
                            oldCard.el.fadeOut({
                                useDisplay: true,
                                duration: this.duration,
                                callback: function () {
                                    this.hide();
                                },
                                scope: this.activeItem
                            });
                        }
                        owner.doLayout();
                        newCard.el.fadeIn({
                            useDisplay: true,
                            duration: this.duration,
                            callback: function () {
                                newCard.el.setStyle({
                                    position: ''
                                });
                                owner.fireEvent("animationfinished", newCard, oldCard);
                            },
                            scope: this
                        });
                        break;
                    case 'slide':
                        newCard.el.setStyle({
                            position: 'absolute',
                            visibility: 'hidden',
                            width: this.getRenderTarget().getWidth(),
                            top: this.getRenderTarget().getPadding('t') + 'px'
                        });
                        newCard.show();
                        if(oldCard) {
                            oldCard.el.slideOut(newIndex > oldIndex ? "l" : "r", {
                                duration: this.duration,
                                easing: this.easing,
                                remove: false,
                                scope: this.activeItem,
                                callback: function () {
                                    this.hide();
                                }
                            });
                        }
                        owner.doLayout();
                        newCard.el.slideIn(newIndex > oldIndex ? "r" : "l", {
                            duration: this.duration,
                            easing: this.easing,
                            scope: this,
                            callback: function () {
                                newCard.el.setStyle({
                                    position: ''
                                });
                                owner.fireEvent("animationfinished", newCard, oldCard);
                            }
                        });
                        break;
                }
                this.activeItem = newCard;
                this.sequenceFx();
            } else {
                this.callParent([
                    newCard
                ]);
                owner.fireEvent("animationfinished", newCard, oldCard);
            }
            return newCard;
        }
    }
});
Ext.define('Admin.view.WizardHeader', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizardHeader',
    cls: 'admin-wizard-header-container',
    border: false,
    displayNameProperty: 'displayName',
    displayNameConfig: {
        emptyText: 'Display Name',
        enableKeyEvents: true,
        hideLabel: true,
        autoFocus: true
    },
    pathProperty: 'path',
    pathConfig: {
        hidden: false,
        emptyText: 'path/to/',
        hideLabel: true
    },
    nameProperty: 'name',
    nameConfig: {
        hidden: false,
        allowBlank: false,
        emptyText: 'Name',
        enableKeyEvents: true,
        hideLabel: true,
        vtype: 'name',
        stripCharsRe: /[^a-z0-9\-]+/ig
    },
    initComponent: function () {
        var me = this;
        me.appendVtypes();
        var headerData = this.prepareHeaderData(this.data);
        me.autogenerateName = Ext.isEmpty(headerData[this.nameProperty]);
        me.autogenerateDisplayName = Ext.isEmpty(headerData[this.displayNameProperty]);
        this.displayNameField = Ext.create('Ext.form.field.Text', Ext.apply({
            xtype: 'textfield',
            grow: true,
            growMin: 200,
            name: this.displayNameProperty,
            value: headerData[this.displayNameProperty],
            cls: 'admin-display-name',
            dirtyCls: 'admin-display-name-dirty'
        }, me.displayNameConfig, Admin.view.WizardHeader.prototype.displayNameConfig));
        this.displayNameField.on({
            afterrender: me.onDisplayNameAfterrender,
            keyup: me.onDisplayNameKey,
            change: me.onDisplayNameChanged,
            scope: me
        });
        this.pathField = Ext.create('Ext.form.field.Display', Ext.apply({
            xtype: 'displayfield',
            cls: 'admin-path',
            dirtyCls: 'admin-path-dirty',
            value: headerData[this.pathProperty]
        }, me.pathConfig, Admin.view.WizardHeader.prototype.pathConfig));
        this.nameField = Ext.create('Ext.form.field.Text', Ext.apply({
            xtype: 'textfield',
            grow: true,
            growMin: 60,
            cls: 'admin-name',
            dirtyCls: 'admin-name-dirty',
            name: this.nameProperty,
            value: headerData[this.nameProperty],
            listeners: {
                change: function (textfield, newValue) {
                    textfield.setValue(textfield.processRawValue(newValue));
                },
                scope: this
            }
        }, me.nameConfig, Admin.view.WizardHeader.prototype.nameConfig));
        this.nameField.on({
            keyup: me.onNameKey,
            change: me.onNameChanged,
            scope: me
        });
        this.items = [
            me.displayNameField
        ];
        if(!me.pathField.hidden && !me.nameField.hidden) {
            this.items.push({
                xtype: 'fieldcontainer',
                hideLabel: true,
                layout: 'hbox',
                items: [
                    me.pathField, 
                    me.nameField
                ]
            });
        } else if(!me.pathField.hidden) {
            this.items.push(me.pathField);
        } else if(!me.nameField.hidden) {
            this.items.push(me.nameField);
        }
        this.callParent(arguments);
        this.addEvents('displaynamechange', 'displaynameoverride', 'namechange', 'nameoverride');
    },
    onDisplayNameAfterrender: function (field) {
        if(!field.readOnly && field.autoFocus) {
            field.focus(false, 100);
            field.selectText(0, 0);
        }
    },
    onDisplayNameKey: function (field, event, opts) {
        var wasAutoGenerate = this.autogenerateDisplayName;
        var autoGenerate = Ext.isEmpty(field.getValue());
        if(wasAutoGenerate != autoGenerate) {
            this.fireEvent('displaynameoverride', !autoGenerate);
        }
        this.autogenerateDisplayName = autoGenerate;
    },
    onDisplayNameChanged: function (field, newVal, oldVal, opts) {
        if(this.fireEvent('displaynamechange', newVal, oldVal) !== false && this.autogenerateName) {
            var processedValue = this.nameField.processRawValue(this.preProcessName(newVal));
            this.nameField.setValue(processedValue);
        }
        this.nameField.growMax = this.el.getWidth() - 100;
        this.nameField.doComponentLayout();
    },
    onNameKey: function (field, event, opts) {
        var wasAutoGenerate = this.autogenerateName;
        var autoGenerate = Ext.isEmpty(field.getValue());
        if(wasAutoGenerate != autoGenerate) {
            this.fireEvent('nameoverride', !autoGenerate);
        }
        this.autogenerateName = autoGenerate;
    },
    onNameChanged: function (field, newVal, oldVal, opts) {
        this.fireEvent('namechange', newVal, oldVal);
    },
    appendVtypes: function () {
        Ext.apply(Ext.form.field.VTypes, {
            name: function (val, field) {
                return /^[a-z0-9\-]+$/i.test(val);
            },
            nameText: 'Not a valid name. Can contain digits, letters and "-" only.',
            nameMask: /^[a-z0-9\-]+$/i
        });
        Ext.apply(Ext.form.field.VTypes, {
            qualifiedName: function (val, field) {
                return /^[a-z0-9\-:]+$/i.test(val);
            },
            qualifiedNameText: 'Not a valid qualified name. Can contain digits, letters, ":" and "-" only.',
            qualifiedNameMask: /^[a-z0-9\-:]+$/i
        });
        Ext.apply(Ext.form.field.VTypes, {
            path: function (val, field) {
                return /^[a-z0-9\-\/]+$/i.test(val);
            },
            pathText: 'Not a valid path. Can contain digits, letters, "/" and "-" only.',
            pathMask: /^[a-z0-9\-\/]+$/i
        });
    },
    preProcessName: function (displayName) {
        return displayName;
    },
    prepareHeaderData: function (data) {
        return data && data.data || data || {
        };
    },
    setData: function (data) {
        this.data = data;
        this.getForm().setValues(this.resolveHeaderData(data));
    },
    getData: function () {
        return this.getForm().getFieldValues();
    },
    getDisplayName: function () {
        return this.displayNameField.getValue();
    },
    setDisplayName: function (displayName) {
        this.displayNameField.setValue(displayName);
    },
    setName: function (name) {
        this.nameField.setValue(name);
    }
});
Ext.define('Admin.view.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.wizardPanel',
    requires: [
        'Admin.view.WizardLayout'
    ],
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    cls: 'admin-wizard',
    externalControls: undefined,
    showControls: true,
    data: undefined,
    isNew: true,
    validateItems: [],
    boundItems: [],
    isWizardValid: undefined,
    isWizardDirty: undefined,
    dirtyItems: undefined,
    invalidItems: undefined,
    presentationMode: false,
    initComponent: function () {
        var me = this;
        var events = [
            "beforestepchanged", 
            "stepchanged", 
            "animationstarted", 
            "animationfinished", 
            'validitychange', 
            'dirtychange', 
            "finished"
        ];
        this.dirtyItems = [];
        this.invalidItems = [];
        this.boundItems = [];
        this.cls += this.isNew ? ' admin-wizard-new' : ' admin-wizard-edit';
        this.wizard = Ext.createByAlias('widget.container', {
            region: 'center',
            layout: {
                type: 'wizard',
                animation: 'none'
            },
            items: this.createSteps()
        });
        this.items = [
            this.createHeaderPanel(), 
            {
                itemId: 'bottomPanel',
                xtype: 'container',
                autoScroll: true,
                padding: '20 0 0 0',
                layout: 'border',
                flex: 1,
                items: [
                    {
                        xtype: 'container',
                        region: 'west',
                        padding: 10,
                        width: 130,
                        style: {
                            position: 'fixed !important',
                            top: '210px !important'
                        },
                        layout: {
                            type: 'hbox',
                            align: 'middle'
                        },
                        listeners: {
                            click: {
                                element: 'el',
                                fn: function () {
                                    me.prev();
                                }
                            },
                            mouseover: {
                                element: 'el',
                                fn: function (event, element) {
                                    me.updateNavButton(element, '#000000');
                                }
                            },
                            mouseout: {
                                element: 'el',
                                fn: function (event, element) {
                                    me.updateNavButton(element, '#777777');
                                }
                            }
                        },
                        items: [
                            {
                                xtype: 'button',
                                itemId: 'prev',
                                iconCls: 'wizard-nav-icon icon-chevron-left icon-6x',
                                cls: 'wizard-nav-button wizard-nav-button-left',
                                height: 64,
                                width: 64,
                                padding: 0,
                                margin: '0 0 0 40'
                            }
                        ]
                    }, 
                    {
                        xtype: 'container',
                        region: 'east',
                        padding: 10,
                        width: 130,
                        style: {
                            position: 'fixed !important',
                            top: '210px !important'
                        },
                        layout: {
                            type: 'hbox',
                            align: 'middle'
                        },
                        listeners: {
                            click: {
                                element: 'el',
                                fn: function () {
                                    me.next();
                                }
                            },
                            mouseover: {
                                element: 'el',
                                fn: function (event, element) {
                                    me.updateNavButton(element, '#000000');
                                }
                            },
                            mouseout: {
                                element: 'el',
                                fn: function (event, element) {
                                    me.updateNavButton(element, '#777777');
                                }
                            }
                        },
                        items: [
                            {
                                xtype: 'button',
                                itemId: 'next',
                                iconAlign: 'right',
                                cls: 'wizard-nav-button wizard-nav-button-right',
                                formBind: true,
                                iconCls: 'wizard-nav-icon icon-chevron-right icon-6x',
                                height: 64,
                                width: 64,
                                padding: 0
                            }
                        ]
                    }, 
                    this.wizard
                ],
                listeners: {
                    scroll: {
                        element: 'el',
                        fn: function () {
                            me.updateShadow(me);
                        }
                    }
                }
            }
        ];
        Ext.EventManager.onWindowResize(function () {
            me.updateShadow(me);
        });
        this.callParent(arguments);
        this.addEvents(events);
        this.wizard.addEvents(events);
        this.wizard.enableBubble(events);
        this.on({
            animationstarted: this.onAnimationStarted,
            animationfinished: this.onAnimationFinished
        });
        if(this.getActionButton()) {
            this.boundItems.push(this.getActionButton());
        }
        this.down('#progressBar').update(this.wizard.items.items);
        this.on('afterrender', this.bindItemListeners);
        me.updateShadow(me);
    },
    updateShadow: function (me) {
        var bottomPanel = me.down('#bottomPanel').getEl();
        if(bottomPanel) {
            var hasScroll = bottomPanel.dom.scrollHeight > bottomPanel.dom.clientHeight, positionPanelEl = me.down('#positionPanel').getEl(), wizardHeaderPanelHeight = me.down('#wizardHeaderPanel').getEl().getHeight(), headerShadowEl = Ext.fly('admin-wizard-header-shadow');
            if(hasScroll && bottomPanel.dom.scrollTop !== 0) {
                if(!headerShadowEl) {
                    var dh = Ext.DomHelper, boxShadowOffsets = Ext.isGecko ? '0 5px 6px -3px' : '0 5px 10px -3px';
                    var shadowDomSpec = {
                        id: 'admin-wizard-header-shadow',
                        tag: 'div',
                        style: 'position:absolute; top:' + wizardHeaderPanelHeight + 'px; left:0px; z-index:1000; height:10px; background:transparent; width:100%; box-shadow:' + boxShadowOffsets + '#888 inset'
                    };
                    dh.append(positionPanelEl, shadowDomSpec);
                    Ext.fly('admin-wizard-header-shadow').show(true);
                }
            } else {
                if(headerShadowEl) {
                    headerShadowEl.remove();
                }
            }
        }
    },
    updateNavButton: function (element, color) {
        var btn = Ext.get(element);
        if(!btn.hasCls('wizard-nav-icon')) {
            btn = btn.down('.wizard-nav-icon');
        } else if(btn.hasCls('x-btn-inner')) {
            btn = btn.next('.x-btn-icon');
        }
        btn.setStyle('color', color);
    },
    updateProgress: function (newStep) {
        var progressBar = this.down('#progressBar');
        progressBar.update(this.wizard.items.items);
        var conditionsMet = this.isWizardValid && (this.isWizardDirty || this.isNew);
        progressBar.setDisabled(this.isNew ? !this.isStepValid(newStep) : !conditionsMet);
    },
    bindItemListeners: function (cmp) {
        Ext.each(cmp.validateItems, function (validateItem, i) {
            if(validateItem) {
                validateItem.on({
                    'validitychange': cmp.handleValidityChange,
                    'dirtychange': cmp.handleDirtyChange,
                    scope: cmp
                }, this);
            }
        });
        var checkValidityFn = function (panel) {
            panel.getForm().checkValidity();
        };
        cmp.wizard.items.each(function (item, i) {
            if(i === 0) {
                cmp.onAnimationFinished(item, null);
            }
            if('editUserFormPanel' === item.getXType()) {
                item.on('fieldsloaded', checkValidityFn);
            }
            var itemForm = Ext.isFunction(item.getForm) ? item.getForm() : undefined;
            if(itemForm) {
                if(Ext.isFunction(cmp.washDirtyForm)) {
                    cmp.washDirtyForm(itemForm);
                }
                Ext.apply(itemForm, {
                    onValidityChange: cmp.formOnValidityChange,
                    _boundItems: undefined
                });
                itemForm.on({
                    'validitychange': cmp.handleValidityChange,
                    'dirtychange': cmp.handleDirtyChange,
                    scope: cmp
                });
                itemForm.checkValidity();
            }
        });
    },
    formOnValidityChange: function () {
        var wizardPanel = this.owner.up('wizardPanel');
        var boundItems = wizardPanel.getFormBoundItems(this);
        if(boundItems && this.owner === wizardPanel.getActiveItem()) {
            var valid = wizardPanel.isStepValid(this.owner);
            boundItems.each(function (cmp) {
                if(cmp.rendered && cmp.isHidden() === valid) {
                    if(valid) {
                        cmp.show();
                    } else {
                        cmp.hide();
                    }
                }
            });
        }
    },
    getFormBoundItems: function (form) {
        var boundItems = form._boundItems;
        if(!boundItems && form.owner.rendered) {
            boundItems = form._boundItems = Ext.create('Ext.util.MixedCollection');
            boundItems.addAll(form.owner.query('[formBind]'));
            boundItems.addAll(this.boundItems);
        }
        return boundItems;
    },
    handleValidityChange: function (form, valid, opts) {
        if(!valid) {
            Ext.Array.include(this.invalidItems, form);
        } else {
            Ext.Array.remove(this.invalidItems, form);
        }
        this.updateProgress();
        var isWizardValid = this.invalidItems.length === 0;
        if(this.isWizardValid !== isWizardValid) {
            this.isWizardValid = isWizardValid;
            var actionButton = this.getActionButton();
            if(actionButton) {
                actionButton.setVisible(isWizardValid);
            }
            this.fireEvent('validitychange', this, isWizardValid);
        }
    },
    handleDirtyChange: function (form, dirty, opts) {
        if(dirty) {
            Ext.Array.include(this.dirtyItems, form);
        } else {
            Ext.Array.remove(this.dirtyItems, form);
        }
        this.updateProgress();
        var isWizardDirty = this.dirtyItems.length > 0;
        if(this.isWizardDirty !== isWizardDirty) {
            this.isWizardDirty = isWizardDirty;
            this.fireEvent('dirtychange', this, isWizardDirty);
        }
    },
    isStepValid: function (step) {
        var isStepValid = Ext.Array.intersect(this.invalidItems, this.validateItems).length === 0;
        var activeStep = step || this.getActiveItem();
        var activeForm;
        if(activeStep && Ext.isFunction(activeStep.getForm)) {
            activeForm = activeStep.getForm();
        }
        if(isStepValid && activeForm) {
            isStepValid = isStepValid && !activeForm.hasInvalidField();
        }
        return isStepValid;
    },
    getProgressBar: function () {
        return this.down('#progressBar');
    },
    createRibbon: function () {
        var me = this;
        var stepsTpl = '<div class="navigation-container">' + '<ul class="navigation clearfix">' + '<tpl for=".">' + '<li class="{[ this.resolveClsName( xindex, xcount ) ]}" wizardStep="{[xindex]}">' + '<a href="javascript:;" class="step {[ this.resolveClsName( xindex, xcount ) ]}">{[' + '(values.stepTitle || values.title) ]}</a></li>' + '</tpl>' + '</ul>' + '</div>';
        return {
            xtype: 'component',
            flex: 1,
            cls: 'toolbar',
            disabledCls: 'toolbar-disabled',
            itemId: 'progressBar',
            width: '100%',
            listeners: {
                click: {
                    fn: this.changeStep,
                    element: 'el',
                    scope: this
                }
            },
            styleHtmlContent: true,
            margin: 0,
            tpl: new Ext.XTemplate(stepsTpl, {
                resolveClsName: function (index, total) {
                    var activeIndex = me.wizard.items.indexOf(me.getActiveItem()) + 1;
                    var clsName = '';
                    if(index === 1) {
                        clsName += 'first ';
                    }
                    if(index < activeIndex) {
                        clsName += 'previous ';
                    }
                    if(index + 1 === activeIndex) {
                        clsName += 'immediate ';
                    }
                    if(index === activeIndex) {
                        clsName += 'current ';
                    }
                    if(index > activeIndex) {
                        clsName += 'next ';
                    }
                    if(index - 1 === activeIndex) {
                        clsName += 'immediate ';
                    }
                    if(index === total) {
                        clsName += 'last ';
                    }
                    return clsName;
                }
            })
        };
    },
    onAnimationStarted: function (newStep, oldStep) {
        if(this.showControls) {
            this.updateButtons(this.wizard, true);
        }
        if(this.externalControls) {
            this.updateButtons(this.externalControls, true);
        }
    },
    onAnimationFinished: function (newStep, oldStep) {
        if(newStep) {
            this.updateProgress(newStep);
            this.focusFirstField(newStep);
            this.fireEvent("stepchanged", this, oldStep, newStep);
            if(this.showControls) {
                this.updateButtons(this.wizard);
            }
            if(this.externalControls) {
                this.updateButtons(this.externalControls);
            }
            if(Ext.isFunction(newStep.getForm)) {
                var newForm = newStep.getForm();
                if(newForm) {
                    newForm.onValidityChange(this.isStepValid(newStep));
                }
            }
            this.doLayout();
            return newStep;
        }
    },
    focusFirstField: function (newStep) {
        var activeItem = newStep || this.getActiveItem();
        var firstField;
        if(activeItem && (firstField = activeItem.down('field[disabled=false]'))) {
            firstField.focus(false);
            if(firstField.rendered && firstField.selectText) {
                firstField.selectText(0, 0);
            }
        }
    },
    updateButtons: function (toolbar, disable) {
        if(toolbar) {
            var prev = this.down('#prev'), next = this.down('#next');
            var hasNext = this.getNext(), hasPrev = this.getPrev();
            if(prev) {
                if(disable || !hasPrev) {
                    prev.hide();
                } else {
                    prev.show();
                }
            }
            if(next) {
                if(disable || !hasNext) {
                    next.hide();
                } else {
                    next.show();
                }
                next.removeCls('admin-prev-button');
                next.removeCls('admin-button');
                next.addCls(hasPrev ? 'admin-prev-button' : 'admin-button');
            }
        }
    },
    changeStep: function (event, target) {
        var progressBar = this.down('#progressBar');
        var isNew = this.isNew;
        var isDisabled = progressBar.isDisabled();
        var li = target && target.tagName === "LI" ? Ext.fly(target) : Ext.fly(target).up('li');
        if((!isDisabled && isNew && li && li.hasCls('next') && li.hasCls('immediate')) || (!isDisabled && !isNew) || (isDisabled && !isNew && li && !li.hasCls('last')) || (li && li.hasCls('previous'))) {
            var step = Number(li.getAttribute('wizardStep'));
            this.navigate(step - 1);
        }
        event.stopEvent();
    },
    createHeaderPanel: function () {
        var icon = this.createIcon();
        return {
            xtype: 'container',
            itemId: 'wizardHeaderPanel',
            cls: 'admin-wizard-panel',
            padding: '10 0 0 10',
            margin: '0 0 0 0',
            layout: {
                type: 'table',
                columns: 2,
                tableAttrs: {
                    width: '100%'
                }
            },
            items: [
                Ext.applyIf(icon, {
                    rowspan: 2,
                    tdAttrs: {
                        style: 'padding-right: 10px'
                    }
                }), 
                Ext.applyIf(this.createWizardHeader(), {
                    tdAttrs: {
                        width: '100%'
                    }
                }), 
                {
                    itemId: 'positionPanel',
                    xtype: 'container',
                    style: {
                        backgroundColor: '#EEEEEE'
                    },
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    tdAttrs: {
                        style: 'vertical-align: bottom;'
                    },
                    items: [
                        Ext.applyIf(this.createRibbon(), {
                            flex: 1
                        }), 
                        Ext.apply(this.createActionButton(), {
                            itemId: 'actionButton',
                            ui: 'green',
                            margin: '0 30 0 0',
                            scale: 'large'
                        })
                    ]
                }
            ]
        };
    },
    getActionButton: function () {
        return this.down('#actionButton');
    },
    next: function (btn) {
        return this.navigate("next", btn);
    },
    prev: function (btn) {
        return this.navigate("prev", btn);
    },
    finish: function () {
        this.fireEvent("finished", this, this.getData());
    },
    getNext: function () {
        return this.wizard.getLayout().getNext();
    },
    getPrev: function () {
        return this.wizard.getLayout().getPrev();
    },
    getActiveItem: function () {
        return this.wizard.getLayout().getActiveItem();
    },
    navigate: function (direction, btn) {
        var oldStep = this.getActiveItem();
        if(btn) {
            this.externalControls = btn.up('toolbar');
        }
        if(this.fireEvent("beforestepchanged", this, oldStep) !== false) {
            var newStep;
            switch(direction) {
                case "-1":
                case "prev":
                    if(this.getPrev()) {
                        newStep = this.wizard.getLayout().prev();
                    }
                    break;
                case "+1":
                case "next":
                    if(this.getNext()) {
                        newStep = this.wizard.getLayout().next();
                    } else {
                        this.finish();
                    }
                    break;
                default:
                    newStep = this.wizard.getLayout().setActiveItem(direction);
                    break;
            }
        }
    },
    addData: function (newValues) {
        if(Ext.isEmpty(this.data)) {
            this.data = {
            };
        }
        Ext.merge(this.data, newValues);
    },
    deleteData: function (key) {
        if(key) {
            delete this.data[key];
        }
    },
    getData: function () {
        var me = this;
        me.wizard.items.each(function (item) {
            if(item.getData) {
                me.addData(item.getData());
            } else if(item.getForm) {
                me.addData(item.getForm().getFieldValues());
            }
        });
        return me.data;
    },
    createSteps: function () {
    },
    createIcon: function () {
    },
    createWizardHeader: function () {
    },
    createActionButton: function () {
    }
});
Ext.define('Admin.view.BaseContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.baseContextMenu',
    cls: 'admin-context-menu',
    border: false,
    shadow: false
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
Ext.define('Admin.view.BaseDetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.detailPanel',
    requires: [
        'Admin.view.DropDownButton', 
        'Admin.view.BaseContextMenu'
    ],
    layout: 'card',
    cls: 'admin-preview-panel admin-detail',
    border: false,
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
            if(this.isFullPage) {
                this.hideActionButton();
            }
            if(this.singleSelection.tabs.length > 0) {
                this.changeTab(this.singleSelection.tabs[0].name);
            }
        }
    },
    initComponent: function () {
        if(this.isVertical) {
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
        if(actionsButton) {
            actionsButton.setVisible(false);
        }
    },
    actionButtonItems: [],
    getActionItems: function () {
        return this.actionButtonItems;
    },
    getActionButton: function () {
        var me = this;
        if(this.actionButtonItems.length < 1) {
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
        if(me.isVertical) {
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
        if(!doRender) {
            return {
            };
        }
        return {
            xtype: 'component',
            cls: (me.isVertical ? 'vertical' : 'horizontal'),
            margin: (me.isVertical ? '0' : '20 0 0'),
            colspan: 3,
            tpl: Ext.create('Ext.XTemplate', '<ul class="admin-detail-nav">' + '<tpl for=".">' + '<li data-tab="{name}">{displayName}</li>' + '</tpl>' + '</ul>'),
            data: me.singleSelection.tabs,
            listeners: {
                click: {
                    element: 'el',
                    fn: function (evt, element) {
                        var tab = element.attributes['data-tab'].value;
                        var panels = Ext.ComponentQuery.query('contentDetail');
                        for(var i = 0; i < panels.length; i++) {
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
            '<a id="remove-from-selection-button:{data.' + this.keyField + '}" class="deselect icon-remove icon-2x" href="javascript:;"></a>', 
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
            '<a id="remove-from-selection-button:{data.' + this.keyField + '}" class="deselect icon-remove icon-large" href="javascript:;"></a>', 
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
    setDataCallback: function (data) {
    },
    resolveActiveItem: function (data) {
        var activeItem;
        if(Ext.isEmpty(this.data)) {
            activeItem = 'noSelection';
        } else if(Ext.isObject(this.data) || this.data.length === 1) {
            activeItem = 'singleSelection';
        } else if(this.data.length > 1 && this.data.length <= 10) {
            activeItem = 'largeBoxSelection';
        } else {
            activeItem = 'smallBoxSelection';
        }
        return activeItem;
    },
    resolveActiveData: function (data) {
        var activeData;
        if(Ext.isArray(data) && data.length === 1) {
            activeData = data[0];
        } else {
            activeData = data;
        }
        return activeData;
    },
    updateActiveItem: function (data, item) {
        item = item || this.getLayout().getActiveItem();
        if('singleSelection' === item.itemId) {
            var previewHeader = item.down('#previewHeader');
            previewHeader.update(data);
            var previewPhoto = item.down('#previewPhoto');
            previewPhoto.update(data);
            this.changeTab('traffic');
        } else if('largeBoxSelection' === item.itemId || 'smallBoxSelection' === item.itemId) {
            item.update(data);
        }
    },
    setData: function (data) {
        this.data = data;
        var toActivate = this.resolveActiveItem(data);
        var active = this.getLayout().getActiveItem();
        if(active.itemId !== toActivate) {
            active = this.getLayout().setActiveItem(toActivate);
        }
        if(active) {
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
        for(var tab in tabs) {
            if(tabs[tab].name === name) {
                return tabs[tab];
            }
        }
        return null;
    },
    changeTab: function (selectedTab) {
        var currentTab = this.getTab(selectedTab);
        if(currentTab) {
            var target = this.down('#center');
            target.remove(target.child());
            if(currentTab.items) {
                target.add(currentTab.items);
                if(currentTab.callback) {
                    currentTab.callback(target);
                }
            }
            var elements = Ext.dom.Query.select('*[data-tab=' + selectedTab + ']');
            for(var i = 0; i < elements.length; i++) {
                var children = elements[i].parentElement.children;
                for(var j = 0; j < children.length; j++) {
                    children[j].className = '';
                }
                elements[i].className = 'active';
            }
        }
    }
});
Ext.define('Admin.view.DetailToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.spaceDetailToolbar',
    cls: 'admin-toolbar',
    defaults: {
        scale: 'medium'
    },
    initComponent: function () {
        this.items = [
            {
                text: 'Edit',
                action: 'editSpace'
            }, 
            {
                text: 'Delete',
                action: 'deleteSpace'
            }, 
            '->', 
            {
                text: 'Close',
                action: 'closeSpace'
            }
        ];
        this.callParent(arguments);
    }
});
var admin;
(function (admin) {
    (function (ui) {
        var SpaceDetailPanel = (function () {
            function SpaceDetailPanel(region) {
                this.isVertical = false;
                this.keyField = 'name';
                this.tabs = [
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
                var p = this.ext = new Ext.panel.Panel();
                if(region) {
                    p.region = region;
                }
                p.layout = 'card';
                p.cls = 'admin-preview-panel admin-detail';
                p.border = false;
                p.split = true;
                p.collapsible = true;
                p.header = false;
                p.flex = 1;
                p.isVertical = this.isVertical;
                p.isFullPage = false;
                p.keyField = this.keyField;
                p.listeners = {
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
                        if(this.isFullPage) {
                            this.hideActionButton();
                        }
                        if(this.tabs.length > 0) {
                            this.changeTab(this.tabs[0].name);
                        }
                    }
                };
                if(p.isVertical) {
                    p.cls = p.cls + 'admin-detail-vertical';
                }
                p.add(this.noSelection());
                p.add(this.singleSelectionComponent(this.data));
                p.add(this.smallBoxSelection(this.data));
                p.add(this.largeBoxSelection(this.data));
            }
            SpaceDetailPanel.prototype.noSelection = function () {
                var p = new Ext.panel.Panel();
                p.itemId = 'noSelection';
                p.styleHtmlContent = true;
                p.padding = 10;
                p.bodyStyle = {
                    border: 'none'
                };
                p.html = '<div>Nothing selected</div>';
                return p;
            };
            SpaceDetailPanel.prototype.singleSelectionComponent = function (data) {
                var _this = this;
                var c = new Ext.container.Container();
                c.itemId = 'singleSelection';
                c.layout = 'border';
                c.overflowX = 'hidden';
                c.overflowY = 'hidden';
                var tabNavigation = new Ext.Component({
                    listeners: {
                        click: {
                            element: 'el',
                            fn: function (evt, element) {
                                var tab = element.attributes['data-tab'].value;
                                _this.changeTab(tab);
                            }
                        }
                    }
                });
                tabNavigation.cls = this.isVertical ? 'vertical' : 'horizontal';
                tabNavigation.margin = this.isVertical ? '0' : '20 0 0';
                tabNavigation.colSpan = 3;
                tabNavigation.tpl = new Ext.XTemplate('<ul class="admin-detail-nav">', '<tpl for=".">', '<li data-tab="{name}">{displayName}</li>', '</tpl>', '</ul>');
                tabNavigation.data = this.tabs;
                var north = new Ext.container.Container();
                north.region = 'north';
                north.cls = 'north';
                north.margin = '5 0';
                north.height = (this.isVertical ? 100 : 64);
                north.layout = {
                    type: 'table',
                    tableAttrs: {
                        style: {
                            tableLayout: 'fixed',
                            width: '100%'
                        }
                    },
                    columns: 2
                };
                if(this.isVertical) {
                    north.add(tabNavigation);
                }
                var photo = this.photo = new Ext.Component();
                photo.width = 64;
                photo.itemId = 'previewPhoto';
                photo.tpl = '<img src="{data.iconUrl}?size=80" style="width: 64px;" alt="{name}"/>';
                photo.data = data;
                photo.margin = '0 5 0 5';
                photo.tdAttrs = {
                    width: 80
                };
                north.add(photo);
                var header = this.header = new Ext.Component();
                header.itemId = 'previewHeader';
                header.tpl = '<h1 title="{data.displayName}">{data.displayName}</h1><span class="path" title="{data.path}">{data.path}</span>';
                header.data = data;
                header.cls = 'admin-detail-header';
                north.add(header);
                c.add(north);
                var west = new Ext.container.Container();
                west.region = 'west';
                west.cls = 'west';
                west.width = 200;
                if(!this.isVertical) {
                    west.add(tabNavigation);
                }
                c.add(west);
                var center = this.center = new Ext.container.Container();
                center.cls = 'center';
                center.itemId = 'center';
                center.region = 'center';
                c.add(center);
                return c;
            };
            SpaceDetailPanel.prototype.smallBoxSelection = function (data) {
                var c = new Ext.Component();
                c.itemId = 'smallBoxSelection';
                c.styleHtmlContent = true;
                c.padding = 10;
                c.autoScroll = true;
                c.bodyStyle = {
                    border: 'none'
                };
                c.tpl = [
                    '<tpl for=".">', 
                    '<div id="selected-item-box-{data.' + this.keyField + '}" class="admin-selected-item-box small clearfix">', 
                    '<div class="left"><img src="{data.iconUrl}?size=20" alt="{data.name}"/></div>', 
                    '<div class="center">{data.displayName}</div>', 
                    '<div class="right">', 
                    '<a id="remove-from-selection-button:{data.' + this.keyField + '}" class="deselect icon-remove icon-large" href="javascript:;"></a>', 
                    '</div>', 
                    '</div>', 
                    '</tpl>'
                ];
                c.data = data;
                return c;
            };
            SpaceDetailPanel.prototype.largeBoxSelection = function (data) {
                var c = new Ext.Component();
                c.itemId = 'largeBoxSelection';
                c.styleHtmlContent = true;
                c.padding = 10;
                c.bodyStyle = {
                    border: 'none'
                };
                c.autoScroll = true;
                c.tpl = [
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
                    '<a id="remove-from-selection-button:{data.' + this.keyField + '}" class="deselect icon-remove icon-2x" href="javascript:;"></a>', 
                    '</div>', 
                    '</div>', 
                    '</tpl>'
                ];
                c.data = data;
            };
            SpaceDetailPanel.prototype.resolveActiveItem = function () {
                var activeItem;
                if(Ext.isEmpty(this.data)) {
                    activeItem = 'noSelection';
                } else if(Ext.isObject(this.data) || this.data.length === 1) {
                    activeItem = 'singleSelection';
                } else if(this.data.length > 1 && this.data.length <= 10) {
                    activeItem = 'largeBoxSelection';
                } else {
                    activeItem = 'smallBoxSelection';
                }
                return activeItem;
            };
            SpaceDetailPanel.prototype.getLayout = function () {
                return this.ext.getLayout();
            };
            SpaceDetailPanel.prototype.resolveActiveData = function (data) {
                var activeData;
                if(Ext.isArray(data) && data.length === 1) {
                    activeData = data[0];
                } else {
                    activeData = data;
                }
                return activeData;
            };
            SpaceDetailPanel.prototype.updateActiveItem = function (data, item) {
                item = item || this.getLayout().getActiveItem();
                if('singleSelection' === item.itemId) {
                    this.header.update(data);
                    this.photo.update(data);
                    this.changeTab(this.tabs[0].name);
                } else if('largeBoxSelection' === item.itemId || 'smallBoxSelection' === item.itemId) {
                    item.update(data);
                }
            };
            SpaceDetailPanel.prototype.getTab = function (name) {
                var tabs = this.tabs;
                for(var tab in tabs) {
                    if(tabs[tab].name === name) {
                        return tabs[tab];
                    }
                }
                return null;
            };
            SpaceDetailPanel.prototype.changeTab = function (selectedTab) {
                var currentTab = this.getTab(selectedTab);
                if(currentTab) {
                    var target = this.center;
                    target.remove(target.child());
                    if(currentTab.items) {
                        target.add(currentTab.items);
                        if(currentTab.callback) {
                            currentTab.callback(target);
                        }
                    }
                    var elements = Ext.dom.Query.select('*[data-tab=' + selectedTab + ']');
                    for(var i = 0; i < elements.length; i++) {
                        var children = elements[i].parentElement.children;
                        for(var j = 0; j < children.length; j++) {
                            children[j].className = '';
                        }
                        elements[i].className = 'active';
                    }
                }
            };
            SpaceDetailPanel.prototype.setData = function (data) {
                console.log(data);
                this.data = data;
                var toActivate = this.resolveActiveItem();
                var active = this.getLayout().getActiveItem();
                if(active.itemId !== toActivate) {
                    active = this.getLayout().setActiveItem(toActivate);
                }
                if(active) {
                    var activeData = this.resolveActiveData(data);
                    this.updateActiveItem(activeData, active);
                }
            };
            return SpaceDetailPanel;
        })();
        ui.SpaceDetailPanel = SpaceDetailPanel;        
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var DeleteSpaceWindow = (function () {
            function DeleteSpaceWindow() {
                var _this = this;
                this.title = "Delete space(s)";
                this.deleteHandler = new admin.app.handler.DeleteSpacesHandler();
                this.template = '<div class="delete-container">' + '<tpl for=".">' + '<div class="delete-item">' + '<img class="icon" src="{data.iconUrl}"/>' + '<h4>{data.displayName}</h4>' + '<p>{data.type}</p>' + '</div>' + '</tpl>' + '</div>';
                this.initComponent();
                APP.event.onDeletePrompt(function (event) {
                    _this.setModel(event.getModel());
                    _this.doShow();
                });
            }
            DeleteSpaceWindow.prototype.initComponent = function () {
                var _this = this;
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
                deleteButton.setHandler(function (btn, evt) {
                    _this.deleteHandler.doDelete(_this.data, deleteCallback);
                });
                buttonRow.add(deleteButton);
                var cancelButton = new Ext.button.Button();
                cancelButton.text = 'Cancel';
                cancelButton.margin = '0 0 0 10';
                cancelButton.setHandler(function () {
                    ct.hide();
                });
                buttonRow.add(cancelButton);
                ct.add(buttonRow);
            };
            DeleteSpaceWindow.prototype.setModel = function (model) {
                this.data = model;
                if(model) {
                    if(this.content) {
                        this.content.update(model);
                    }
                }
            };
            DeleteSpaceWindow.prototype.doShow = function () {
                this.container.show();
            };
            return DeleteSpaceWindow;
        })();
        ui.DeleteSpaceWindow = DeleteSpaceWindow;        
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
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
    nameTemplate: '<div class="admin-{0}-thumbnail">' + '<img src="{1}"/>' + '</div>' + '<div class="admin-{0}-description">' + '<h6>{2}</h6>' + '<p>{3}</p>' + '</div>',
    initComponent: function () {
        var me = this;
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
        if(plugin) {
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
        if(activeList.xtype === 'grid') {
            var store = activeList.getStore(), record;
            for(i = 0; i < keys.length; i++) {
                record = store.findRecord(this.keyField, keys[i]);
                if(record) {
                    selModel.select(record, keepExisting);
                }
            }
        }
    },
    deselect: function (key) {
        var activeList = this.getActiveList(), plugin = activeList.getPlugin('persistentGridSelection'), selModel = plugin ? plugin : activeList.getSelectionModel();
        if(!key || key === -1) {
            if(plugin) {
                plugin.clearSelection();
            } else {
                selModel.deselectAll();
            }
        } else {
            if(activeList.xtype === 'grid') {
                var record = activeList.getStore().findRecord(this.keyField, key);
                if(record) {
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
        activeList.removeAll();
    },
    refresh: function () {
        var activeList = this.getActiveList();
        var currentStore = activeList.store;
        if(!currentStore.loading) {
            if(activeList.xtype === 'grid') {
                currentStore.loadPage(currentStore.currentPage);
            }
        }
    }
});
Ext.define('Admin.view.TreeGridPanel', {
    extend: 'Admin.view.BaseTreeGridPanel',
    alias: 'widget.spaceTreeGrid',
    store: 'Admin.store.SpaceStore',
    border: false,
    keyField: 'name',
    activeItem: 'grid',
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
                text: 'Owner',
                dataIndex: 'owner',
                sortable: true
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
        var space = record.data;
        var activeListType = this.getActiveList().itemId;
        return Ext.String.format(this.nameTemplate, activeListType, space.iconUrl, value, space.name);
    },
    statusRenderer: function () {
        return "Online";
    },
    prettyDateRenderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        try  {
            if(parent && Ext.isFunction(parent['humane_date'])) {
                return parent['humane_date'](value);
            } else {
                return value;
            }
        } catch (e) {
            return value;
        }
    }
});
var admin;
(function (admin) {
    (function (ui) {
        var ContextMenu = (function () {
            function ContextMenu() {
                var menu = new Ext.menu.Menu();
                menu.addCls('admin-context-menu');
                menu.border = false;
                menu.shadow = false;
                menu.itemId = 'spaceContextMenu';
                var menuItemEdit = new Ext.menu.Item();
                menuItemEdit.text = 'Edit';
                menuItemEdit.iconCls = 'icon-edit';
                menuItemEdit.action = 'editSpace';
                menu.add(menuItemEdit);
                var menuItemOpen = new Ext.menu.Item();
                menuItemOpen.text = 'Open';
                menuItemOpen.iconCls = 'icon-view';
                menuItemOpen.action = 'viewSpace';
                menu.add(menuItemOpen);
                var menuItemDelete = new Ext.menu.Item();
                menuItemDelete.text = 'Delete';
                menuItemDelete.iconCls = 'icon-delete';
                menuItemDelete.action = 'deleteSpace';
                menu.add(menuItemDelete);
                this.ext = menu;
            }
            ContextMenu.prototype.showAt = function (x, y) {
                this.ext.showAt(x, y);
            };
            return ContextMenu;
        })();
        ui.ContextMenu = ContextMenu;        
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
Ext.define('Admin.view.wizard.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.spaceAdminWizardToolbar',
    cls: 'admin-toolbar',
    border: false,
    isNewGroup: true,
    defaults: {
        scale: 'medium'
    },
    initComponent: function () {
        this.items = [
            {
                text: 'Save',
                action: 'saveSpace',
                itemId: 'save',
                disabled: true
            }, 
            {
                text: 'Delete',
                disabled: this.isNew,
                action: 'deleteSpace'
            }, 
            {
                text: 'Duplicate',
                disabled: this.isNew
            }, 
            '->', 
            {
                text: 'Close',
                action: 'closeWizard'
            }
        ];
        this.callParent(arguments);
    }
});
Ext.define('Admin.view.wizard.SpaceStepPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.spaceStepPanel',
    stepTitle: 'Space',
    data: undefined,
    initComponent: function () {
        var templates = Ext.create('Ext.data.Store', {
            fields: [
                'code', 
                'name'
            ],
            data: [
                {
                    "code": "1",
                    "name": "Tpl1"
                }, 
                {
                    "code": "2",
                    "name": "Tpl2"
                }, 
                {
                    "code": "3",
                    "name": "Tpl3"
                }
            ]
        });
        this.items = [
            {
                xtype: 'fieldset',
                title: 'Template',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: [
                    {
                        xtype: 'combo',
                        fieldLabel: 'Space Template',
                        displayField: 'name',
                        valueField: 'code',
                        store: templates
                    }
                ]
            }
        ];
        this.callParent(arguments);
    }
});
Ext.define('Admin.view.wizard.WizardPanel', {
    extend: 'Admin.view.WizardPanel',
    alias: 'widget.spaceAdminWizardPanel',
    requires: [
        'Admin.view.WizardHeader', 
        'Admin.view.wizard.Toolbar', 
        'Admin.plugin.fileupload.PhotoUploadButton', 
        'Admin.view.wizard.SpaceStepPanel'
    ],
    border: 0,
    autoScroll: true,
    defaults: {
        border: false
    },
    initComponent: function () {
        var me = this;
        var headerData = me.resolveHeaderData(me.data);
        me.tbar = Ext.createByAlias('widget.spaceAdminWizardToolbar', {
            isNew: headerData.isNewSpace
        });
        this.callParent(arguments);
        var uploader = this.down('photoUploadButton');
        uploader.on('fileuploaded', me.photoUploaded, me);
    },
    resolveHeaderData: function (data) {
        var me = this;
        var iconUrl = 'resources/images/icons/128x128/default_space.png';
        var displayNameValue = '';
        var spaceName = '';
        if(data) {
            displayNameValue = me.data.get('displayName') || '';
            spaceName = me.data.get('name') || '';
            iconUrl = me.data.get('iconUrl');
        }
        return {
            'displayName': displayNameValue,
            'spaceName': spaceName,
            'isNewSpace': spaceName ? false : true,
            'iconUrl': iconUrl
        };
    },
    createSteps: function () {
        return [
            {
                xtype: 'spaceStepPanel',
                data: this.data
            }, 
            {
                stepTitle: 'Schemas'
            }, 
            {
                stepTitle: 'Modules'
            }, 
            {
                stepTitle: 'Templates'
            }, 
            {
                stepTitle: 'Security'
            }, 
            {
                stepTitle: 'Summary'
            }
        ];
    },
    createWizardHeader: function () {
        var wizardHeader = Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            pathConfig: {
                hidden: true
            },
            data: this.data
        });
        this.validateItems.push(wizardHeader);
        return wizardHeader;
    },
    createIcon: function () {
        var me = this;
        var headerData = me.resolveHeaderData(me.data);
        return {
            xtype: 'container',
            width: 110,
            height: 110,
            items: [
                {
                    xtype: 'photoUploadButton',
                    width: 110,
                    height: 110,
                    photoUrl: headerData.iconUrl,
                    title: "Space",
                    style: {
                        margin: '1px'
                    },
                    progressBarHeight: 6,
                    listeners: {
                        mouseenter: function () {
                            var imageToolTip = me.down('#imageToolTip');
                            imageToolTip.show();
                        },
                        mouseleave: function () {
                            var imageToolTip = me.down('#imageToolTip');
                            imageToolTip.hide();
                        }
                    }
                }, 
                {
                    styleHtmlContent: true,
                    height: 50,
                    border: 0,
                    itemId: 'imageToolTip',
                    style: {
                        top: '5px',
                        zIndex: 1001
                    },
                    cls: 'admin-image-upload-button-image-tip',
                    html: '<div class="x-tip x-tip-default x-layer" role="tooltip">' + '<div class="x-tip-anchor x-tip-anchor-top"></div>' + '<div class="x-tip-body  x-tip-body-default x-tip-body-default">' + 'Click to upload icon</div></div>',
                    listeners: {
                        afterrender: function (cmp) {
                            Ext.Function.defer(function () {
                                cmp.hide();
                            }, 10000);
                        }
                    }
                }
            ]
        };
    },
    createActionButton: function () {
        return {
            xtype: 'button',
            text: 'Save',
            action: 'saveSpace'
        };
    },
    getWizardHeader: function () {
        return this.down('wizardHeader');
    },
    getData: function () {
        var data = this.callParent();
        var headerData = this.getWizardHeader().getData();
        return Ext.apply(data, {
            displayName: headerData.displayName,
            spaceName: headerData.name
        });
    },
    photoUploaded: function (photoUploadButton, response) {
        var iconRef = response.items && response.items.length > 0 && response.items[0].id;
        this.addData({
            iconRef: iconRef
        });
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
            if(!item.popupPanel) {
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
                if(item.popupPanel.isHidden()) {
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
        if(this.iconCls || this.iconSrc) {
            this.items.push({
                xtype: 'image',
                width: 32,
                height: 32,
                margin: '0 12px 0 0',
                cls: this.iconCls,
                src: this.iconSrc
            });
        }
        if(this.text1 || this.text2) {
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
        if(this.closable !== false) {
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
        if(!me.activated && me.canActivate && me.rendered && !me.isDisabled() && me.isVisible()) {
            me.el.addCls(me.activeCls);
            me.focus();
            me.activated = true;
            me.fireEvent('activate', me);
        }
    },
    deactivate: function () {
        var me = this;
        if(me.activated) {
            me.el.removeCls(me.activeCls);
            me.blur();
            me.activated = false;
            me.fireEvent('deactivate', me);
        }
    },
    onClick: function (e) {
        var me = this;
        if(!me.href) {
            e.stopEvent();
        }
        if(me.disabled) {
            return;
        }
        Ext.callback(me.handler, me.scope || me, [
            me, 
            e
        ]);
        me.fireEvent('click', me, e);
        if(!me.hideOnClick) {
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
        if(me.disabled) {
            e.stopEvent();
            return;
        }
        item = (e.type === 'click') ? me.getItemFromEvent(e) : me.activeItem;
        if(item && item.isMenuItem && item.onClick(e) !== false) {
            if(me.fireEvent('click', me, item, e) !== false && this.tabPanel) {
                this.tabPanel.setActiveTab(item.card);
            }
            this.hide();
        }
    },
    onShow: function () {
        this.callParent(arguments);
        if(this.activeTab) {
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
        if(this.tabPanel) {
            this.tabPanel.remove(item.card);
        }
        if(this.getAllItems(false).length === 0) {
            this.hide();
        }
    },
    markActiveTab: function (item) {
        var me = this;
        var menuItem;
        if(me.isVisible()) {
            menuItem = me.el.down('.current-tab');
            if(menuItem) {
                menuItem.removeCls('current-tab');
            }
            if(item) {
                menuItem = me.down('#' + item.id);
                if(menuItem && menuItem.el) {
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
        }while(item && Ext.isDefined(item.getChildByElement) && item.getXType() !== 'topBarMenuItem');
        return item;
    },
    getAllItems: function (includeNonClosable) {
        var items = [];
        if(includeNonClosable === false) {
            items = items.concat(this.down('#editItems').query('topBarMenuItem'));
            items = items.concat(this.down('#viewItems').query('topBarMenuItem'));
        } else {
            items = items.concat(this.query('topBarMenuItem'));
        }
        return items;
    },
    addItems: function (items) {
        if(Ext.isEmpty(items)) {
            return;
        } else if(Ext.isObject(items)) {
            items = [].concat(items);
        }
        this.saveScrollState();
        var editItems = [];
        var viewItems = [];
        var nonClosableItems = [];
        Ext.Array.each(items, function (item) {
            if(item.closable === false) {
                nonClosableItems.push(item);
            } else if(item.editing) {
                editItems.push(item);
            } else {
                viewItems.push(item);
            }
        });
        var added = [];
        if(nonClosableItems.length > 0) {
            added = added.concat(this.down("#nonClosableItems").add(nonClosableItems));
        }
        if(editItems.length > 0) {
            added = added.concat(this.down('#editItems').add(editItems));
        }
        if(viewItems.length > 0) {
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
            if(item && item.closable !== false) {
                removed.push(editItems.remove(item));
            }
        });
        Ext.Array.each(viewItems.items.items, function (item) {
            if(item && item.closable !== false) {
                removed.push(viewItems.remove(item));
            }
        });
        if(includeNonClosable) {
            var nonClosableItems = this.down('#nonClosableItems');
            Ext.Array.each(nonClosableItems.items.items, function (item) {
                if(item && item.closable !== false) {
                    removed.push(nonClosableItems.remove(item));
                }
            });
        }
        this.updateTitles();
        return removed;
    },
    removeItems: function (items) {
        if(Ext.isEmpty(items)) {
            return;
        } else if(Ext.isObject(items)) {
            items = [].concat(items);
        }
        this.saveScrollState();
        var editItems = this.down('#editItems');
        var viewItems = this.down('#viewItems');
        var nonClosableItems = this.down('#nonClosableItems');
        var removed = [];
        Ext.Array.each(items, function (item) {
            if(item && item.closable !== false) {
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
        if(!me.rendered) {
            me.doAutoRender();
        }
        if(me.floating) {
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
        if(this.rendered && !this.hidden) {
            var dom = this.body.dom, state = this.scrollState;
            state.left = dom.scrollLeft;
            state.top = dom.scrollTop;
        }
    },
    restoreScrollState: function () {
        if(this.rendered && !this.hidden) {
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
        'Admin.view.AdminImageButton'
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
                if(me.tabPanel) {
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
                    icon: API.util.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'),
                    popupTpl: '<div class="title">User</div>' + '<div class="user-name">{userName}</div>' + '<div class="content">' + '<div class="column"><img src="{photoUrl}"/>' + '<button class="x-btn-red-small">Log Out</button>' + '</div>' + '<div class="column">' + '<span>{qName}</span>' + '<a href="#">View Profile</a>' + '<a href="#">Edit Profile</a>' + '<a href="#">Change User</a>' + '</div>' + '</div>',
                    popupData: {
                        userName: "Thomas Lund Sigdestad",
                        photoUrl: API.util.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'),
                        qName: 'system/tsi'
                    }
                }
            ]
        });
        this.items = [
            me.leftContainer, 
            me.rightContainer
        ];
        if(this.tabPanel) {
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
                    if(this.el) {
                        this.el.down('.title').setHTML(title);
                    }
                },
                setCount: function (count) {
                    if(this.el) {
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
        if(isInsideIframe) {
            window.parent['Ext'].getCmp('admin-home-main-container').toggleShowHide();
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
        if('tab-browse' === card.id) {
            buttonText = '';
        } else if(card.tab.iconClass) {
            iconClass = card.tab.iconClass;
        } else if(card.tab.editing) {
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
        if(this.tabPanel) {
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
        if(this.tabMenu && this.titleButton) {
            var tabCount = this.tabMenu.getAllItems(false).length;
            this.titleButton.setVisible(tabCount > 0);
            this.titleButton.setCount(tabCount);
            API.notify.updateAppTabCount(this.getApplicationId(), tabCount);
        }
    },
    getApplicationId: function () {
        var urlParamsString = document.URL.split('?'), urlParams = Ext.urlDecode(urlParamsString[urlParamsString.length - 1]);
        return urlParams.appId ? urlParams.appId.split('#')[0] : null;
    },
    getMenuItemIcon: function (card) {
        var icon;
        if(card.data && card.data instanceof Ext.data.Model) {
            icon = card.data.get('iconUrl') || card.data.get('image_url');
        }
        return icon;
    },
    getMenuItemDescription: function (card) {
        var desc;
        if(!card.isNew && card.data && card.data instanceof Ext.data.Model) {
            desc = card.data.get('path') || card.data.get('qualifiedName') || card.data.get('displayName');
        }
        if(!desc) {
            desc = card.title;
        }
        return desc;
    },
    getMenuItemDisplayName: function (card) {
        var desc;
        if(!card.isNew && card.data && card.data instanceof Ext.data.Model) {
            desc = card.data.get('displayName') || card.data.get('name');
        }
        if(!desc) {
            desc = card.title;
        }
        return desc;
    },
    setTitleButtonText: function (text) {
        this.titleButton.setTitle(text);
        var activeTab = this.titleButton.menu.activeTab;
        if(activeTab) {
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
        if(me.activeTab) {
            me.activeTab.tab.activate(true);
            me.tabBar.setActiveTab(me.activeTab.tab);
        }
    },
    addTab: function (item, index, requestConfig) {
        var me = this;
        var tab = this.getTabById(item.id);
        if(!tab) {
            tab = this.insert(index || this.items.length, item);
            if(requestConfig) {
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
        while(this.getTabCount() > 1) {
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
        if(item.isPanel) {
            if(me.removePanelHeader) {
                if(item.rendered) {
                    if(item.header) {
                        item.header.hide();
                    }
                } else {
                    item.header = false;
                }
            }
            if(item.isPanel && me.border) {
                item.setBorder(false);
            }
        }
    },
    doRemove: function (item, autoDestroy) {
        var me = this;
        if(me.destroying || me.items.getCount() === 1) {
            me.activeTab = null;
        } else if(me.activeTab === item) {
            var toActivate = me.tabBar.findNextActivatable(item.tab);
            if(toActivate) {
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
        if(!me.destroying) {
            me.tabBar.remove(item.tab);
        }
    }
});
Ext.define('Admin.view.BaseFilterPanel', {
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
        if(!Ext.isEmpty(this.title)) {
            this.originalTitle = this.title;
        }
        Ext.applyIf(this, {
            items: [],
            facetTpl: new Ext.XTemplate('<tpl for=".">', '<div class="admin-facet-group" name="{name}">', '<h2>{[values.displayName || values.name]}</h2>', '<tpl for="terms">{[this.updateFacetCount(values, parent)]}', '<tpl if="this.shouldShowTerm(values, parent)">', '<div class="admin-facet {[values.selected ? \'checked\' : \'\']}">', '<input type="checkbox" id="facet-{term}" value="{name}" class="admin-facet-cb" name="{parent.name}" {[values.selected ? \'checked="true"\' : \'\']} />', '<label for="facet-{key}" class="admin-facet-lbl"> {[values.displayName || values.name]} ({[this.getTermCount(values)]})</label>', '</div>', '</tpl>', '</tpl>', '</div>', '</tpl>', {
                updateFacetCount: function (term, facet) {
                    var isCriteria = me.updateCountCriteria == 'always' || (me.updateCountCriteria == 'query' && me.queryDirty);
                    var isStrategy = me.updateCountStrategy == 'all' || (me.updateCountStrategy == 'notlast' && me.lastFacetName != facet.name);
                    var isDefined = Ext.isDefined(me.facetCountMap[term.name]);
                    var isDirty = me.isDirty();
                    if(!isDirty || !isDefined || (isCriteria && isStrategy)) {
                        me.facetCountMap[term.name] = term.count;
                    }
                },
                shouldShowTerm: function (term, facet) {
                    return me.includeEmptyFacets == 'all' || (me.includeEmptyFacets == 'last' && (!me.lastFacetName || me.lastFacetName == facet.name)) || me.facetCountMap[term.name] > 0 || term.selected || this.isSelected(term, facet);
                },
                getTermCount: function (term) {
                    return me.facetCountMap[term.name];
                },
                isSelected: function (term, facet) {
                    var terms = me.selectedValues[facet.name];
                    if(terms) {
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
        if(this.includeSearch) {
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
        if(this.suspendEvents !== true) {
            if(event.getKey() === event.ENTER) {
                if(event.type === "keydown") {
                    this.fireEvent('search', this.getValues());
                }
            } else {
                var me = this;
                if(this.searchFilterTypingTimer !== null) {
                    window.clearTimeout(this.searchFilterTypingTimer);
                    this.searchFilterTypingTimer = null;
                }
                this.searchFilterTypingTimer = window.setTimeout(function () {
                    if(me.updateCountCriteria === 'query') {
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
        if(facet) {
            var cb = facet.down('input[type=checkbox]', true);
            var checked = cb.hasAttribute("checked");
            if(checked) {
                cb.removeAttribute("checked");
                facet.removeCls("checked");
            } else {
                cb.setAttribute("checked", "true");
                facet.addCls("checked");
            }
            var group = facet.up('.admin-facet-group', true);
            if(group) {
                this.lastFacetName = group.getAttribute('name');
            }
            this.search();
        }
        event.stopEvent();
        return true;
    },
    updateFacets: function (facets) {
        if(facets) {
            this.selectedValues = this.getValues();
            this.down('#facetContainer').update(facets);
            this.setValues(this.selectedValues);
        }
    },
    getValues: function () {
        var selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
        var values = {
        };
        if(this.searchField) {
            var query = this.searchField.getValue();
            if(Ext.String.trim(query).length > 0) {
                values[this.searchField.name] = query;
            }
        }
        Ext.Array.each(selectedCheckboxes, function (cb) {
            var oldValue = values[cb.name];
            if(Ext.isArray(oldValue)) {
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
        if(this.searchField) {
            this.searchField.setValue(values[this.searchField.name]);
        }
        var checkboxes = Ext.query('.admin-facet-group input[type=checkbox]', this.facetContainer.el.dom);
        var checkedCount = 0, facet;
        Ext.Array.each(checkboxes, function (cb) {
            var facet = Ext.fly(cb).up('.admin-facet');
            if(me.isValueChecked(cb.value, values)) {
                checkedCount++;
                cb.setAttribute('checked', 'true');
                facet.addCls('checked');
            } else {
                cb.removeAttribute('checked');
                facet.removeCls('checked');
            }
        });
        if(this.updateCountCriteria == 'query' && this.queryDirty && checkedCount === 0) {
            this.queryDirty = false;
        }
    },
    isValueChecked: function (value, values) {
        for(var facet in values) {
            if(values.hasOwnProperty(facet)) {
                var terms = [].concat(values[facet]);
                for(var i = 0; i < terms.length; i++) {
                    if(terms[i] === value) {
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
        if(this.facetContainer && this.facetContainer.el) {
            selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
        }
        if(this.searchField) {
            query = Ext.String.trim(this.searchField.getValue());
        }
        return selectedCheckboxes.length > 0 || query.length > 0;
    },
    reset: function () {
        if(this.fireEvent('reset', this.isDirty()) !== false) {
            if(this.searchField) {
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
        if(this.fireEvent('search', this.getValues()) !== false) {
            this.clearLink.el.setStyle('visibility', this.isDirty() ? 'visible' : 'hidden');
        }
    }
});
var admin;
(function (admin) {
    (function (ui) {
        var FilterPanel = (function () {
            function FilterPanel() {
                this.facetData = [
                    {
                        "name": 'Space',
                        "terms": [
                            {
                                "name": 'Public Web',
                                "key": 'public',
                                "count": 8
                            }, 
                            {
                                "name": 'Intranet',
                                "key": 'intra',
                                "count": 20
                            }
                        ]
                    }, 
                    {
                        "name": "Type",
                        "terms": [
                            {
                                "name": "Space",
                                "key": "space",
                                "count": 10
                            }, 
                            {
                                "name": "Part",
                                "key": "part",
                                "count": 80
                            }, 
                            {
                                "name": "Page Template",
                                "key": "template",
                                "count": 7
                            }
                        ]
                    }, 
                    {
                        "name": "Module",
                        "terms": [
                            {
                                "name": "Twitter Bootrstrap",
                                "key": "twitter",
                                "count": 0
                            }, 
                            {
                                "name": "Enonic",
                                "key": "enonic",
                                "count": 3
                            }, 
                            {
                                "name": "Foo",
                                "key": "foo",
                                "count": 6
                            }
                        ]
                    }
                ];
                this.facetTpl = '<tpl for=".">' + '<div class="admin-facet-group" name="{name}">' + '<h2>{[values.displayName || values.name]}</h2>' + '<tpl for="terms">{[this.updateFacetCount(values, parent)]}' + '<tpl if="this.shouldShowTerm(values, parent)">' + '<div class="admin-facet {[values.selected ? \'checked\' : \'\']}">' + '<input type="checkbox" id="facet-{term}" value="{name}" class="admin-facet-cb" name="{parent.name}" {[values.selected ? \'checked="true"\' : \'\']} />' + '<label for="facet-{key}" class="admin-facet-lbl"> {[values.displayName || values.name]} ({[this.getTermCount(values)]})</label>' + '</div>' + '</tpl>' + '</tpl>' + '</div>' + '</tpl>';
                var fp = this.ext = new Ext.panel.Panel();
                fp.cls = 'admin-filter';
                fp.header = false;
                fp.layout = {
                    type: 'vbox',
                    align: 'stretch'
                };
                fp.autoScroll = true;
                fp.split = true;
                fp.includeSearch = true;
                fp.includeEmptyFacets = 'none';
                fp.updateCountCriteria = 'always';
                fp.updateCountStrategy = 'notlast';
                fp.originalTitle = '';
                fp.facetData = this.facetData;
                fp.facetTpl = new Ext.XTemplate(this.facetTpl, {
                    updateFacetCount: function (term, facet) {
                        var isCriteria = fp.updateCountCriteria == 'always' || (fp.updateCountCriteria == 'query' && fp.queryDirty);
                        var isStrategy = fp.updateCountStrategy == 'all' || (fp.updateCountStrategy == 'notlast' && fp.lastFacetNafp != facet.nafp);
                        var isDefined = Ext.isDefined(fp.facetCountMap[term.name]);
                        var isDirty = fp.isDirty();
                        if(!isDirty || !isDefined || (isCriteria && isStrategy)) {
                            fp.facetCountMap[term.name] = term.count;
                        }
                    },
                    shouldShowTerm: function (term, facet) {
                        return fp.includeEmptyFacets == 'all' || (fp.includeEmptyFacets == 'last' && (!fp.lastFacetName || fp.lastFacetName == facet.name)) || fp.facetCountMap[term.name] > 0 || term.selected || this.isSelected(term, facet);
                    },
                    getTermCount: function (term) {
                        return fp.facetCountMap[term.name];
                    },
                    isSelected: function (term, facet) {
                        var terms = fp.selectedValues[facet.name];
                        if(terms) {
                            return Ext.Array.contains(terms, term.name);
                        }
                        return false;
                    }
                });
                fp.facetContainer = this.createFacetContainer();
                fp.insert(0, fp.facetContainer);
                fp.clearLink = this.createClearLink();
                fp.insert(0, fp.clearLink);
                fp.facetCountMap = [];
                if(fp.includeSearch) {
                    fp.searchField = this.createSearchField();
                    fp.insert(0, fp.searchField);
                }
                fp.updateFacets = function (facets) {
                    if(facets) {
                        this.selectedValues = this.getValues();
                        this.down('#facetContainer').update(facets);
                        this.setValues(this.selectedValues);
                    }
                };
                fp.getValues = function () {
                    var selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
                    var values = {
                    };
                    if(this.searchField) {
                        var query = this.searchField.getValue();
                        if(Ext.String.trim(query).length > 0) {
                            values[this.searchField.name] = query;
                        }
                    }
                    Ext.Array.each(selectedCheckboxes, function (cb) {
                        var oldValue = values[cb.name];
                        if(Ext.isArray(oldValue)) {
                            oldValue.push(cb.value);
                        } else {
                            values[cb.name] = [
                                cb.value
                            ];
                        }
                    });
                    return values;
                };
                fp.setValues = function (values) {
                    var me = this;
                    if(this.searchField) {
                        this.searchField.setValue(values[this.searchField.name]);
                    }
                    var checkboxes = Ext.query('.admin-facet-group input[type=checkbox]', this.facetContainer.el.dom);
                    var checkedCount = 0, facet;
                    Ext.Array.each(checkboxes, function (cb) {
                        var facet = Ext.fly(cb).up('.admin-facet');
                        if(me.isValueChecked(cb.value, values)) {
                            checkedCount++;
                            cb.setAttribute('checked', 'true');
                            facet.addCls('checked');
                        } else {
                            cb.removeAttribute('checked');
                            facet.removeCls('checked');
                        }
                    });
                    if(this.updateCountCriteria == 'query' && this.queryDirty && checkedCount === 0) {
                        this.queryDirty = false;
                    }
                };
                fp.isValueChecked = function (value, values) {
                    for(var facet in values) {
                        if(values.hasOwnProperty(facet)) {
                            var terms = [].concat(values[facet]);
                            for(var i = 0; i < terms.length; i++) {
                                if(terms[i] === value) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                };
                fp.isDirty = function () {
                    var selectedCheckboxes = [];
                    var query = '';
                    if(this.facetContainer && this.facetContainer.el) {
                        selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
                    }
                    if(this.searchField) {
                        query = Ext.String.trim(this.searchField.getValue());
                    }
                    return selectedCheckboxes.length > 0 || query.length > 0;
                };
                fp.search = function () {
                    if(this.fireEvent('search', this.getValues()) !== false) {
                        this.clearLink.el.setStyle('visibility', this.isDirty() ? 'visible' : 'hidden');
                    }
                };
                fp.addEvents('search', 'reset');
            }
            FilterPanel.prototype.createFacetContainer = function () {
                var fp = this.ext;
                var onFacetClicked = function (event, target, opts) {
                    target = Ext.fly(target);
                    var facet = target.hasCls('admin-facet') ? target : target.up('.admin-facet');
                    if(facet) {
                        var cb = facet.down('input[type=checkbox]', true);
                        var checked = cb.hasAttribute("checked");
                        if(checked) {
                            cb.removeAttribute("checked");
                            facet.removeCls("checked");
                        } else {
                            cb.setAttribute("checked", "true");
                            facet.addCls("checked");
                        }
                        var group = facet.up('.admin-facet-group', true);
                        if(group) {
                            this.lastFacetName = group.getAttribute('name');
                        }
                        this.search();
                    }
                    event.stopEvent();
                    return true;
                };
                var facetContainer = new Ext.Component();
                facetContainer.on('afterrender', function (cmp) {
                    cmp.el.on('click', onFacetClicked, fp, {
                        delegate: '.admin-facet'
                    });
                });
                facetContainer.itemId = 'facetContainer';
                facetContainer.tpl = fp.facetTpl;
                facetContainer.data = fp.facetData;
                return facetContainer;
            };
            FilterPanel.prototype.createClearLink = function () {
                var reset = function () {
                    if(this.fireEvent('reset', this.isDirty()) !== false) {
                        if(this.searchField) {
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
                };
                var clearLink = new Ext.Component();
                clearLink.html = '<a href="javascript:;">Clear filter</a>';
                clearLink.on('click', reset, this.ext, {
                    element: 'el'
                });
                clearLink.on('afterrender', function (cmp) {
                    cmp.el.setStyle('visibility', 'hidden');
                });
                return clearLink;
            };
            FilterPanel.prototype.createSearchField = function () {
                var onKeyPressed = function (field, event, opts) {
                    if(this.suspendEvents !== true) {
                        if(event.getKey() === event.ENTER) {
                            if(event.type === "keydown") {
                                this.fireEvent('search', this.getValues());
                            }
                        } else {
                            var me = this;
                            if(this.searchFilterTypingTimer !== null) {
                                window.clearTimeout(this.searchFilterTypingTimer);
                                this.searchFilterTypingTimer = null;
                            }
                            this.searchFilterTypingTimer = window.setTimeout(function () {
                                if(me.updateCountCriteria === 'query') {
                                    me.queryDirty = true;
                                }
                                me.lastFacetName = undefined;
                                me.search();
                            }, 500);
                        }
                    }
                };
                var searchField = new Ext.form.field.Text();
                searchField.cls = 'admin-search-field';
                searchField.enableKeyEvents = true;
                searchField.bubbleEvents = [
                    'specialkey'
                ];
                searchField.itemId = 'filterText';
                searchField.margin = '0 0 10 0';
                searchField.name = 'query';
                searchField.emptyText = 'Search';
                searchField.on('specialkey', onKeyPressed, this.ext);
                searchField.on('keypress', onKeyPressed, this.ext);
                return searchField;
            };
            FilterPanel.prototype.getExtEl = function () {
                return this.ext;
            };
            return FilterPanel;
        })();
        ui.FilterPanel = FilterPanel;        
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var BrowseToolbar = (function () {
            function BrowseToolbar(region) {
                var tb = this.ext = new Ext.toolbar.Toolbar();
                tb.cls = 'admin-toolbar';
                tb.border = true;
                tb.itemId = 'spaceBrowseToolbar';
                if(region) {
                    tb.region = region;
                }
                var newButton = new Ext.button.Button();
                newButton.text = 'New';
                newButton.action = 'newSpace';
                newButton.scale = 'medium';
                newButton.iconAlign = 'top';
                newButton.minWidth = 64;
                tb.add(newButton);
                var editButton = new Ext.button.Button();
                editButton.text = 'Edit';
                editButton.disabled = true;
                editButton.action = 'editSpace';
                editButton.scale = 'medium';
                editButton.iconAlign = 'top';
                editButton.minWidth = 64;
                tb.add(editButton);
                var openButton = new Ext.button.Button();
                openButton.text = 'Open';
                openButton.disabled = true;
                openButton.action = 'viewSpace';
                openButton.scale = 'medium';
                openButton.iconAlign = 'top';
                openButton.minWidth = 64;
                tb.add(openButton);
                var deleteButton = new Ext.button.Button();
                deleteButton.text = 'Delete';
                deleteButton.disabled = true;
                deleteButton.action = 'deleteSpace';
                deleteButton.scale = 'medium';
                deleteButton.iconAlign = 'top';
                deleteButton.minWidth = 64;
                deleteButton.setHandler(function () {
                    new APP.event.DeletePromptEvent(components.gridPanel.getSelection()).fire();
                });
                tb.add(deleteButton);
            }
            return BrowseToolbar;
        })();
        ui.BrowseToolbar = BrowseToolbar;        
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
Ext.define('Admin.controller.Controller', {
    extend: 'Ext.app.Controller',
    stores: [
        'Admin.store.SpaceStore'
    ],
    models: [
        'Admin.model.SpaceModel'
    ],
    requires: [
        'Admin.lib.RemoteService'
    ],
    init: function () {
        var me = this;
        me.control({
        });
    },
    generateTabId: function (space, isEdit) {
        return 'tab-' + (isEdit ? 'edit-' : 'preview-') + space.get('name');
    },
    showNewSpaceWindow: function () {
        var tabs = this.getCmsTabPanel();
        var tabItem = {
            id: 'new-space',
            xtype: 'spaceAdminWizardPanel',
            editing: true,
            title: 'New Space'
        };
        tabs.addTab(tabItem);
    },
    viewSpace: function (space) {
        space = this.validateSpace(space);
        var me = this;
        var tabs = this.getCmsTabPanel();
        var activeTab = tabs.setActiveTab(me.generateTabId(space, true));
        if(!activeTab) {
            var tabItem = new admin.ui.SpaceDetailPanel();
            tabs.addTab(tabItem);
        }
    },
    editSpace: function (space) {
        space = this.validateSpace(space);
        var me = this;
        var tabs = this.getCmsTabPanel();
        tabs.el.mask();
        Admin.lib.RemoteService.space_get({
            "spaceName": [
                space.get('name')
            ]
        }, function (r) {
            tabs.el.unmask();
            if(r) {
                var tabItem = {
                    id: me.generateTabId(space, true),
                    editing: true,
                    xtype: 'spaceAdminWizardPanel',
                    data: space,
                    title: space.get('displayName')
                };
                var index = tabs.items.indexOfKey(me.generateTabId(space, false));
                if(index >= 0) {
                    tabs.remove(index);
                }
                tabs.addTab(tabItem, index >= 0 ? index : undefined, undefined);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to retrieve space.");
            }
        });
    },
    deleteSpace: function (space) {
        space = this.validateSpace(space);
        this.showDeleteSpaceWindow([].concat(space));
    },
    showDeleteSpaceWindow: function (spaceArray) {
        var win = this.getDeleteSpaceWindow();
        win.setModel(spaceArray);
        win.doShow();
    },
    validateSpace: function (space) {
        if(!space) {
            var showPanel = this.getSpaceTreeGridPanel();
            return showPanel.getSelection()[0];
        }
        return space;
    },
    updateDetailPanel: function (selected) {
        this.getSpaceDetailPanel().setData(selected);
    },
    updateToolbarButtons: function (selected) {
        var enable = selected && selected.length > 0;
        var toolbar = this.getSpaceBrowseToolbar();
        var buttons = Ext.ComponentQuery.query('button[action=viewSpace], ' + 'button[action=editSpace], ' + 'button[action=deleteSpace]', toolbar);
        Ext.Array.each(buttons, function (button) {
            button.setDisabled(!enable);
        });
    },
    getSpaceFilterPanel: function () {
        return Ext.ComponentQuery.query('spaceFilter')[0];
    },
    getSpaceBrowseToolbar: function () {
        return Ext.ComponentQuery.query('spaceBrowseToolbar')[0];
    },
    getSpaceTreeGridPanel: function () {
        return Ext.ComponentQuery.query('spaceTreeGrid')[0];
    },
    getSpaceDetailPanel: function () {
        return components.detailPanel;
    },
    deleteSpaceWindow: null,
    getDeleteSpaceWindow: function () {
        var win = this.deleteSpaceWindow;
        if(!win) {
            win = new admin.ui.DeleteSpaceWindow();
        }
        return win;
    },
    getCmsTabPanel: function () {
        return Ext.ComponentQuery.query('cmsTabPanel')[0];
    },
    getTopBar: function () {
        return Ext.ComponentQuery.query('topBar')[0];
    },
    getMainViewport: function () {
        var parent = window.parent || window;
        return parent['Ext'].ComponentQuery.query('#mainViewport')[0];
    }
});
Ext.define('Admin.controller.SpaceController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    views: [],
    init: function () {
    },
    remoteCreateOrUpdateSpace: function (spaceParams, callback) {
        Admin.lib.RemoteService.space_createOrUpdate(spaceParams, function (r) {
            if(r && r.success) {
                callback(r.created, r.updated);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "An unexpected error occurred.");
            }
        });
    },
    remoteDeleteSpace: function (spaces, callback) {
        var me = this;
        var spaceNames = Ext.Array.map([].concat(spaces), function (item) {
            return item.get('name');
        });
        Admin.lib.RemoteService.space_delete({
            "spaceName": spaceNames
        }, function (r) {
            if(r) {
                callback.call(me, r.success, r);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to delete space.");
            }
        });
    }
});
Ext.define('Admin.controller.FilterPanelController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    views: [],
    init: function () {
        this.control({
            'spaceFilter': {
                search: this.doSearch,
                reset: this.doReset
            }
        });
    },
    doSearch: function (values) {
        this.getCmsTabPanel().setActiveTab(0);
        var treeGrid = this.getSpaceTreeGridPanel();
        treeGrid.setRemoteSearchParams(this.getStoreParamsFromFilter(values));
        treeGrid.refresh();
        var selection = treeGrid.getSelection();
        this.updateDetailPanel(selection);
        this.updateToolbarButtons(selection);
    },
    doReset: function (dirty) {
        if(!dirty) {
            return false;
        }
        var treeGrid = this.getSpaceTreeGridPanel();
        treeGrid.setRemoteSearchParams({
        });
        treeGrid.refresh();
        return true;
    },
    getStoreParamsFromFilter: function (values) {
        return {
        };
    }
});
Ext.define('Admin.controller.GridPanelController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    views: [
        'Admin.view.TreeGridPanel'
    ],
    contextMenu: null,
    init: function () {
        this.control({
            'spaceTreeGrid gridpanel, spaceTreeGrid treepanel': {
                selectionchange: this.onGridSelectionChange,
                itemcontextmenu: this.showContextMenu,
                itemdblclick: function (grid, record) {
                    this.editSpace(record);
                }
            },
            '#spaceContextMenu *[action=deleteSpace]': {
                click: function (el, e) {
                    this.deleteSpace();
                }
            },
            '#spaceContextMenu *[action=editSpace]': {
                click: function (el, e) {
                    this.editSpace();
                }
            },
            '#spaceContextMenu *[action=viewSpace]': {
                click: function (el, e) {
                    this.viewSpace();
                }
            }
        });
    },
    onGridSelectionChange: function (selModel, selected, opts) {
        this.updateDetailPanel(selected);
        this.updateToolbarButtons(selected);
    },
    showContextMenu: function (view, rec, node, index, e) {
        e.stopEvent();
        var xy = e.getXY();
        this.getContextMenu().showAt(xy[0], xy[1]);
        return false;
    },
    getContextMenu: function () {
        if(!this.contextMenu) {
            this.contextMenu = new admin.ui.ContextMenu();
        }
        return this.contextMenu;
    }
});
Ext.define('Admin.controller.BrowseToolbarController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    views: [
        'Admin.view.wizard.WizardPanel'
    ],
    init: function () {
    },
    viewSelectedSpaces: function () {
        var selection = this.getSpaceTreeGridPanel().getSelection();
        for(var i = 0; i < selection.length; i++) {
            this.viewSpace(selection[i]);
        }
    },
    editSelectedSpaces: function () {
        var selection = this.getSpaceTreeGridPanel().getSelection();
        for(var i = 0; i < selection.length; i++) {
            this.editSpace(selection[i]);
        }
    },
    deleteSelectedSpaces: function () {
        var selection = this.getSpaceTreeGridPanel().getSelection();
        this.showDeleteSpaceWindow(selection);
    }
});
Ext.define('Admin.controller.DetailPanelController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    init: function () {
        this.control({
            'spaceDetail': {
                deselect: this.deselectRecord,
                clearselection: this.clearSelection
            }
        });
    },
    deselectRecord: function (key) {
        this.getSpaceTreeGridPanel().deselect(key);
    },
    clearSelection: function () {
        this.getSpaceTreeGridPanel().deselect(-1);
    }
});
Ext.define('Admin.controller.DetailToolbarController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    views: [
        'Admin.view.DetailToolbar'
    ],
    init: function () {
        this.control({
            'spaceDetailToolbar *[action=closeSpace]': {
                click: function (el, e) {
                    this.getCmsTabPanel().getActiveTab().close();
                }
            },
            'spaceDetailToolbar *[action=editSpace]': {
                click: function (el, e) {
                    var space = el.up('spaceDetail').getData();
                    this.editSpace(space);
                }
            },
            'spaceDetailToolbar *[action=deleteSpace]': {
                click: function (el, e) {
                    var space = el.up('spaceDetail').getData();
                    this.showDeleteSpaceWindow(space);
                }
            }
        });
    }
});
Ext.define('Admin.controller.DialogWindowController', {
    extend: 'Admin.controller.SpaceController',
    stores: [],
    models: [],
    views: [],
    init: function () {
        this.control({
            'deleteSpaceWindow *[action=deleteSpace]': {
                click: this.deleteSpace
            }
        });
    },
    deleteSpace: function () {
        var win = this.getDeleteSpaceWindow(), space = win.data, me = this;
        var onDelete = function (success, details) {
            win.close();
            if(success && details.deleted) {
                API.notify.showFeedback(Ext.isArray(space) && space.length > 1 ? space.length + ' spaces were deleted' : '1 space was deleted');
            } else {
                var message = details.reason;
                API.notify.showFeedback(message);
            }
            me.getSpaceTreeGridPanel().refresh();
        };
        this.remoteDeleteSpace(space, onDelete);
    }
});
Ext.define('Admin.controller.WizardController', {
    extend: 'Admin.controller.SpaceController',
    stores: [],
    models: [],
    views: [],
    init: function () {
        var me = this;
        me.control({
            'spaceAdminWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            'spaceAdminWizardPanel *[action=saveSpace]': {
                click: function (el) {
                    me.saveSpace(el.up('spaceAdminWizardPanel'), false);
                }
            },
            'spaceAdminWizardPanel *[action=deleteSpace]': {
                click: function () {
                    this.deleteSpace(this.getWizardTab());
                }
            },
            'spaceAdminWizardPanel wizardHeader': {
                displaynamechange: function (newVal, oldVal) {
                    this.getTopBar().setTitleButtonText(newVal);
                }
            },
            'spaceAdminWizardPanel': {
                'validitychange': function (wizard, isValid) {
                    this.updateWizardToolbarButtons(wizard.isWizardDirty, isValid);
                },
                'dirtychange': function (wizard, isDirty) {
                    this.updateWizardToolbarButtons(isDirty, wizard.isWizardValid);
                }
            }
        });
    },
    updateWizardToolbarButtons: function (isDirty, isValid) {
        var toolbar = this.getWizardToolbar();
        var save = toolbar.down('button[action=saveSpace]');
        save.setDisabled(!isDirty || !isValid);
    },
    closeWizard: function (el, e) {
        var tab = this.getWizardTab();
        var spaceWizard = this.getWizardPanel();
        if(spaceWizard.isWizardDirty) {
            Ext.Msg.confirm('Close wizard', 'There are unsaved changes, do you want to close it anyway ?', function (answer) {
                if('yes' === answer) {
                    tab.close();
                }
            });
        } else {
            tab.close();
        }
    },
    saveSpace: function (spaceWizard, closeWizard) {
        var me = this;
        var spaceWizardData = spaceWizard.getData();
        var displayName = spaceWizardData.displayName;
        var spaceName = spaceWizardData.spaceName;
        var iconReference = spaceWizardData.iconRef;
        var spaceModel = spaceWizard.data;
        var originalSpaceName = spaceModel && spaceModel.get ? spaceModel.get('name') : spaceModel.name;
        var spaceParams = {
            spaceName: originalSpaceName || spaceName,
            displayName: displayName,
            iconReference: iconReference,
            newSpaceName: (originalSpaceName !== spaceName) ? spaceName : undefined
        };
        var onUpdateSpaceSuccess = function (created, updated) {
            if(created || updated) {
                if(closeWizard) {
                    me.getWizardTab().close();
                }
                API.notify.showFeedback('Space "' + spaceName + '" was saved');
                me.getSpaceTreeGridPanel().refresh();
            }
        };
        this.remoteCreateOrUpdateSpace(spaceParams, onUpdateSpaceSuccess);
    },
    deleteSpace: function (wizard) {
        var me = this;
        var space = wizard.data;
        var onDeleteSpaceSuccess = function (success, failures) {
            if(success) {
                wizard.close();
                API.notify.showFeedback('Space was deleted');
            }
        };
        this.remoteDeleteSpace(space, onDeleteSpaceSuccess);
    },
    getWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },
    getWizardPanel: function () {
        return this.getWizardTab();
    },
    getWizardToolbar: function () {
        return Ext.ComponentQuery.query('spaceAdminWizardToolbar', this.getWizardTab())[0];
    }
});
var APP;
(function (APP) {
    APP.id = 'space-manager';
})(APP || (APP = {}));
var components;
(function (components) {
    components.detailPanel;
    components.gridPanel;
})(components || (components = {}));
Ext.application({
    name: 'spaceAdmin',
    controllers: [
        'Admin.controller.FilterPanelController', 
        'Admin.controller.GridPanelController', 
        'Admin.controller.BrowseToolbarController', 
        'Admin.controller.DetailPanelController', 
        'Admin.controller.DetailToolbarController', 
        'Admin.controller.DialogWindowController', 
        'Admin.controller.WizardController'
    ],
    stores: [
        'Admin.store.SpaceStore'
    ],
    launch: function () {
        var toolbar = new admin.ui.BrowseToolbar('north');
        var grid = components.gridPanel = new Admin.view.TreeGridPanel();
        grid.region = 'center';
        grid.flex = 1;
        var detail = components.detailPanel = new admin.ui.SpaceDetailPanel('south');
        var center = new Ext.container.Container();
        center.region = 'center';
        center.layout = 'border';
        center.add(detail.ext);
        center.add(grid);
        center.add(toolbar.ext);
        var west = new admin.ui.FilterPanel().getExtEl();
        west.region = 'west';
        west.width = 200;
        var p = new Ext.panel.Panel();
        p.id = 'tab-browse';
        p.title = 'Browse';
        p.closable = false;
        p.border = false;
        p.layout = 'border';
        p.tabConfig = {
            hidden: true
        };
        p.add(center);
        p.add(west);
        var tabPanel = new Admin.view.TabPanel();
        tabPanel.appName = 'Space Admin';
        tabPanel.appIconCls = 'icon-metro-space-admin-24';
        tabPanel.add(p);
        var wp = new Ext.container.Viewport();
        wp.layout = 'fit';
        wp.cls = 'admin-viewport';
        wp.add(tabPanel);
        new admin.ui.DeleteSpaceWindow();
    }
});
//@ sourceMappingURL=all.js.map
