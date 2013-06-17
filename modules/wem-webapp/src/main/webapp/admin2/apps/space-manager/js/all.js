var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var app_event;
(function (app_event) {
    var DeletedEvent = (function (_super) {
        __extends(DeletedEvent, _super);
        function DeletedEvent() {
                _super.call(this, 'deleted');
        }
        DeletedEvent.on = function on(handler) {
            api_event.onEvent('deleted', handler);
        };
        return DeletedEvent;
    })(api_event.Event);
    app_event.DeletedEvent = DeletedEvent;    
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var BaseSpaceModelEvent = (function (_super) {
        __extends(BaseSpaceModelEvent, _super);
        function BaseSpaceModelEvent(name, model) {
            this.model = model;
                _super.call(this, name);
        }
        BaseSpaceModelEvent.prototype.getModels = function () {
            return this.model;
        };
        return BaseSpaceModelEvent;
    })(api_event.Event);
    app_event.BaseSpaceModelEvent = BaseSpaceModelEvent;    
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var DeletePromptEvent = (function (_super) {
        __extends(DeletePromptEvent, _super);
        function DeletePromptEvent(model) {
                _super.call(this, 'deletePrompt', model);
        }
        DeletePromptEvent.on = function on(handler) {
            api_event.onEvent('deletePrompt', handler);
        };
        return DeletePromptEvent;
    })(app_event.BaseSpaceModelEvent);
    app_event.DeletePromptEvent = DeletePromptEvent;    
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var GridSelectionChangeEvent = (function (_super) {
        __extends(GridSelectionChangeEvent, _super);
        function GridSelectionChangeEvent(model) {
                _super.call(this, 'gridChange', model);
        }
        GridSelectionChangeEvent.on = function on(handler) {
            api_event.onEvent('gridChange', handler);
        };
        return GridSelectionChangeEvent;
    })(app_event.BaseSpaceModelEvent);
    app_event.GridSelectionChangeEvent = GridSelectionChangeEvent;    
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var GridDeselectEvent = (function (_super) {
        __extends(GridDeselectEvent, _super);
        function GridDeselectEvent(model) {
                _super.call(this, 'removeFromGrid', model);
        }
        GridDeselectEvent.on = function on(handler) {
            api_event.onEvent('removeFromGrid', handler);
        };
        return GridDeselectEvent;
    })(app_event.BaseSpaceModelEvent);
    app_event.GridDeselectEvent = GridDeselectEvent;    
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var ShowContextMenuEvent = (function (_super) {
        __extends(ShowContextMenuEvent, _super);
        function ShowContextMenuEvent(x, y) {
            this.x = x;
            this.y = y;
                _super.call(this, 'showContextMenu');
        }
        ShowContextMenuEvent.prototype.getX = function () {
            return this.x;
        };
        ShowContextMenuEvent.prototype.getY = function () {
            return this.y;
        };
        ShowContextMenuEvent.on = function on(handler) {
            api_event.onEvent('showContextMenu', handler);
        };
        return ShowContextMenuEvent;
    })(api_event.Event);
    app_event.ShowContextMenuEvent = ShowContextMenuEvent;    
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var NewSpaceEvent = (function (_super) {
        __extends(NewSpaceEvent, _super);
        function NewSpaceEvent() {
                _super.call(this, 'newSpace');
        }
        NewSpaceEvent.on = function on(handler) {
            api_event.onEvent('newSpace', handler);
        };
        return NewSpaceEvent;
    })(api_event.Event);
    app_event.NewSpaceEvent = NewSpaceEvent;    
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var OpenSpaceEvent = (function (_super) {
        __extends(OpenSpaceEvent, _super);
        function OpenSpaceEvent(model) {
                _super.call(this, 'openSpace', model);
        }
        OpenSpaceEvent.on = function on(handler) {
            api_event.onEvent('openSpace', handler);
        };
        return OpenSpaceEvent;
    })(app_event.BaseSpaceModelEvent);
    app_event.OpenSpaceEvent = OpenSpaceEvent;    
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var EditSpaceEvent = (function (_super) {
        __extends(EditSpaceEvent, _super);
        function EditSpaceEvent(model) {
                _super.call(this, 'editSpaceEvent', model);
        }
        EditSpaceEvent.on = function on(handler) {
            api_event.onEvent('editSpaceEvent', handler);
        };
        return EditSpaceEvent;
    })(app_event.BaseSpaceModelEvent);
    app_event.EditSpaceEvent = EditSpaceEvent;    
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var SaveSpaceEvent = (function (_super) {
        __extends(SaveSpaceEvent, _super);
        function SaveSpaceEvent() {
                _super.call(this, 'saveSpace');
        }
        SaveSpaceEvent.on = function on(handler) {
            api_event.onEvent('saveSpace', handler);
        };
        return SaveSpaceEvent;
    })(api_event.Event);
    app_event.SaveSpaceEvent = SaveSpaceEvent;    
})(app_event || (app_event = {}));
var app;
(function (app) {
    var SpaceContext = (function () {
        function SpaceContext() {
            var _this = this;
            app_event.GridSelectionChangeEvent.on(function (event) {
                _this.selectedSpaces = event.getModels();
            });
        }
        SpaceContext.init = function init() {
            return SpaceContext.context = new SpaceContext();
        };
        SpaceContext.get = function get() {
            return SpaceContext.context;
        };
        SpaceContext.prototype.getSelectedSpaces = function () {
            return this.selectedSpaces;
        };
        return SpaceContext;
    })();
    app.SpaceContext = SpaceContext;    
})(app || (app = {}));
var app;
(function (app) {
    var NewSpaceAction = (function (_super) {
        __extends(NewSpaceAction, _super);
        function NewSpaceAction() {
                _super.call(this, "New");
            this.addExecutionListener(function () {
                new app_event.NewSpaceEvent().fire();
            });
        }
        return NewSpaceAction;
    })(api_ui.Action);
    app.NewSpaceAction = NewSpaceAction;    
    var OpenSpaceAction = (function (_super) {
        __extends(OpenSpaceAction, _super);
        function OpenSpaceAction() {
                _super.call(this, "Open");
            this.setEnabled(false);
            this.addExecutionListener(function () {
                new app_event.OpenSpaceEvent(app.SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
        return OpenSpaceAction;
    })(api_ui.Action);
    app.OpenSpaceAction = OpenSpaceAction;    
    var EditSpaceAction = (function (_super) {
        __extends(EditSpaceAction, _super);
        function EditSpaceAction() {
                _super.call(this, "Edit");
            this.setEnabled(false);
            this.addExecutionListener(function () {
                new app_event.EditSpaceEvent(app.SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
        return EditSpaceAction;
    })(api_ui.Action);
    app.EditSpaceAction = EditSpaceAction;    
    var DeleteSpaceAction = (function (_super) {
        __extends(DeleteSpaceAction, _super);
        function DeleteSpaceAction() {
                _super.call(this, "Delete");
            this.setEnabled(false);
            this.addExecutionListener(function () {
                new app_event.DeletePromptEvent(app.SpaceContext.get().getSelectedSpaces()).fire();
            });
        }
        return DeleteSpaceAction;
    })(api_ui.Action);
    app.DeleteSpaceAction = DeleteSpaceAction;    
    var SpaceActions = (function () {
        function SpaceActions() { }
        SpaceActions.NEW_SPACE = new NewSpaceAction();
        SpaceActions.OPEN_SPACE = new OpenSpaceAction();
        SpaceActions.EDIT_SPACE = new EditSpaceAction();
        SpaceActions.DELETE_SPACE = new DeleteSpaceAction();
        SpaceActions.init = function init() {
            app_event.GridSelectionChangeEvent.on(function (event) {
                var spaces = event.getModels();
                if(spaces.length <= 0) {
                    SpaceActions.NEW_SPACE.setEnabled(true);
                    SpaceActions.OPEN_SPACE.setEnabled(false);
                    SpaceActions.EDIT_SPACE.setEnabled(false);
                    SpaceActions.DELETE_SPACE.setEnabled(false);
                } else if(spaces.length == 1) {
                    SpaceActions.NEW_SPACE.setEnabled(false);
                    SpaceActions.OPEN_SPACE.setEnabled(true);
                    SpaceActions.EDIT_SPACE.setEnabled(spaces[0].data.editable);
                    SpaceActions.DELETE_SPACE.setEnabled(spaces[0].data.deletable);
                } else {
                    SpaceActions.NEW_SPACE.setEnabled(false);
                    SpaceActions.OPEN_SPACE.setEnabled(true);
                    SpaceActions.EDIT_SPACE.setEnabled(SpaceActions.anyEditable(spaces));
                    SpaceActions.DELETE_SPACE.setEnabled(SpaceActions.anyDeleteable(spaces));
                }
            });
        };
        SpaceActions.anyEditable = function anyEditable(spaces) {
            for(var i in spaces) {
                var space = spaces[i];
                if(space.data.editable) {
                    return true;
                }
            }
            return false;
        };
        SpaceActions.anyDeleteable = function anyDeleteable(spaces) {
            for(var i in spaces) {
                var space = spaces[i];
                if(space.data.deletable) {
                    return true;
                }
            }
            return false;
        };
        return SpaceActions;
    })();
    app.SpaceActions = SpaceActions;    
})(app || (app = {}));
var app;
(function (app) {
    var SpaceAppBrowsePanel = (function (_super) {
        __extends(SpaceAppBrowsePanel, _super);
        function SpaceAppBrowsePanel() {
            var toolbar = new app_ui.BrowseToolbar();
            var grid = components.gridPanel = new app_ui.TreeGridPanel('center');
            var detail = components.detailPanel = new app_ui.SpaceDetailPanel();
            var filterPanel = new app_ui.FilterPanel({
                region: 'west',
                width: 200
            }).getExtEl();
                _super.call(this, toolbar, grid, detail, filterPanel);
        }
        return SpaceAppBrowsePanel;
    })(api.AppBrowsePanel);
    app.SpaceAppBrowsePanel = SpaceAppBrowsePanel;    
})(app || (app = {}));
var app_wizard;
(function (app_wizard) {
    var SaveSpaceAction = (function (_super) {
        __extends(SaveSpaceAction, _super);
        function SaveSpaceAction() {
                _super.call(this, "Save");
            this.addExecutionListener(function () {
            });
        }
        return SaveSpaceAction;
    })(api_ui.Action);
    app_wizard.SaveSpaceAction = SaveSpaceAction;    
    var DuplicateSpaceAction = (function (_super) {
        __extends(DuplicateSpaceAction, _super);
        function DuplicateSpaceAction() {
                _super.call(this, "Duplicate");
            this.addExecutionListener(function () {
            });
        }
        return DuplicateSpaceAction;
    })(api_ui.Action);
    app_wizard.DuplicateSpaceAction = DuplicateSpaceAction;    
    var DeleteSpaceAction = (function (_super) {
        __extends(DeleteSpaceAction, _super);
        function DeleteSpaceAction() {
                _super.call(this, "Delete");
            this.setEnabled(false);
            this.addExecutionListener(function () {
            });
        }
        return DeleteSpaceAction;
    })(api_ui.Action);
    app_wizard.DeleteSpaceAction = DeleteSpaceAction;    
    var CloseSpaceAction = (function (_super) {
        __extends(CloseSpaceAction, _super);
        function CloseSpaceAction() {
                _super.call(this, "Close");
            this.addExecutionListener(function () {
            });
        }
        return CloseSpaceAction;
    })(api_ui.Action);
    app_wizard.CloseSpaceAction = CloseSpaceAction;    
    var SpaceWizardActions = (function () {
        function SpaceWizardActions() {
            this.SAVE_SPACE = new SaveSpaceAction();
            this.DUPLICATE_SPACE = new DuplicateSpaceAction();
            this.DELETE_SPACE = new DeleteSpaceAction();
            this.CLOSE_SPACE = new CloseSpaceAction();
        }
        return SpaceWizardActions;
    })();
    app_wizard.SpaceWizardActions = SpaceWizardActions;    
})(app_wizard || (app_wizard = {}));
var app_wizard;
(function (app_wizard) {
    var SpaceWizardContext = (function () {
        function SpaceWizardContext(id) {
            this.id = id;
            this.spaceWizardActions = new app_wizard.SpaceWizardActions();
        }
        SpaceWizardContext.spaceWizardContexts = [];
        SpaceWizardContext.activeSpaceWizardContext = -1;
        SpaceWizardContext.createSpaceWizardContext = function createSpaceWizardContext() {
            var id = SpaceWizardContext.spaceWizardContexts.length + 1;
            var context = new SpaceWizardContext(id);
            SpaceWizardContext.spaceWizardContexts.push(context);
            return context;
        };
        SpaceWizardContext.setActiveSpaceWizardContext = function setActiveSpaceWizardContext(value) {
            SpaceWizardContext.activeSpaceWizardContext = value;
        };
        SpaceWizardContext.getActiveSpaceWizardContext = function getActiveSpaceWizardContext() {
            return SpaceWizardContext.spaceWizardContexts[SpaceWizardContext.activeSpaceWizardContext];
        };
        SpaceWizardContext.prototype.getId = function () {
            return this.id;
        };
        SpaceWizardContext.prototype.getActions = function () {
            return this.spaceWizardActions;
        };
        return SpaceWizardContext;
    })();
    app_wizard.SpaceWizardContext = SpaceWizardContext;    
})(app_wizard || (app_wizard = {}));
var app_wizard;
(function (app_wizard) {
    var SpaceWizardToolbar2 = (function (_super) {
        __extends(SpaceWizardToolbar2, _super);
        function SpaceWizardToolbar2(actions) {
                _super.call(this);
            _super.prototype.addAction.call(this, actions.SAVE_SPACE);
            _super.prototype.addAction.call(this, actions.DUPLICATE_SPACE);
            _super.prototype.addAction.call(this, actions.DELETE_SPACE);
            _super.prototype.addGreedySpacer.call(this);
            _super.prototype.addAction.call(this, actions.DELETE_SPACE);
        }
        return SpaceWizardToolbar2;
    })(api_ui_toolbar.Toolbar);
    app_wizard.SpaceWizardToolbar2 = SpaceWizardToolbar2;    
})(app_wizard || (app_wizard = {}));
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
            var isChecked = header.el.hasCls('x-grid-hd-checker-on');
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
        }, 
        {
            name: 'editable',
            type: 'boolean'
        }, 
        {
            name: 'deletable',
            type: 'boolean'
        }, 
        
    ],
    idProperty: 'name'
});
var app_ui;
(function (app_ui) {
    var WizardLayout = (function () {
        function WizardLayout(animation) {
            if (typeof animation === "undefined") { animation = 'slide'; }
            var _this = this;
            var cardLayout = new Ext.layout.container.Card({
                mixins: [
                    'Ext.util.Animate'
                ],
                deferredRender: false,
                renderHidden: false,
                animation: animation,
                easing: 'easeOut',
                duration: 500,
                setActiveItem: function (item) {
                    return _this.setActiveItem(item);
                }
            });
            this.ext = cardLayout;
        }
        WizardLayout.prototype.setActiveItem = function (item) {
            var me = this.ext;
            var owner = me.owner;
            var oldCard = me.activeItem;
            var oldIndex = owner.items.indexOf(oldCard);
            var newCard = me.parseActiveItem(item);
            var newIndex = owner.items.indexOf(newCard);
            if(oldCard !== newCard) {
                owner.fireEvent("animationstarted", newCard, oldCard);
                if(newCard.rendered && me.animation && me.animation !== "none") {
                    me.syncFx();
                    var target = me.getRenderTarget();
                    newCard.setWidth(target.getWidth() - target.getPadding("lr") - Ext.getScrollbarSize().width);
                    switch(me.animation) {
                        case 'fade':
                            newCard.el.setStyle({
                                position: 'absolute',
                                opacity: 0,
                                top: me.getRenderTarget().getPadding('t') + 'px'
                            });
                            newCard.show();
                            if(oldCard) {
                                oldCard.el.fadeOut({
                                    useDisplay: true,
                                    duration: me.duration,
                                    callback: function () {
                                        me.hide();
                                    },
                                    scope: me.activeItem
                                });
                            }
                            owner.doLayout();
                            newCard.el.fadeIn({
                                useDisplay: true,
                                duration: me.duration,
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
                                width: me.getRenderTarget().getWidth(),
                                top: me.getRenderTarget().getPadding('t') + 'px'
                            });
                            newCard.show();
                            if(oldCard) {
                                oldCard.el.slideOut(newIndex > oldIndex ? "l" : "r", {
                                    duration: me.duration,
                                    easing: me.easing,
                                    remove: false,
                                    scope: me.activeItem,
                                    callback: function () {
                                        me.hide();
                                    }
                                });
                            }
                            owner.doLayout();
                            newCard.el.slideIn(newIndex > oldIndex ? "r" : "l", {
                                duration: me.duration,
                                easing: me.easing,
                                scope: me,
                                callback: function () {
                                    newCard.el.setStyle({
                                        position: ''
                                    });
                                    owner.fireEvent("animationfinished", newCard, oldCard);
                                }
                            });
                            break;
                    }
                    me.activeItem = newCard;
                    me.sequenceFx();
                } else {
                    me.callParent([
                        newCard
                    ]);
                    owner.fireEvent("animationfinished", newCard, oldCard);
                }
                return newCard;
            }
        };
        return WizardLayout;
    })();
    app_ui.WizardLayout = WizardLayout;    
})(app_ui || (app_ui = {}));
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
                newCard.setWidth(target.getWidth() - target.getPadding("lr") - Ext.getScrollbarSize().width);
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
var app_ui;
(function (app_ui) {
    var WizardHeader = (function () {
        function WizardHeader(data, displayNameConfig, pathConfig, nameConfig, displayNameProperty, pathProperty, nameProperty) {
            if (typeof displayNameConfig === "undefined") { displayNameConfig = {
            }; }
            if (typeof pathConfig === "undefined") { pathConfig = {
            }; }
            if (typeof nameConfig === "undefined") { nameConfig = {
            }; }
            if (typeof displayNameProperty === "undefined") { displayNameProperty = 'displayName'; }
            if (typeof pathProperty === "undefined") { pathProperty = 'path'; }
            if (typeof nameProperty === "undefined") { nameProperty = 'name'; }
            this.data = data;
            this.displayNameConfig = Ext.apply({
            }, displayNameConfig, WizardHeader.DEFAULT_DISPLAY_NAME_CONFIG);
            this.pathConfig = Ext.apply({
            }, pathConfig, WizardHeader.DEFAULT_PATH_CONFIG);
            this.nameConfig = Ext.apply({
            }, nameConfig, WizardHeader.DEFAULT_NAME_CONFIG);
            this.displayNameProperty = displayNameProperty;
            this.pathProperty = pathProperty;
            this.nameProperty = nameProperty;
            var panel = new Ext.form.Panel({
                itemId: 'wizardHeader',
                cls: 'admin-wizard-header-container',
                border: false
            });
            this.ext = panel;
            this.initComponent();
        }
        WizardHeader.DEFAULT_DISPLAY_NAME_CONFIG = {
            emptyText: 'Display Name',
            enableKeyEvents: true,
            hideLabel: true,
            autoFocus: true
        };
        WizardHeader.DEFAULT_NAME_CONFIG = {
            hidden: false,
            allowBlank: false,
            emptyText: 'Name',
            enableKeyEvents: true,
            hideLabel: true,
            vtype: 'name',
            stripCharsRe: /[^a-z0-9\-]+/ig
        };
        WizardHeader.DEFAULT_PATH_CONFIG = {
            hidden: false,
            emptyText: 'path/to/',
            hideLabel: true
        };
        WizardHeader.prototype.initComponent = function () {
            var me = this.ext;
            this.appendVtypes();
            var headerData = this.prepareHeaderData(this.data);
            this.autogenerateName = Ext.isEmpty(headerData[this.nameProperty]);
            this.autogenerateDisplayName = Ext.isEmpty(headerData[this.displayNameProperty]);
            this.displayNameField = new Ext.form.field.Text(Ext.apply({
                xtype: 'textfield',
                grow: true,
                growMin: 200,
                name: this.displayNameProperty,
                value: headerData[this.displayNameProperty],
                cls: 'admin-display-name',
                dirtyCls: 'admin-display-name-dirty'
            }, this.displayNameConfig));
            this.displayNameField.on({
                afterrender: this.onDisplayNameAfterrender,
                keyup: this.onDisplayNameKey,
                change: this.onDisplayNameChanged,
                scope: this
            });
            this.pathField = new Ext.form.field.Display(Ext.apply({
                xtype: 'displayfield',
                cls: 'admin-path',
                dirtyCls: 'admin-path-dirty',
                value: headerData[this.pathProperty]
            }, this.pathConfig));
            this.nameField = new Ext.form.field.Text(Ext.apply({
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
            }, this.nameConfig));
            this.nameField.on({
                keyup: this.onNameKey,
                change: this.onNameChanged,
                scope: this
            });
            var items = [
                this.displayNameField
            ];
            if(!this.pathField.hidden && !this.nameField.hidden) {
                items.push({
                    xtype: 'fieldcontainer',
                    hideLabel: true,
                    layout: 'hbox',
                    items: [
                        this.pathField, 
                        this.nameField
                    ]
                });
            } else if(!this.pathField.hidden) {
                items.push(this.pathField);
            } else if(!this.nameField.hidden) {
                items.push(this.nameField);
            }
            me.add(items);
            me.addEvents('displaynamechange', 'displaynameoverride', 'namechange', 'nameoverride');
        };
        WizardHeader.prototype.onDisplayNameAfterrender = function (field) {
            if(!field.readOnly && field.autoFocus) {
                field.focus(false, 100);
                field.selectText(0, 0);
            }
        };
        WizardHeader.prototype.onDisplayNameKey = function (field, event, opts) {
            var wasAutoGenerate = this.autogenerateDisplayName;
            var autoGenerate = Ext.isEmpty(field.getValue());
            if(wasAutoGenerate != autoGenerate) {
                this.ext.fireEvent('displaynameoverride', !autoGenerate);
            }
            this.autogenerateDisplayName = autoGenerate;
        };
        WizardHeader.prototype.onDisplayNameChanged = function (field, newVal, oldVal, opts) {
            if(this.ext.fireEvent('displaynamechange', newVal, oldVal) !== false && this.autogenerateName) {
                var processedValue = this.nameField.processRawValue(this.preProcessName(newVal));
                this.nameField.setValue(processedValue);
            }
            this.nameField.growMax = this.ext.getEl().getWidth() - 100;
            this.nameField.doComponentLayout();
        };
        WizardHeader.prototype.onNameKey = function (field, event, opts) {
            var wasAutoGenerate = this.autogenerateName;
            var autoGenerate = Ext.isEmpty(field.getValue());
            if(wasAutoGenerate != autoGenerate) {
                this.ext.fireEvent('nameoverride', !autoGenerate);
            }
            this.autogenerateName = autoGenerate;
        };
        WizardHeader.prototype.onNameChanged = function (field, newVal, oldVal, opts) {
            this.ext.fireEvent('namechange', newVal, oldVal);
        };
        WizardHeader.prototype.appendVtypes = function () {
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
        };
        WizardHeader.prototype.preProcessName = function (displayName) {
            return displayName;
        };
        WizardHeader.prototype.prepareHeaderData = function (data) {
            return data && data.data || data || {
            };
        };
        WizardHeader.prototype.setData = function (data) {
            this.data = data;
            if(this.resolveHeaderData) {
                this.ext.getForm().setValues(this.resolveHeaderData(data));
            }
        };
        WizardHeader.prototype.getData = function () {
            return this.ext.getForm().getFieldValues();
        };
        WizardHeader.prototype.getDisplayName = function () {
            return this.displayNameField.getValue();
        };
        WizardHeader.prototype.setDisplayName = function (displayName) {
            this.displayNameField.setValue(displayName);
        };
        WizardHeader.prototype.setName = function (name) {
            this.nameField.setValue(name);
        };
        return WizardHeader;
    })();
    app_ui.WizardHeader = WizardHeader;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var WizardPanel = (function () {
        function WizardPanel(config) {
            this.showControls = true;
            this.isNew = true;
            this.validateItems = [];
            this.boundItems = [];
            this.presentationMode = false;
            this.dirtyItems = [];
            this.invalidItems = [];
            this.boundItems = [];
            var defaultPanelConfig = {
                itemId: 'wizardPanel',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                cls: 'admin-wizard'
            };
            var panelConfig = Ext.apply({
            }, config, defaultPanelConfig);
            var panel = new Ext.panel.Panel(panelConfig);
            this.ext = panel;
            this.ext.wrapper = this;
            this.initComponent();
        }
        WizardPanel.prototype.initComponent = function () {
            var _this = this;
            var me = this.ext;
            var events = [
                "beforestepchanged", 
                "stepchanged", 
                "animationstarted", 
                "animationfinished", 
                'validitychange', 
                'dirtychange', 
                "finished"
            ];
            me.cls += this.isNew ? ' admin-wizard-new' : ' admin-wizard-edit';
            this.wizard = new Ext.container.Container({
                region: 'center',
                layout: {
                    type: 'wizard',
                    animation: 'none'
                },
                items: this.createSteps()
            });
            var items = [
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
                                        _this.prev();
                                    }
                                },
                                mouseover: {
                                    element: 'el',
                                    fn: function (event, element) {
                                        _this.updateNavButton(element, '#000000');
                                    }
                                },
                                mouseout: {
                                    element: 'el',
                                    fn: function (event, element) {
                                        _this.updateNavButton(element, '#777777');
                                    }
                                }
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    itemId: 'prev',
                                    iconCls: 'wizard-nav-icon icon-chevron-left icon-6x',
                                    cls: 'wizard-nav-button wizard-nav-button-left',
                                    height: 74,
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
                                        _this.next();
                                    }
                                },
                                mouseover: {
                                    element: 'el',
                                    fn: function (event, element) {
                                        _this.updateNavButton(element, '#000000');
                                    }
                                },
                                mouseout: {
                                    element: 'el',
                                    fn: function (event, element) {
                                        _this.updateNavButton(element, '#777777');
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
                                    height: 74,
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
                                _this.updateShadow();
                            }
                        }
                    }
                }
            ];
            me.add(items);
            me.addEvents(events);
            this.wizard.addEvents(events);
            this.wizard.enableBubble(events);
            me.on({
                animationstarted: this.onAnimationStarted,
                animationfinished: this.onAnimationFinished,
                resize: function () {
                    _this.updateShadow();
                },
                scope: this
            });
            if(this.getActionButton()) {
                this.boundItems.push(this.getActionButton());
            }
            me.down('#progressBar').update(this.wizard.items.items);
            me.on('afterrender', this.bindItemListeners, this);
            this.updateShadow();
        };
        WizardPanel.prototype.updateShadow = function () {
            var me = this.ext;
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
        };
        WizardPanel.prototype.updateNavButton = function (element, color) {
            var btn = Ext.get(element);
            if(!btn.hasCls('wizard-nav-icon')) {
                btn = btn.down('.wizard-nav-icon');
            } else if(btn.hasCls('x-btn-inner')) {
                btn = btn.next('.x-btn-icon');
            }
            btn && btn.setStyle('color', color);
        };
        WizardPanel.prototype.updateProgress = function (newStep) {
            var me = this.ext;
            var progressBar = me.down('#progressBar');
            progressBar.update(this.wizard.items.items);
            var conditionsMet = this.isWizardValid && (this.isWizardDirty || this.isNew);
            progressBar.setDisabled(this.isNew ? !this.isStepValid(newStep) : !conditionsMet);
        };
        WizardPanel.prototype.bindItemListeners = function (cmp) {
            var _this = this;
            Ext.each(this.validateItems, function (validateItem, index, all) {
                if(validateItem) {
                    validateItem.on({
                        'validitychange': _this.handleValidityChange,
                        'dirtychange': _this.handleDirtyChange,
                        scope: _this
                    }, _this);
                }
                return true;
            });
            var checkValidityFn = function (panel) {
                panel.getForm().checkValidity();
            };
            this.wizard.items.each(function (item, i) {
                if(i === 0) {
                    _this.onAnimationFinished(item, null);
                }
                if('editUserFormPanel' === item.getXType()) {
                    item.on('fieldsloaded', checkValidityFn);
                }
                var itemForm = Ext.isFunction(item.getForm) ? item.getForm() : undefined;
                if(itemForm) {
                    if(Ext.isFunction(_this.washDirtyForm)) {
                        _this.washDirtyForm(itemForm);
                    }
                    Ext.apply(itemForm, {
                        onValidityChange: _this.formOnValidityChange,
                        _boundItems: undefined
                    });
                    itemForm.on({
                        'validitychange': _this.handleValidityChange,
                        'dirtychange': _this.handleDirtyChange,
                        scope: _this
                    });
                    itemForm.checkValidity();
                }
            });
        };
        WizardPanel.prototype.formOnValidityChange = function () {
            var me = this.ext;
            var wizardPanel = me.owner.up('wizardPanel');
            var boundItems = wizardPanel.getFormBoundItems(this);
            if(boundItems && me.owner === wizardPanel.getActiveItem()) {
                var valid = wizardPanel.isStepValid(me.owner);
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
        };
        WizardPanel.prototype.getFormBoundItems = function (form) {
            var boundItems = form._boundItems;
            if(!boundItems && form.owner.rendered) {
                boundItems = form._boundItems = Ext.create('Ext.util.MixedCollection');
                boundItems.addAll(form.owner.query('[formBind]'));
                boundItems.addAll(this.boundItems);
            }
            return boundItems;
        };
        WizardPanel.prototype.handleValidityChange = function (form, valid, opts) {
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
                this.ext.fireEvent('validitychange', this, isWizardValid);
            }
        };
        WizardPanel.prototype.handleDirtyChange = function (form, dirty, opts) {
            if(dirty) {
                Ext.Array.include(this.dirtyItems, form);
            } else {
                Ext.Array.remove(this.dirtyItems, form);
            }
            this.updateProgress();
            var isWizardDirty = this.dirtyItems.length > 0;
            if(this.isWizardDirty !== isWizardDirty) {
                this.isWizardDirty = isWizardDirty;
                this.ext.fireEvent('dirtychange', this, isWizardDirty);
            }
        };
        WizardPanel.prototype.isStepValid = function (step) {
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
        };
        WizardPanel.prototype.getProgressBar = function () {
            return this.ext.down('#progressBar');
        };
        WizardPanel.prototype.createRibbon = function () {
            var _this = this;
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
                        fn: me.changeStep,
                        element: 'el',
                        scope: me
                    }
                },
                styleHtmlContent: true,
                margin: 0,
                tpl: new Ext.XTemplate(stepsTpl, {
                    resolveClsName: function (index, total) {
                        var activeIndex = me.wizard.items.indexOf(_this.getActiveItem()) + 1;
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
        };
        WizardPanel.prototype.onAnimationStarted = function (newStep, oldStep) {
            if(this.showControls) {
                this.updateButtons(this.wizard, true);
            }
            if(this.externalControls) {
                this.updateButtons(this.externalControls, true);
            }
        };
        WizardPanel.prototype.onAnimationFinished = function (newStep, oldStep) {
            var me = this.ext;
            if(newStep) {
                this.updateProgress(newStep);
                this.focusFirstField(newStep);
                me.fireEvent("stepchanged", this, oldStep, newStep);
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
                me.doLayout();
                return newStep;
            }
            return null;
        };
        WizardPanel.prototype.focusFirstField = function (newStep) {
            var activeItem = newStep || this.getActiveItem();
            var firstField;
            if(activeItem && (firstField = activeItem.down('field[disabled=false]'))) {
                firstField.focus(false);
                if(firstField.rendered && firstField.selectText) {
                    firstField.selectText(0, 0);
                }
            }
        };
        WizardPanel.prototype.updateButtons = function (toolbar, disable) {
            var me = this.ext;
            if(toolbar) {
                var prev = me.down('#prev'), next = me.down('#next');
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
        };
        WizardPanel.prototype.changeStep = function (event, target) {
            var me = this.ext;
            var progressBar = me.down('#progressBar');
            var isNew = this.isNew;
            var isDisabled = progressBar.isDisabled();
            var li = target && target.tagName === "LI" ? Ext.fly(target) : Ext.fly(target).up('li');
            if((!isDisabled && isNew && li && li.hasCls('next') && li.hasCls('immediate')) || (!isDisabled && !isNew) || (isDisabled && !isNew && li && !li.hasCls('last')) || (li && li.hasCls('previous'))) {
                var step = Number(li.getAttribute('wizardStep'));
                this.navigate(step - 1);
            }
            event.stopEvent();
        };
        WizardPanel.prototype.createHeaderPanel = function () {
            var icon = this.createIcon();
            var actionButton = Ext.apply(this.createActionButton(), {
                itemId: 'actionButton',
                ui: 'green',
                margin: '0 30 0 0',
                scale: 'large'
            });
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
                            actionButton
                        ]
                    }
                ]
            };
        };
        WizardPanel.prototype.getActionButton = function () {
            return this.ext.down('#actionButton');
        };
        WizardPanel.prototype.next = function (btn) {
            return this.navigate("next", btn);
        };
        WizardPanel.prototype.prev = function (btn) {
            return this.navigate("prev", btn);
        };
        WizardPanel.prototype.finish = function () {
            this.ext.fireEvent("finished", this, this.getData());
        };
        WizardPanel.prototype.getNext = function () {
            return this.wizard.getLayout().getNext();
        };
        WizardPanel.prototype.getPrev = function () {
            return this.wizard.getLayout().getPrev();
        };
        WizardPanel.prototype.getActiveItem = function () {
            return this.wizard.getLayout().getActiveItem();
        };
        WizardPanel.prototype.navigate = function (direction, btn) {
            var oldStep = this.getActiveItem();
            if(btn) {
                this.externalControls = btn.up('toolbar');
            }
            if(this.ext.fireEvent("beforestepchanged", this, oldStep) !== false) {
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
        };
        WizardPanel.prototype.addData = function (newValues) {
            if(Ext.isEmpty(this.data)) {
                this.data = {
                };
            }
            Ext.merge(this.data, newValues);
        };
        WizardPanel.prototype.deleteData = function (key) {
            if(key) {
                delete this.data[key];
            }
        };
        WizardPanel.prototype.getData = function () {
            var _this = this;
            this.wizard.items.each(function (item) {
                if(item.getData) {
                    _this.addData(item.getData());
                } else if(item.getForm) {
                    _this.addData(item.getForm().getFieldValues());
                }
            });
            return this.data;
        };
        WizardPanel.prototype.createSteps = function () {
            return null;
        };
        WizardPanel.prototype.createIcon = function () {
            return null;
        };
        WizardPanel.prototype.createWizardHeader = function () {
            return null;
        };
        WizardPanel.prototype.createActionButton = function () {
            return null;
        };
        WizardPanel.prototype.washDirtyForm = function (form) {
            return null;
        };
        return WizardPanel;
    })();
    app_ui.WizardPanel = WizardPanel;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var BaseActionMenu = (function () {
        function BaseActionMenu(menuItems) {
            var menu = new Ext.menu.Menu({
                cls: 'admin-context-menu',
                border: false,
                shadow: false,
                width: 120
            });
            for(var i in menuItems) {
                menu.add(menuItems[i]);
            }
            this.ext = new Ext.button.Button({
                menu: menu,
                cls: 'admin-dropdown-button',
                width: 120,
                padding: 5,
                text: 'Actions',
                height: 30,
                itemId: 'dropdown',
                margin: '0 20 0 0',
                tdAttrs: {
                    width: 140,
                    valign: 'top',
                    style: {
                        padding: '0 20px 0 0'
                    }
                }
            });
        }
        return BaseActionMenu;
    })();
    app_ui.BaseActionMenu = BaseActionMenu;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var ActionMenu = (function (_super) {
        __extends(ActionMenu, _super);
        function ActionMenu() {
            var openMenuItem = new Ext.menu.Item({
                text: 'Open',
                action: 'viewSpace'
            });
            var editMenuItem = new Ext.menu.Item({
                text: 'Edit',
                action: 'editSpace'
            });
                _super.call(this, [
        openMenuItem, 
        editMenuItem
    ]);
        }
        return ActionMenu;
    })(app_ui.BaseActionMenu);
    app_ui.ActionMenu = ActionMenu;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var ActionMenu2 = (function (_super) {
        __extends(ActionMenu2, _super);
        function ActionMenu2() {
                _super.call(this, app.SpaceActions.OPEN_SPACE, app.SpaceActions.EDIT_SPACE);
        }
        return ActionMenu2;
    })(api_ui_menu.ActionMenu);
    app_ui.ActionMenu2 = ActionMenu2;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var DetailToolbar = (function () {
        function DetailToolbar() {
            var tbar = new Ext.toolbar.Toolbar({
                itemId: 'spaceDetailToolbar',
                cls: 'admin-toolbar'
            });
            var defaults = {
                scale: 'medium'
            };
            var editButton = new Ext.button.Button(Ext.apply({
                text: 'Edit',
                action: 'editSpace'
            }, defaults));
            var deleteButton = new Ext.button.Button(Ext.apply({
                text: 'Delete',
                action: 'deleteSpace'
            }, defaults));
            var separator = new Ext.toolbar.Fill();
            var closeButton = new Ext.button.Button(Ext.apply({
                text: 'Close',
                action: 'closeSpace',
                scale: 'medium'
            }, defaults));
            tbar.add(editButton, deleteButton, separator, closeButton);
            this.ext = tbar;
        }
        return DetailToolbar;
    })();
    app_ui.DetailToolbar = DetailToolbar;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var SpaceDetailPanel = (function (_super) {
        __extends(SpaceDetailPanel, _super);
        function SpaceDetailPanel() {
            var _this = this;
                _super.call(this);
            var selectedSpaces = app.SpaceContext.get().getSelectedSpaces();
            if(!selectedSpaces || selectedSpaces.length == 0) {
                this.showBlank();
            }
            app_event.GridSelectionChangeEvent.on(function (event) {
                _this.update(event.getModels());
            });
        }
        SpaceDetailPanel.prototype.showBlank = function () {
            this.getEl().setInnerHtml("Nothing selected");
        };
        SpaceDetailPanel.prototype.update = function (models) {
            if(models.length == 1) {
                this.showSingle(models[0]);
            } else if(models.length > 1) {
                this.showMultiple(models);
            }
        };
        SpaceDetailPanel.prototype.showSingle = function (model) {
            this.empty();
            var tabPanel = new api_ui_detailpanel.DetailTabPanel(model);
            tabPanel.addTab(new api_ui_detailpanel.DetailPanelTab("Analytics"));
            tabPanel.addTab(new api_ui_detailpanel.DetailPanelTab("Sales"));
            tabPanel.addTab(new api_ui_detailpanel.DetailPanelTab("History"));
            var testAction = new api_ui.Action("Test");
            tabPanel.addAction(testAction);
            this.getEl().appendChild(tabPanel.getHTMLElement());
        };
        SpaceDetailPanel.prototype.showMultiple = function (models) {
            this.empty();
            for(var i in models) {
                var removeCallback = function (box) {
                    var models = [
                        box.getModel()
                    ];
                    new app_event.GridDeselectEvent(models).fire();
                };
                this.getEl().appendChild(new api_ui_detailpanel.DetailPanelBox(models[i], removeCallback).getHTMLElement());
            }
        };
        return SpaceDetailPanel;
    })(api_ui_detailpanel.DetailPanel);
    app_ui.SpaceDetailPanel = SpaceDetailPanel;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var DeleteSpaceDialog = (function (_super) {
        __extends(DeleteSpaceDialog, _super);
        function DeleteSpaceDialog() {
            var _this = this;
                _super.call(this, "Space");
            this.deleteAction = new DeleteSpaceDialogAction();
            this.deleteHandler = new api_handler.DeleteSpacesHandler();
            this.setDeleteAction(this.deleteAction);
            var deleteCallback = function (obj, success, result) {
                _this.close();
                components.gridPanel.refresh();
                api_notify.showFeedback('Space(s) was deleted!');
            };
            this.deleteAction.addExecutionListener(function () {
                _this.deleteHandler.doDelete(api_handler.DeleteSpaceParamFactory.create(_this.spacesToDelete), deleteCallback);
            });
            document.body.appendChild(this.getHTMLElement());
        }
        DeleteSpaceDialog.prototype.setSpacesToDelete = function (spaces) {
            this.spacesToDelete = spaces;
            var deleteItems = [];
            for(var i in spaces) {
                var space = spaces[i];
                var deleteItem = new api_delete.DeleteItem(space.data.iconUrl, space.data.displayName);
                deleteItems.push(deleteItem);
            }
            this.setDeleteItems(deleteItems);
        };
        return DeleteSpaceDialog;
    })(api_delete.DeleteDialog);
    app_ui.DeleteSpaceDialog = DeleteSpaceDialog;    
    var DeleteSpaceDialogAction = (function (_super) {
        __extends(DeleteSpaceDialogAction, _super);
        function DeleteSpaceDialogAction() {
                _super.call(this, "Delete");
        }
        return DeleteSpaceDialogAction;
    })(api_ui.Action);
    app_ui.DeleteSpaceDialogAction = DeleteSpaceDialogAction;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var TreeGridPanel = (function () {
        function TreeGridPanel(region) {
            var _this = this;
            this.store = new Ext.data.Store({
                pageSize: 100,
                autoLoad: true,
                model: 'Admin.model.SpaceModel',
                proxy: {
                    type: 'direct',
                    directFn: api_remote.RemoteService.space_list,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'spaces',
                        totalProperty: 'total'
                    }
                }
            });
            this.keyField = 'name';
            this.nameTemplate = '<div class="admin-{0}-thumbnail">' + '<img src="{1}"/>' + '</div>' + '<div class="admin-{0}-description">' + '<h6>{2}</h6>' + '<p>{3}</p>' + '</div>';
            var gridSelectionPlugin = new Admin.plugin.PersistentGridSelectionPlugin({
                keyField: this.keyField
            });
            var p = this.ext = new Ext.panel.Panel({
                region: region,
                flex: 1,
                layout: 'card',
                border: false,
                activeItem: 'grid',
                itemId: 'spaceTreeGrid',
                alias: 'widget.spaceTreeGrid',
                gridConf: {
                    selModel: Ext.create('Ext.selection.CheckboxModel', {
                        headerWidth: 36
                    })
                },
                treeConf: {
                    selModel: Ext.create('Ext.selection.CheckboxModel', {
                        headerWidth: 36
                    })
                }
            });
            var gp = new Ext.grid.Panel({
                itemId: 'grid',
                cls: 'admin-grid',
                border: false,
                hideHeaders: true,
                columns: [
                    {
                        text: 'Display Name',
                        dataIndex: 'displayName',
                        sortable: true,
                        renderer: this.nameRenderer,
                        scope: this,
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
                        scope: this,
                        sortable: true
                    }
                ],
                viewConfig: {
                    trackOver: true,
                    stripeRows: true,
                    loadMask: {
                        store: this.store
                    }
                },
                store: this.store,
                selModel: Ext.create('Ext.selection.CheckboxModel', {
                    headerWidth: 36
                }),
                plugins: [
                    gridSelectionPlugin
                ],
                listeners: {
                    selectionchange: function (selModel, selected, opts) {
                        new app_event.GridSelectionChangeEvent(selected).fire();
                    },
                    itemcontextmenu: function (view, rec, node, index, event) {
                        event.stopEvent();
                        new app_event.ShowContextMenuEvent(event.xy[0], event.xy[1]).fire();
                    },
                    itemdblclick: function (grid, record) {
                        new app_event.EditSpaceEvent(grid.getSelection()).fire();
                    }
                }
            });
            gp.addDocked(new Ext.toolbar.Toolbar({
                itemId: 'selectionToolbar',
                cls: 'admin-white-toolbar',
                dock: 'top',
                store: this.store,
                gridPanel: gp,
                resultCountHidden: true,
                plugins: [
                    'gridToolbarPlugin'
                ]
            }));
            gp.getStore().on('datachanged', this.fireUpdateEvent, this);
            p.add(gp);
            app_event.GridDeselectEvent.on(function (event) {
                _this.deselect(event.getModels()[0].data.name);
            });
        }
        TreeGridPanel.prototype.fireUpdateEvent = function (values) {
            this.ext.fireEvent('datachanged', values);
        };
        TreeGridPanel.prototype.getActiveList = function () {
            return (this.ext.getLayout()).getActiveItem();
        };
        TreeGridPanel.prototype.nameRenderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
            var space = record.data;
            var activeListType = this.getActiveList().getItemId();
            return Ext.String.format(this.nameTemplate, activeListType, space.iconUrl, value, space.name);
        };
        TreeGridPanel.prototype.statusRenderer = function () {
            return "Online";
        };
        TreeGridPanel.prototype.prettyDateRenderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
            try  {
                if(parent && Ext.isFunction(parent['humane_date'])) {
                    return parent['humane_date'](value);
                } else {
                    return value;
                }
            } catch (e) {
                return value;
            }
        };
        TreeGridPanel.prototype.refresh = function () {
            this.store.loadPage(this.store.currentPage);
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
        return TreeGridPanel;
    })();
    app_ui.TreeGridPanel = TreeGridPanel;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var ContextMenu = (function () {
        function ContextMenu() {
            var _this = this;
            var menu = new Ext.menu.Menu({
                cls: 'admin-context-menu',
                border: false,
                shadow: false,
                itemId: 'spaceContextMenu'
            });
            var menuItemEdit = new Ext.menu.Item({
                text: 'Edit',
                iconCls: 'icon-edit',
                action: 'editSpace'
            });
            var menuItemOpen = new Ext.menu.Item({
                text: 'Open',
                iconCls: 'icon-view',
                action: 'viewSpace'
            });
            var menuItemDelete = new Ext.menu.Item({
                text: 'Delete',
                iconCls: 'icon-delete',
                action: 'deleteSpace'
            });
            menu.add(menuItemEdit, menuItemOpen, menuItemDelete);
            this.ext = menu;
            app_event.ShowContextMenuEvent.on(function (event) {
                _this.showAt(event.getX(), event.getY());
            });
        }
        ContextMenu.prototype.showAt = function (x, y) {
            this.ext.showAt(x, y);
        };
        return ContextMenu;
    })();
    app_ui.ContextMenu = ContextMenu;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var ContextMenuGridPanel = (function (_super) {
        __extends(ContextMenuGridPanel, _super);
        function ContextMenuGridPanel() {
                _super.call(this);
            _super.prototype.addAction.call(this, app.SpaceActions.EDIT_SPACE);
            _super.prototype.addAction.call(this, app.SpaceActions.OPEN_SPACE);
            _super.prototype.addAction.call(this, app.SpaceActions.DELETE_SPACE);
        }
        return ContextMenuGridPanel;
    })(api_ui_menu.ContextMenu);
    app_ui.ContextMenuGridPanel = ContextMenuGridPanel;    
})(app_ui || (app_ui = {}));
var app_ui_wizard;
(function (app_ui_wizard) {
    var SpaceWizardToolbar = (function () {
        function SpaceWizardToolbar(isNew) {
            if (typeof isNew === "undefined") { isNew = true; }
            this.isNew = isNew;
            var tb = new Ext.toolbar.Toolbar({
                cls: 'admin-toolbar',
                itemId: 'spaceWizardToolbar',
                border: false
            });
            this.ext = tb;
            var defaults = {
                scale: 'medium'
            };
            var saveBtn = new Ext.button.Button(Ext.apply({
                text: 'Save',
                action: 'saveSpace',
                itemId: 'save',
                disabled: true
            }, defaults));
            var deleteBtn = new Ext.button.Button(Ext.apply({
                text: 'Delete',
                disabled: this.isNew,
                action: 'deleteSpace'
            }, defaults));
            var duplicateBtn = new Ext.button.Button(Ext.apply({
                text: 'Duplicate',
                disabled: this.isNew
            }, defaults));
            var closeBtn = new Ext.button.Button(Ext.apply({
                text: 'Close',
                action: 'closeWizard'
            }, defaults));
            tb.add(saveBtn, deleteBtn, duplicateBtn, '->', closeBtn);
        }
        return SpaceWizardToolbar;
    })();
    app_ui_wizard.SpaceWizardToolbar = SpaceWizardToolbar;    
})(app_ui_wizard || (app_ui_wizard = {}));
var app_ui_wizard;
(function (app_ui_wizard) {
    var SpaceStepPanel = (function () {
        function SpaceStepPanel(data) {
            this.data = data;
            var templates = new Ext.data.Store({
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
            var fs = this.ext = new Ext.form.FieldSet({
                stepTitle: 'Space',
                title: 'Template',
                padding: '10px 15px',
                defaults: {
                    width: 600
                }
            });
            var combo = new Ext.form.field.ComboBox({
                fieldLabel: 'Space Template',
                displayField: 'name',
                valueField: 'code',
                store: templates
            });
            fs.add(combo);
        }
        return SpaceStepPanel;
    })();
    app_ui_wizard.SpaceStepPanel = SpaceStepPanel;    
})(app_ui_wizard || (app_ui_wizard = {}));
var app_ui_wizard;
(function (app_ui_wizard) {
    var SpaceWizardPanel = (function (_super) {
        __extends(SpaceWizardPanel, _super);
        function SpaceWizardPanel(id, title, editing, data) {
            var headerData = this.resolveHeaderData();
            this.data = data;
            var panelConfig = {
                id: id,
                editing: editing,
                title: title,
                data: data,
                itemId: 'spaceAdminWizardPanel',
                border: 0,
                autoScroll: true,
                defaults: {
                    border: false
                },
                tbar: new app_ui_wizard.SpaceWizardToolbar(headerData.isNewSpace).ext
            };
                _super.call(this, panelConfig);
            var uploader = this.ext.down('photoUploadButton');
            uploader.on('fileuploaded', this.photoUploaded, this);
        }
        SpaceWizardPanel.prototype.resolveHeaderData = function () {
            var iconUrl = 'resources/images/icons/128x128/default_space.png';
            var displayNameValue = '';
            var spaceName = '';
            var data = this.data;
            if(data) {
                displayNameValue = data.get('displayName') || '';
                spaceName = data.get('name') || '';
                iconUrl = data.get('iconUrl');
            }
            return {
                'displayName': displayNameValue,
                'spaceName': spaceName,
                'isNewSpace': spaceName ? false : true,
                'iconUrl': iconUrl
            };
        };
        SpaceWizardPanel.prototype.createSteps = function () {
            var spaceStep = new app_ui_wizard.SpaceStepPanel(this.data);
            return [
                spaceStep.ext, 
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
        };
        SpaceWizardPanel.prototype.createWizardHeader = function () {
            var pathConfig = {
                hidden: true
            };
            var wizardHeader = this.wizardHeader = new app_ui.WizardHeader(this.data, {
            }, pathConfig);
            this.validateItems.push(wizardHeader.ext);
            return wizardHeader.ext;
        };
        SpaceWizardPanel.prototype.createIcon = function () {
            var me = this.ext;
            var headerData = this.resolveHeaderData();
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
        };
        SpaceWizardPanel.prototype.createActionButton = function () {
            return {
                xtype: 'button',
                text: 'Save',
                handler: function () {
                    new app_event.SaveSpaceEvent().fire();
                }
            };
        };
        SpaceWizardPanel.prototype.getWizardHeader = function () {
            return this.wizardHeader;
        };
        SpaceWizardPanel.prototype.getData = function () {
            var data = _super.prototype.getData.call(this);
            var headerData = this.getWizardHeader().getData();
            return this.merge(data, {
                displayName: headerData.displayName,
                spaceName: headerData.name
            });
        };
        SpaceWizardPanel.prototype.photoUploaded = function (photoUploadButton, response) {
            var iconRef = response.items && response.items.length > 0 && response.items[0].id;
            this.addData({
                iconRef: iconRef
            });
        };
        SpaceWizardPanel.prototype.merge = function (obj1, obj2) {
            var obj3 = {
            };
            for(var attrname in obj1) {
                obj3[attrname] = obj1[attrname];
            }
            for(var attrname in obj2) {
                obj3[attrname] = obj2[attrname];
            }
            return obj3;
        };
        return SpaceWizardPanel;
    })(app_ui.WizardPanel);
    app_ui_wizard.SpaceWizardPanel = SpaceWizardPanel;    
})(app_ui_wizard || (app_ui_wizard = {}));
var app_ui_wizard;
(function (app_ui_wizard) {
    var SpaceWizardPanel2 = (function (_super) {
        __extends(SpaceWizardPanel2, _super);
        function SpaceWizardPanel2(id, title) {
                _super.call(this);
            this.setTitle(title);
            this.setSubtitle(id);
            var spacePanel = new api_ui.Panel("spacePanel");
            var h1El = new api_ui.H1El();
            h1El.getEl().setInnerHtml("space");
            spacePanel.appendChild(h1El);
            var schemaPanel = new api_ui.Panel("schemaPanel");
            h1El = new api_ui.H1El();
            h1El.getEl().setInnerHtml("schema");
            schemaPanel.appendChild(h1El);
            var modulesPanel = new api_ui.Panel("modulesPanel");
            h1El = new api_ui.H1El();
            h1El.getEl().setInnerHtml("modules");
            modulesPanel.appendChild(h1El);
            var templatesPanel = new api_ui.Panel("templatesPanel");
            h1El = new api_ui.H1El();
            h1El.getEl().setInnerHtml("templates");
            templatesPanel.appendChild(h1El);
            this.addStep(new api_ui_wizard.WizardStep("Space", spacePanel));
            this.addStep(new api_ui_wizard.WizardStep("Schemas", schemaPanel));
            this.addStep(new api_ui_wizard.WizardStep("Modules", modulesPanel));
            this.addStep(new api_ui_wizard.WizardStep("Templates", templatesPanel));
        }
        return SpaceWizardPanel2;
    })(api_ui_wizard.WizardPanel);
    app_ui_wizard.SpaceWizardPanel2 = SpaceWizardPanel2;    
})(app_ui_wizard || (app_ui_wizard = {}));
var app_ui;
(function (app_ui) {
    var AdminImageButton = (function () {
        function AdminImageButton(iconUrl, popupTpl, popupData) {
            this.popupTpl = popupTpl;
            this.popupData = popupData;
            var button = new Ext.button.Button({
                itemId: 'adminImageButton',
                cls: 'admin-image-button',
                scale: 'large',
                icon: iconUrl
            });
            button.on('click', this.onClick, this);
            this.ext = button;
        }
        AdminImageButton.prototype.onClick = function (button) {
            if(!this.popupPanel) {
                this.popupPanel = new Ext.panel.Panel({
                    floating: true,
                    cls: 'admin-toolbar-popup',
                    border: false,
                    tpl: this.popupTpl,
                    data: this.popupData,
                    styleHtmlContent: true,
                    renderTo: Ext.getBody(),
                    listeners: {
                        afterrender: function (cont) {
                            cont.show();
                            cont.setPagePosition(cont.el.getAlignToXY(button.el, "tr-br?"));
                        }
                    }
                });
            } else {
                if(this.popupPanel.isHidden()) {
                    this.popupPanel.show();
                } else {
                    this.popupPanel.hide();
                }
            }
        };
        return AdminImageButton;
    })();
    app_ui.AdminImageButton = AdminImageButton;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var TopBarMenuItem = (function () {
        function TopBarMenuItem(text1, text2, card, tabBar, closable, disabled, editing, hidden, iconClass, iconSrc) {
            var _this = this;
            this.text1 = text1;
            this.text2 = text2;
            var tbmi = new Ext.container.Container({
                cls: 'admin-topbar-menu-item',
                activeCls: 'active',
                isMenuItem: true,
                canActivate: true,
                card: card,
                tabBar: tabBar,
                closable: closable,
                disabled: disabled,
                editing: editing,
                hidden: hidden,
                iconClass: iconClass,
                iconSrc: iconSrc,
                text1: text1,
                text2: text2,
                layout: {
                    type: 'hbox',
                    align: 'middle'
                }
            });
            this.ext = tbmi;
            Ext.override(tbmi, {
                onClick: function (e) {
                    return _this.onClick(e);
                },
                activate: function () {
                    _this.activate();
                },
                deactivate: function () {
                    _this.deactivate();
                }
            });
            tbmi.enableBubble('closeMenuItem');
            this.initComponent(tbmi);
        }
        TopBarMenuItem.prototype.initComponent = function (topBarMenuItem) {
            var _this = this;
            var items = [];
            if(topBarMenuItem.iconCls || topBarMenuItem.iconSrc) {
                var image = new Ext.Img({
                    width: 32,
                    height: 32,
                    margin: '0 12px 0 0',
                    cls: topBarMenuItem.iconCls,
                    src: topBarMenuItem.iconSrc
                });
                items.push(image);
            }
            if(this.text1 || this.text2) {
                var titleContainer = new Ext.Component({
                    flex: 1,
                    itemId: 'titleContainer',
                    styleHtmlContent: true,
                    tpl: '<strong>{text1}</strong><tpl if="text2"><br/><em>{text2}</em></tpl>',
                    data: {
                        text1: this.text1,
                        text2: this.text2
                    }
                });
                items.push(titleContainer);
                this.titleContainer = titleContainer;
            }
            if(topBarMenuItem.closable !== false) {
                var closeButton = new Ext.Component({
                    autoEl: 'a',
                    cls: 'close-button icon-remove icon-large',
                    margins: '0 0 0 12px'
                });
                closeButton.on('afterrender', function (cmp) {
                    cmp.el.on('click', function () {
                        _this.deactivate();
                        topBarMenuItem.fireEvent('closeMenuItem', topBarMenuItem);
                    });
                });
                items.push(closeButton);
            }
            topBarMenuItem.add(items);
            topBarMenuItem.addEvents('activate', 'deactivate', 'click', 'closeMenuItem');
        };
        TopBarMenuItem.prototype.activate = function () {
            var me = this.ext;
            if(!me.activated && me.canActivate && me.rendered && !me.isDisabled() && me.isVisible()) {
                me.el.addCls(me.activeCls);
                me.focus();
                me.activated = true;
                me.fireEvent('activate', me);
            }
        };
        TopBarMenuItem.prototype.deactivate = function () {
            var me = this.ext;
            if(me.activated) {
                me.el.removeCls(me.activeCls);
                me.blur();
                me.activated = false;
                me.fireEvent('deactivate', me);
            }
        };
        TopBarMenuItem.prototype.onClick = function (e) {
            var me = this.ext;
            if(!me.href) {
                e.stopEvent();
            }
            if(me.disabled) {
                return false;
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
        };
        TopBarMenuItem.prototype.updateTitleContainer = function () {
            this.titleContainer.update({
                text1: this.text1,
                text2: this.text2
            });
        };
        return TopBarMenuItem;
    })();
    app_ui.TopBarMenuItem = TopBarMenuItem;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var TopBarMenu = (function () {
        function TopBarMenu(tabPanel) {
            var _this = this;
            var tbm = new Ext.menu.Menu({
                itemId: 'topBarMenu',
                cls: 'admin-topbar-menu',
                showSeparator: false,
                styleHtmlContent: true,
                overflowY: 'auto',
                overflowX: 'hidden',
                width: 300,
                layout: {
                    type: 'vbox',
                    align: 'stretchmax'
                }
            });
            this.ext = tbm;
            this.tabPanel = tabPanel;
            this.nonClosableItems = this.createNonClosableItems();
            this.editTitle = this.createEditTitle();
            this.editItems = this.createEditItems();
            this.viewTitle = this.createViewTitle();
            this.viewItems = this.createViewItems();
            this.emptyTitle = this.createEmptyTitle();
            tbm.add(this.nonClosableItems);
            tbm.add(this.editTitle);
            tbm.add(this.editItems);
            tbm.add(this.viewTitle);
            tbm.add(this.viewItems);
            tbm.add(this.emptyTitle);
            Ext.Function.interceptAfter(tbm, 'onShow', this.onShow, this);
            Ext.Function.interceptAfter(tbm, 'onBoxReady', this.onBoxReady, this);
            Ext.Function.interceptAfter(tbm, 'show', this.show, this);
            Ext.Function.interceptBefore(tbm, 'hide', this.hide, this);
            Ext.override(tbm, {
                scrollState: {
                    left: 0,
                    top: 0
                },
                onClick: function (e) {
                    return _this.onClick(e);
                },
                setVerticalPosition: function () {
                    _this.setVerticalPosition();
                }
            });
            tbm.on('closeMenuItem', this.onCloseMenuItem, this);
            tbm.on('resize', this.updatePosition, this);
        }
        TopBarMenu.prototype.createNonClosableItems = function () {
            var item = new Ext.container.Container({
                itemId: 'nonClosableItems'
            });
            return item;
        };
        TopBarMenu.prototype.createEditTitle = function () {
            var item = new Ext.Component({
                cls: 'title',
                itemId: 'editTitle',
                hidden: true,
                html: '<span>Editing</span>'
            });
            return item;
        };
        TopBarMenu.prototype.createEditItems = function () {
            var item = new Ext.container.Container({
                itemId: 'editItems'
            });
            return item;
        };
        TopBarMenu.prototype.createViewTitle = function () {
            var item = new Ext.Component({
                cls: 'title',
                itemId: 'viewTitle',
                hidden: true,
                html: '<span>Viewing</span>'
            });
            return item;
        };
        TopBarMenu.prototype.createViewItems = function () {
            var item = new Ext.container.Container({
                itemId: 'viewItems'
            });
            return item;
        };
        TopBarMenu.prototype.createEmptyTitle = function () {
            var item = new Ext.Component({
                cls: 'info',
                itemId: 'emptyTitle',
                html: 'List is empty'
            });
            return item;
        };
        TopBarMenu.prototype.onClick = function (e) {
            var me = this.ext, item;
            if(me.disabled) {
                e.stopEvent();
                return;
            }
            item = (e.type === 'click') ? this.getItemFromEvent(e) : me.activeItem;
            if(item && item.isMenuItem && item.onClick(e) !== false) {
                if(me.fireEvent('click', me, item, e) !== false && this.tabPanel) {
                    this.tabPanel.setActiveTab(item.card);
                }
                me.hide();
            }
        };
        TopBarMenu.prototype.onShow = function () {
            if(this.activeTab) {
                this.markActiveTab(this.activeTab);
            }
        };
        TopBarMenu.prototype.onBoxReady = function () {
            var tip = Ext.DomHelper.append(this.ext.el, {
                tag: 'div',
                cls: 'balloon-tip'
            }, true);
        };
        TopBarMenu.prototype.onCloseMenuItem = function (item) {
            if(this.tabPanel) {
                this.tabPanel.remove(item.card);
            }
            if(this.getAllItems(false).length === 0) {
                this.ext.hide();
            }
        };
        TopBarMenu.prototype.markActiveTab = function (item) {
            var me = this.ext;
            var menuItem;
            if(me.isVisible()) {
                menuItem = me.el.down('.current-tab');
                if(menuItem) {
                    menuItem.removeCls('current-tab');
                }
                if(item) {
                    menuItem = item;
                    if(menuItem && menuItem.el) {
                        menuItem.el.addCls('current-tab');
                    }
                }
            }
            this.activeTab = item;
        };
        TopBarMenu.prototype.getItemFromEvent = function (e) {
            var item = this.ext;
            do {
                item = item.getChildByElement(e.getTarget());
            }while(item && Ext.isDefined(item.getChildByElement) && item.isMenuItem !== true);
            return item;
        };
        TopBarMenu.prototype.getAllItems = function (includeNonClosable) {
            var items = [];
            if(includeNonClosable === false) {
                items = items.concat(this.editItems.query('*[isMenuItem=true]'));
                items = items.concat(this.viewItems.query('*[isMenuItem=true]'));
            } else {
                items = items.concat(this.ext.query('*[isMenuItem=true]'));
            }
            return items;
        };
        TopBarMenu.prototype.addItems = function (items) {
            if(Ext.isEmpty(items)) {
                return [];
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
                added = added.concat(this.nonClosableItems.add(nonClosableItems));
            }
            if(editItems.length > 0) {
                var editItemObjects = [];
                Ext.Array.each(editItems, function (editItem) {
                    if(!editItem.xtype) {
                        var tbmi = new app_ui.TopBarMenuItem(editItem.text1, editItem.text2, editItem.card, editItem.tabBar, editItem.closable, editItem.disabled, editItem.editing, editItem.hidden, editItem.iconClass, editItem.iconSrc).ext;
                        editItemObjects.push(tbmi);
                    } else {
                        editItemObjects.push(editItem);
                    }
                });
                added = added.concat(this.editItems.add(editItemObjects));
            }
            if(viewItems.length > 0) {
                var viewItemObjects = [];
                Ext.Array.each(viewItems, function (viewItem) {
                    if(!viewItem.xtype) {
                        var tbmi = new app_ui.TopBarMenuItem(viewItem.text1, viewItem.text2, viewItem.card, viewItem.tabBar, viewItem.closable, viewItem.disabled, viewItem.editing, viewItem.hidden, viewItem.iconClass, viewItem.iconSrc).ext;
                        viewItemObjects.push(tbmi);
                    } else {
                        viewItemObjects.push(viewItem);
                    }
                });
                added = added.concat(this.viewItems.add(viewItemObjects));
            }
            this.updateTitles();
            this.restoreScrollState();
            return added;
        };
        TopBarMenu.prototype.removeAllItems = function (includeNonClosable) {
            var me = this.ext;
            var editItems = this.editItems;
            var viewItems = this.viewItems;
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
                var nonClosableItems = this.nonClosableItems;
                Ext.Array.each(nonClosableItems.items.items, function (item) {
                    if(item && item.closable !== false) {
                        removed.push(nonClosableItems.remove(item));
                    }
                });
            }
            this.updateTitles();
            return removed;
        };
        TopBarMenu.prototype.removeItems = function (items) {
            if(Ext.isEmpty(items)) {
                return null;
            } else if(Ext.isObject(items)) {
                items = [].concat(items);
            }
            this.saveScrollState();
            var me = this.ext;
            var editItems = this.editItems;
            var viewItems = this.viewItems;
            var nonClosableItems = this.nonClosableItems;
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
            return removed;
        };
        TopBarMenu.prototype.updateTitles = function () {
            var editCount = this.editItems.items.getCount();
            var viewCount = this.viewItems.items.getCount();
            var nonClosableCount = this.nonClosableItems.items.getCount();
            if(editCount > 0) {
                this.editTitle.show();
            } else {
                this.editTitle.hide();
            }
            if(viewCount > 0) {
                this.viewTitle.show();
            } else {
                this.viewTitle.hide();
            }
            if((viewCount || editCount || nonClosableCount) > 0) {
                this.emptyTitle.hide();
            } else {
                this.emptyTitle.show();
            }
        };
        TopBarMenu.prototype.updatePosition = function (menu, width, height, oldWidth, oldHeight, opts) {
            this.ext.el.move('r', ((oldWidth - width) / 2), false);
        };
        TopBarMenu.prototype.show = function () {
            var me = this.ext, parentEl, viewHeight;
            this.maxWas = me.maxHeight;
            if(!me.rendered) {
                me.doAutoRender();
            }
            if(me.floating) {
                parentEl = Ext.fly(me.el.getScopeParent());
                viewHeight = parentEl.getViewSize().height;
                me.maxHeight = Math.min(this.maxWas || viewHeight - 50, viewHeight - 50);
            }
            return me;
        };
        TopBarMenu.prototype.hide = function () {
            this.ext.maxHeight = this.maxWas;
        };
        TopBarMenu.prototype.setVerticalPosition = function () {
        };
        TopBarMenu.prototype.saveScrollState = function () {
            var me = this.ext;
            if(me.rendered && !me.hidden) {
                var dom = me.body.dom, state = me.scrollState;
                state.left = dom.scrollLeft;
                state.top = dom.scrollTop;
            }
        };
        TopBarMenu.prototype.restoreScrollState = function () {
            var me = this.ext;
            if(me.rendered && !me.hidden) {
                var dom = me.body.dom, state = me.scrollState;
                dom.scrollLeft = state.left;
                dom.scrollTop = state.top;
            }
        };
        return TopBarMenu;
    })();
    app_ui.TopBarMenu = TopBarMenu;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var TopBar = (function () {
        function TopBar(appName, tabPanel) {
            this.ext = new Ext.toolbar.Toolbar({
                itemId: 'topBar',
                buttonAlign: 'center',
                cls: 'admin-topbar-panel',
                dock: 'top',
                plain: true,
                border: false
            });
            this.appName = appName;
            this.tabPanel = tabPanel;
            this.initComponent();
        }
        TopBar.prototype.initComponent = function () {
            var _this = this;
            var me = this.ext;
            this.startButton = Ext.create('Ext.button.Button', {
                xtype: 'button',
                itemId: 'app-launcher-button',
                margins: '0 8px 0 0',
                cls: 'start-button',
                handler: function (btn, evt) {
                    _this.toggleHomeScreen();
                }
            });
            this.homeButton = Ext.create('Ext.button.Button', {
                text: this.appName || '&lt; app name &gt;',
                cls: 'home-button',
                handler: function (btn, evt) {
                    if(_this.tabPanel) {
                        _this.tabPanel.setActiveTab(0);
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
                    this.startButton, 
                    {
                        xtype: "tbseparator",
                        width: '2px'
                    }, 
                    this.homeButton
                ]
            });
            this.rightContainer = Ext.create('Ext.Container', {
                flex: 5,
                layout: {
                    type: 'hbox',
                    align: 'middle',
                    pack: 'end'
                }
            });
            var adminImageButton = new app_ui.AdminImageButton(api_util.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'), '<div class="title">User</div>' + '<div class="user-name">{userName}</div>' + '<div class="content">' + '<div class="column"><img src="{photoUrl}"/>' + '<button class="x-btn-red-small">Log Out</button>' + '</div>' + '<div class="column">' + '<span>{qName}</span>' + '<a href="#">View Profile</a>' + '<a href="#">Edit Profile</a>' + '<a href="#">Change User</a>' + '</div>' + '</div>', {
                userName: "Thomas Lund Sigdestad",
                photoUrl: api_util.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'),
                qName: 'system/tsi'
            });
            this.rightContainer.add(adminImageButton.ext);
            me.add(this.leftContainer);
            me.add(this.rightContainer);
            if(this.tabPanel) {
                this.tabMenu = new app_ui.TopBarMenu(this.tabPanel);
                this.titleButton = Ext.create('Ext.button.Button', {
                    cls: 'title-button',
                    menuAlign: 't-b?',
                    menu: this.tabMenu.ext,
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
                me.insert(1, this.titleButton);
            }
            this.syncTabCount();
        };
        TopBar.prototype.toggleHomeScreen = function () {
            var isInsideIframe = window.top !== window.self;
            if(isInsideIframe) {
                window.parent['Ext'].getCmp('admin-home-main-container').toggleShowHide();
            } else {
                console.error('Can not toggle home screen. Document must be loaded inside the main window');
            }
        };
        TopBar.prototype.insert = function (index, cfg) {
            var added = this.tabMenu.addItems(cfg);
            this.syncTabCount();
            return added.length === 1 ? added[0] : added;
        };
        TopBar.prototype.setActiveTab = function (tab) {
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
        };
        TopBar.prototype.remove = function (tab) {
            var removed = this.tabMenu.removeItems(tab);
            this.syncTabCount();
            return removed;
        };
        TopBar.prototype.findNextActivatable = function () {
            if(this.tabPanel) {
                return this.tabPanel.items.get(0);
            }
            return null;
        };
        TopBar.prototype.createMenuItemFromTab = function (item) {
            var cfg = item.initialConfig || item;
            return {
                tabBar: this.ext,
                card: item,
                disabled: cfg.disabled,
                closable: cfg.closable,
                hidden: cfg.hidden && !item.hiddenByLayout,
                iconSrc: this.getMenuItemIcon(item),
                iconClass: cfg.iconClass,
                editing: cfg.editing || false,
                text1: Ext.String.ellipsis(this.getMenuItemDisplayName(item), 26),
                text2: Ext.String.ellipsis(this.getMenuItemDescription(item), 38)
            };
        };
        TopBar.prototype.syncTabCount = function () {
            if(this.tabMenu && this.titleButton) {
                var tabCount = this.tabMenu.getAllItems(false).length;
                this.titleButton.setVisible(tabCount > 0);
                this.titleButton.setCount(tabCount);
                api_notify.updateAppTabCount(this.getApplicationId(), tabCount);
            }
        };
        TopBar.prototype.getApplicationId = function () {
            var urlParamsString = document.URL.split('?'), urlParams = Ext.Object.fromQueryString(urlParamsString[urlParamsString.length - 1]);
            return urlParams.appId ? urlParams.appId.split('#')[0] : null;
        };
        TopBar.prototype.getMenuItemIcon = function (card) {
            var icon;
            if(card.data && card.data instanceof Ext.data.Model) {
                icon = card.data.get('iconUrl') || card.data.get('image_url');
            }
            return icon;
        };
        TopBar.prototype.getMenuItemDescription = function (card) {
            var desc;
            if(!card.isNew && card.data && card.data instanceof Ext.data.Model) {
                desc = card.data.get('path') || card.data.get('qualifiedName') || card.data.get('displayName');
            }
            if(!desc) {
                desc = card.title;
            }
            return desc;
        };
        TopBar.prototype.getMenuItemDisplayName = function (card) {
            var desc;
            if(!card.isNew && card.data && card.data instanceof Ext.data.Model) {
                desc = card.data.get('displayName') || card.data.get('name');
            }
            if(!desc) {
                desc = card.title;
            }
            return desc;
        };
        TopBar.prototype.setTitleButtonText = function (text) {
            this.titleButton.setTitle(text);
            var activeTab = this.titleButton.menu.activeTab;
            if(activeTab) {
                activeTab.text1 = text;
                activeTab.updateTitleContainer();
            }
        };
        TopBar.prototype.getStartButton = function () {
            return this.startButton;
        };
        TopBar.prototype.getLeftContainer = function () {
            return this.leftContainer;
        };
        TopBar.prototype.getRightContainer = function () {
            return this.rightContainer;
        };
        return TopBar;
    })();
    app_ui.TopBar = TopBar;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var TabPanel = (function () {
        function TabPanel(config) {
            var _this = this;
            this.ext = new Ext.tab.Panel({
                appName: config.appName,
                appIconCls: config.appIconCls,
                border: false,
                defaults: {
                    closable: true
                },
                addTab: function (item, index, requestConfig) {
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
                    (Ext.tab.Panel).superclass.doRemove.apply(this, arguments);
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
                },
                initComponent: function () {
                    var me = this, dockedItems = [].concat(me.dockedItems || []), activeTab = me.activeTab || (me.activeTab = 0);
                    me.layout = new Ext.layout.container.Card(Ext.apply({
                        owner: me,
                        deferredRender: me.deferredRender,
                        itemCls: me.itemCls,
                        activeItem: me.activeTab
                    }, me.layout));
                    this.tabBar = new app_ui.TopBar(me.appName, me);
                    dockedItems.push(this.tabBar.ext);
                    me.dockedItems = dockedItems;
                    me.addEvents('beforetabchange', 'tabchange');
                    me.superclass.superclass.initComponent.apply(me, arguments);
                    me.activeTab = me.getComponent(activeTab);
                    if(me.activeTab) {
                        me.activeTab.tab.activate(true);
                        me.tabBar.setActiveTab(me.activeTab.tab);
                    }
                }
            });
            app_event.EditSpaceEvent.on(function (event) {
                var spaces = event.getModels();
                for(var i in spaces) {
                    var space = spaces[i];
                    console.log(space);
                    api_remote.RemoteService.space_get({
                        "spaceName": [
                            space.get('name')
                        ]
                    }, function (r) {
                        _this.ext.el.unmask();
                        if(r) {
                            var id = _this.generateTabId(space, true);
                            var editing = true;
                            var title = space.get('displayName');
                            var data = space;
                            var spaceWizardPanel = new app_ui_wizard.SpaceWizardPanel2(id, title);
                            var index = _this.ext.items.indexOfKey(_this.generateTabId(space, false));
                            if(index >= 0) {
                                _this.ext.remove(index);
                            }
                            _this.addTab(spaceWizardPanel.ext, index >= 0 ? index : undefined, undefined);
                        } else {
                            console.error("Error", r ? r.error : "Unable to retrieve space.");
                        }
                    });
                }
            });
        }
        TabPanel.prototype.getExtEl = function () {
            return this.ext;
        };
        TabPanel.prototype.generateTabId = function (space, isEdit) {
            return 'tab-' + (isEdit ? 'edit-' : 'preview-') + space.get('name');
        };
        TabPanel.prototype.getTabCount = function () {
            return this.ext.getTabCount();
        };
        TabPanel.prototype.removeAllOpenTabs = function () {
            this.ext.removeAllOpenTabs();
        };
        TabPanel.prototype.addTab = function (item, index, requestConfig) {
            return this.ext.addTab(item, index, requestConfig);
        };
        return TabPanel;
    })();
    app_ui.TabPanel = TabPanel;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var FilterPanel = (function () {
        function FilterPanel(config) {
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
            var updateFacets = function (facets) {
                if(facets) {
                    this.selectedValues = this.getValues();
                    this.down('#facetContainer').update(facets);
                    this.setValues(this.selectedValues);
                }
            };
            var getValues = function () {
                var selectedCheckboxes = Ext.query('.admin-facet-group input[type=checkbox]:checked', this.facetContainer.el.dom);
                var values = {
                };
                if(this.searchField) {
                    var query = this.searchField.getValue();
                    if(Ext.String.trim(query).length > 0) {
                        values[this.searchField.name] = query;
                    }
                }
                Ext.Array.each(selectedCheckboxes, function (cb, index, all) {
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
            var setValues = function (values) {
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
            var isValueChecked = function (value, values) {
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
            var isDirty = function () {
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
            var search = function () {
                if(this.fireEvent('search', this.getValues()) !== false) {
                    this.clearLink.el.setStyle('visibility', this.isDirty() ? 'visible' : 'hidden');
                }
            };
            var includeSearch = config && Ext.isDefined(config.includeSearch) ? config.includeSearch : true;
            var fp = this.ext = new Ext.panel.Panel({
                region: config ? config.region : undefined,
                width: config ? config.width : undefined,
                cls: 'admin-filter',
                header: false,
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                autoScroll: true,
                split: true,
                facetCountMap: [],
                includeSearch: includeSearch,
                includeEmptyFacets: 'none',
                updateCountCriteria: 'always',
                updateCountStrategy: 'notlast',
                originalTitle: '',
                updateFacets: updateFacets,
                getValues: getValues,
                setValues: setValues,
                isValueChecked: isValueChecked,
                isDirty: isDirty,
                search: search
            });
            var facetContainer = this.createFacetContainer();
            fp.insert(0, facetContainer);
            var clearLink = this.createClearLink();
            fp.insert(0, clearLink);
            Ext.apply(fp, {
                facetContainer: facetContainer,
                clearLink: clearLink
            });
            if(includeSearch) {
                var searchField = this.createSearchField();
                fp.insert(0, searchField);
                Ext.apply(fp, {
                    searchField: searchField
                });
            }
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
            var facetContainer = new Ext.Component({
                itemId: 'facetContainer',
                tpl: new Ext.XTemplate(this.facetTpl, {
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
                }),
                data: this.facetData
            });
            facetContainer.on('afterrender', function (cmp) {
                cmp.el.on('click', onFacetClicked, fp, {
                    delegate: '.admin-facet'
                });
            });
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
            var clearLink = new Ext.Component({
                html: '<a href="javascript:;">Clear filter</a>'
            });
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
            var searchField = new Ext.form.field.Text({
                cls: 'admin-search-trigger',
                enableKeyEvents: true,
                bubbleEvents: [
                    'specialkey'
                ],
                itemId: 'filterText',
                margin: '0 0 10 0',
                name: 'query',
                emptyText: 'Search'
            });
            searchField.on('specialkey', onKeyPressed, this.ext);
            searchField.on('keypress', onKeyPressed, this.ext);
            return searchField;
        };
        FilterPanel.prototype.getExtEl = function () {
            return this.ext;
        };
        return FilterPanel;
    })();
    app_ui.FilterPanel = FilterPanel;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var BrowseToolbar = (function (_super) {
        __extends(BrowseToolbar, _super);
        function BrowseToolbar() {
                _super.call(this);
            _super.prototype.addAction.call(this, app.SpaceActions.NEW_SPACE);
            _super.prototype.addAction.call(this, app.SpaceActions.EDIT_SPACE);
            _super.prototype.addAction.call(this, app.SpaceActions.OPEN_SPACE);
            _super.prototype.addAction.call(this, app.SpaceActions.DELETE_SPACE);
        }
        return BrowseToolbar;
    })(api_ui_toolbar.Toolbar);
    app_ui.BrowseToolbar = BrowseToolbar;    
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var SpaceAppBar = (function (_super) {
        __extends(SpaceAppBar, _super);
        function SpaceAppBar() {
                _super.call(this, "Space Admin");
        }
        return SpaceAppBar;
    })(api_appbar.AppBar);
    app_ui.SpaceAppBar = SpaceAppBar;    
})(app_ui || (app_ui = {}));
Ext.define('Admin.controller.Controller', {
    extend: 'Ext.app.Controller',
    stores: [],
    models: [
        'Admin.model.SpaceModel'
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
        var spaceWizardPanel = new app_ui_wizard.SpaceWizardPanel('new-space', 'New Space', true);
        tabs.addTab(spaceWizardPanel.ext);
    },
    viewSpace: function (space) {
        space = this.validateSpace(space);
        var me = this;
        var tabs = this.getCmsTabPanel();
        var activeTab = tabs.setActiveTab(me.generateTabId(space, true));
        if(!activeTab) {
            var id = this.generateTabId(space, false);
        }
    },
    editSpace: function (space) {
        space = this.validateSpace(space);
        var me = this;
        var tabs = this.getCmsTabPanel();
        tabs.el.mask();
        api_remote.RemoteService.space_get({
            "spaceName": [
                space.data.name
            ]
        }, function (r) {
            tabs.el.unmask();
            if(r) {
                var id = me.generateTabId(space, true);
                var editing = true;
                var title = space.data.displayName;
                var data = space;
                var spaceWizardPanel = new app_ui_wizard.SpaceWizardPanel(id, title, editing, data);
                var index = tabs.items.indexOfKey(me.generateTabId(space, false));
                if(index >= 0) {
                    tabs.remove(index);
                }
                tabs.addTab(spaceWizardPanel.ext, index >= 0 ? index : undefined, undefined);
            } else {
                console.error("Error", r ? r.error : "Unable to retrieve space.");
            }
        });
    },
    validateSpace: function (space) {
        if(!space) {
            var showPanel = this.getSpaceTreeGridPanel();
            return showPanel.getSelection()[0];
        }
        return space;
    },
    updateDetailPanel: function (selected) {
    },
    updateToolbarButtons: function (selected) {
        var enable = selected && selected.length > 0;
        var toolbar = this.getSpaceBrowseToolbar();
        var buttons = Ext.ComponentQuery.query('button[action=viewSpace], ' + 'button[action=editSpace] ', toolbar);
        Ext.Array.each(buttons, function (button, index, all) {
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
        return components.gridPanel;
    },
    getSpaceDetailPanel: function () {
        return components.detailPanel;
    },
    getCmsTabPanel: function () {
        return components.tabPanel;
    },
    getTopBar: function () {
        return this.getCmsTabPanel().tabBar;
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
        api_remote.RemoteService.space_createOrUpdate(spaceParams, function (r) {
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
        api_remote.RemoteService.space_delete({
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
    views: [],
    contextMenu: null,
    init: function () {
        this.contextMenu = new app_ui.ContextMenuGridPanel();
        this.control({
            '#spaceTreeGrid gridpanel, #spaceTreeGrid treepanel': {
                selectionchange: this.onGridSelectionChange,
                itemcontextmenu: this.showContextMenu,
                itemdblclick: function (grid, record) {
                    this.editSpace(record);
                }
            },
            '#spaceContextMenu *[action=editSpace]': {
                click: function (el, e) {
                    this.editSpace();
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
            this.contextMenu = new app_ui.ContextMenuGridPanel();
        }
        return this.contextMenu;
    }
});
Ext.define('Admin.controller.BrowseToolbarController', {
    extend: 'Admin.controller.Controller',
    stores: [],
    models: [],
    views: [],
    init: function () {
        var _this = this;
        app_event.OpenSpaceEvent.on(function (event) {
            _this.viewSelectedSpaces();
        });
        this.control({
            '#spaceBrowseToolbar *[action=newSpace]': {
                click: function (button, event) {
                    this.showNewSpaceWindow();
                }
            },
            '#spaceBrowseToolbar *[action=viewSpace]': {
                click: function (button, event) {
                    this.viewSelectedSpaces();
                }
            },
            '#spaceBrowseToolbar *[action=editSpace]': {
                click: function (button, event) {
                    this.editSelectedSpaces();
                }
            }
        });
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
    views: [],
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
            }
        });
    }
});
Ext.define('Admin.controller.WizardController', {
    extend: 'Admin.controller.SpaceController',
    stores: [],
    models: [],
    views: [],
    init: function () {
        var _this = this;
        var me = this;
        app_event.NewSpaceEvent.on(function (event) {
            _this.showNewSpaceWindow();
        });
        me.control({
            '#spaceAdminWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            '#spaceAdminWizardPanel *[action=saveSpace]': {
                click: function (el) {
                    new app_event.SaveSpaceEvent().fire();
                }
            },
            '#spaceAdminWizardPanel *[action=deleteSpace]': {
                click: function () {
                    this.deleteSpace(this.getWizardTab());
                }
            },
            '#spaceAdminWizardPanel #wizardHeader': {
                displaynamechange: function (newVal, oldVal) {
                    this.getTopBar().setTitleButtonText(newVal);
                }
            },
            '#spaceAdminWizardPanel': {
                'validitychange': function (wizard, isValid) {
                    this.updateWizardToolbarButtons(wizard.isWizardDirty, isValid);
                },
                'dirtychange': function (wizard, isDirty) {
                    this.updateWizardToolbarButtons(isDirty, wizard.isWizardValid);
                }
            }
        });
        app_event.SaveSpaceEvent.on(function (event) {
            me.saveSpace();
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
    saveSpace: function () {
        var me = this;
        var spaceWizardPanel = me.getWizardPanel();
        var spaceWizardData = spaceWizardPanel.getData();
        var displayName = spaceWizardData.displayName;
        var spaceName = spaceWizardData.spaceName;
        var iconReference = spaceWizardData.iconRef;
        var spaceModel = spaceWizardPanel.data;
        var originalSpaceName = spaceModel && (spaceModel.get ? spaceModel.get('name') : spaceModel.name);
        var spaceParams = {
            spaceName: originalSpaceName || spaceName,
            displayName: displayName,
            iconReference: iconReference,
            newSpaceName: (originalSpaceName !== spaceName) ? spaceName : undefined
        };
        var onUpdateSpaceSuccess = function (created, updated) {
            if(created || updated) {
                api_notify.showFeedback('Space "' + spaceName + '" was saved');
                me.getSpaceTreeGridPanel().refresh();
                me.getWizardPanel().isWizardDirty = false;
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
                api_notify.showFeedback('Space was deleted');
            }
        };
        this.remoteDeleteSpace(space, onDeleteSpaceSuccess);
    },
    getWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },
    getWizardPanel: function () {
        return this.getWizardTab().wrapper;
    },
    getWizardToolbar: function () {
        return Ext.ComponentQuery.query('#spaceWizardToolbar', this.getWizardTab())[0];
    }
});
var app_appbar;
(function (app_appbar) {
    var SpaceTabMenuItem = (function (_super) {
        __extends(SpaceTabMenuItem, _super);
        function SpaceTabMenuItem() {
            _super.apply(this, arguments);

        }
        return SpaceTabMenuItem;
    })(api_ui_tab.TabMenuItem);
    app_appbar.SpaceTabMenuItem = SpaceTabMenuItem;    
})(app_appbar || (app_appbar = {}));
var app;
(function (app) {
    var SpaceAppBarTabMenu = (function () {
        function SpaceAppBarTabMenu() {
            app_event.NewSpaceEvent.on(function (event) {
            });
        }
        return SpaceAppBarTabMenu;
    })();
    app.SpaceAppBarTabMenu = SpaceAppBarTabMenu;    
})(app || (app = {}));
var app;
(function (app) {
    var SpaceAppPanelController = (function (_super) {
        __extends(SpaceAppPanelController, _super);
        function SpaceAppPanelController() {
            var _this = this;
            var appBarTabMenu = new api_ui_tab.TabMenu();
            var appPanel = new api_ui.DeckPanel();
                _super.call(this, appBarTabMenu, appPanel);
            app_event.NewSpaceEvent.on(function (event) {
                var tabMenuItem = new app_appbar.SpaceTabMenuItem();
                var panel = new api_ui.Panel();
                _this.addPanel(panel, tabMenuItem);
            });
            app_event.OpenSpaceEvent.on(function (event) {
                var tabMenuItem = new app_appbar.SpaceTabMenuItem();
                var panel = new api_ui.Panel();
                _this.addPanel(panel, tabMenuItem);
            });
        }
        return SpaceAppPanelController;
    })(api_ui_tab.TabPanelController);
    app.SpaceAppPanelController = SpaceAppPanelController;    
})(app || (app = {}));
var app;
(function (app) {
    app.id = 'space-manager';
})(app || (app = {}));
var components;
(function (components) {
    components.detailPanel;
    components.gridPanel;
    components.tabPanel;
    components.deleteWindow;
})(components || (components = {}));
Ext.application({
    name: 'spaceAdmin',
    controllers: [
        'Admin.controller.FilterPanelController', 
        'Admin.controller.GridPanelController', 
        'Admin.controller.BrowseToolbarController', 
        'Admin.controller.DetailPanelController', 
        'Admin.controller.DetailToolbarController', 
        'Admin.controller.WizardController'
    ],
    stores: [],
    launch: function () {
        var spaceAppMainPanel = new app.SpaceAppBrowsePanel();
        var tabPanel = components.tabPanel = new app_ui.TabPanel({
            appName: 'Space Admin',
            appIconCls: 'icon-metro-space-admin-24'
        }).getExtEl();
        tabPanel.add(spaceAppMainPanel.ext);
        var viewPort = new Ext.container.Viewport({
            layout: 'fit',
            cls: 'admin-viewport'
        });
        viewPort.add(tabPanel);
        var deleteSpaceDialog = new app_ui.DeleteSpaceDialog();
        app_event.DeletePromptEvent.on(function (event) {
            deleteSpaceDialog.setSpacesToDelete(event.getModels());
            deleteSpaceDialog.open();
        });
    }
});
app.SpaceContext.init();
app.SpaceActions.init();
//@ sourceMappingURL=all.js.map
