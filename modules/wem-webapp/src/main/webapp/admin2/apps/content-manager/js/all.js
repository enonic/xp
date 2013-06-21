var __extends = this.__extends || function (d, b) {
    function __() {
        this.constructor = d;
    }

    __.prototype = b.prototype;
    d.prototype = new __();
};
var app_event;
(function (app_event) {
    var BaseContentModelEvent = (function (_super) {
        __extends(BaseContentModelEvent, _super);
        function BaseContentModelEvent(name, model) {
            this.model = model;
            _super.call(this, name);
        }

        BaseContentModelEvent.prototype.getModels = function () {
            return this.model;
        };
        return BaseContentModelEvent;
    })(api_event.Event);
    app_event.BaseContentModelEvent = BaseContentModelEvent;
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
    })(app_event.BaseContentModelEvent);
    app_event.GridSelectionChangeEvent = GridSelectionChangeEvent;
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var NewContentEvent = (function (_super) {
        __extends(NewContentEvent, _super);
        function NewContentEvent() {
            _super.call(this, 'newContent');
        }

        NewContentEvent.on = function on(handler) {
            api_event.onEvent('newContent', handler);
        };
        return NewContentEvent;
    })(api_event.Event);
    app_event.NewContentEvent = NewContentEvent;
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var OpenContentEvent = (function (_super) {
        __extends(OpenContentEvent, _super);
        function OpenContentEvent(model) {
            _super.call(this, 'openContent', model);
        }

        OpenContentEvent.on = function on(handler) {
            api_event.onEvent('openContent', handler);
        };
        return OpenContentEvent;
    })(app_event.BaseContentModelEvent);
    app_event.OpenContentEvent = OpenContentEvent;
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var EditContentEvent = (function (_super) {
        __extends(EditContentEvent, _super);
        function EditContentEvent(model) {
            _super.call(this, 'editContent', model);
        }

        EditContentEvent.on = function on(handler) {
            api_event.onEvent('editContent', handler);
        };
        return EditContentEvent;
    })(app_event.BaseContentModelEvent);
    app_event.EditContentEvent = EditContentEvent;
})(app_event || (app_event = {}));
var app_event;
(function (app_event) {
    var DeleteContentEvent = (function (_super) {
        __extends(DeleteContentEvent, _super);
        function DeleteContentEvent(model) {
            _super.call(this, 'deleteContent', model);
        }

        DeleteContentEvent.on = function on(handler) {
            api_event.onEvent('deleteContent', handler);
        };
        return DeleteContentEvent;
    })(app_event.BaseContentModelEvent);
    app_event.DeleteContentEvent = DeleteContentEvent;
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
var app;
(function (app) {
    var ContentContext = (function () {
        function ContentContext() {
            var _this = this;
            app_event.GridSelectionChangeEvent.on(function (event) {
                _this.selectedContents = event.getModels();
            });
        }

        ContentContext.init = function init() {
            return ContentContext.context = new ContentContext();
        };
        ContentContext.get = function get() {
            return ContentContext.context;
        };
        ContentContext.prototype.getSelectedContents = function () {
            return this.selectedContents;
        };
        return ContentContext;
    })();
    app.ContentContext = ContentContext;
})(app || (app = {}));
var app;
(function (app) {
    var NewContentAction = (function (_super) {
        __extends(NewContentAction, _super);
        function NewContentAction() {
            _super.call(this, "New");
            this.addExecutionListener(function () {
                new app_event.NewContentEvent().fire();
            });
        }

        return NewContentAction;
    })(api_ui.Action);
    app.NewContentAction = NewContentAction;
    var OpenContentAction = (function (_super) {
        __extends(OpenContentAction, _super);
        function OpenContentAction() {
            _super.call(this, "Open");
            this.setEnabled(false);
            this.addExecutionListener(function () {
                new app_event.OpenContentEvent(app.ContentContext.get().getSelectedContents()).fire();
            });
        }

        return OpenContentAction;
    })(api_ui.Action);
    app.OpenContentAction = OpenContentAction;
    var EditContentAction = (function (_super) {
        __extends(EditContentAction, _super);
        function EditContentAction() {
            _super.call(this, "Edit");
            this.setEnabled(false);
            this.addExecutionListener(function () {
                new app_event.EditContentEvent(app.ContentContext.get().getSelectedContents()).fire();
            });
        }

        return EditContentAction;
    })(api_ui.Action);
    app.EditContentAction = EditContentAction;
    var DeleteContentAction = (function (_super) {
        __extends(DeleteContentAction, _super);
        function DeleteContentAction() {
            _super.call(this, "Delete");
            this.setEnabled(false);
            this.addExecutionListener(function () {
                new app_event.DeleteContentEvent(app.ContentContext.get().getSelectedContents()).fire();
            });
        }

        return DeleteContentAction;
    })(api_ui.Action);
    app.DeleteContentAction = DeleteContentAction;
    var DuplicateContentAction = (function (_super) {
        __extends(DuplicateContentAction, _super);
        function DuplicateContentAction() {
            _super.call(this, "Duplicate");
            this.setEnabled(false);
            this.addExecutionListener(function () {
                console.log('TODO: Duplicate content');
            });
        }

        return DuplicateContentAction;
    })(api_ui.Action);
    app.DuplicateContentAction = DuplicateContentAction;
    var MoveContentAction = (function (_super) {
        __extends(MoveContentAction, _super);
        function MoveContentAction() {
            _super.call(this, "Move");
            this.setEnabled(false);
            this.addExecutionListener(function () {
                console.log('TODO: Move content');
            });
        }

        return MoveContentAction;
    })(api_ui.Action);
    app.MoveContentAction = MoveContentAction;
    var BrowseContentSettingsAction = (function (_super) {
        __extends(BrowseContentSettingsAction, _super);
        function BrowseContentSettingsAction() {
            _super.call(this, "");
            this.setEnabled(true);
            this.setIconClass('icon-toolbar-settings');
            this.addExecutionListener(function () {
                console.log('TODO: browse content settings');
            });
        }

        return BrowseContentSettingsAction;
    })(api_ui.Action);
    app.BrowseContentSettingsAction = BrowseContentSettingsAction;
    var ContentActions = (function () {
        function ContentActions() {
        }

        ContentActions.NEW_CONTENT = new NewContentAction();
        ContentActions.OPEN_CONTENT = new OpenContentAction();
        ContentActions.EDIT_CONTENT = new EditContentAction();
        ContentActions.DELETE_CONTENT = new DeleteContentAction();
        ContentActions.DUPLICATE_CONTENT = new DuplicateContentAction();
        ContentActions.MOVE_CONTENT = new MoveContentAction();
        ContentActions.BROWSE_CONTENT_SETTINGS = new BrowseContentSettingsAction();
        ContentActions.init = function init() {
            app_event.GridSelectionChangeEvent.on(function (event) {
                var contents = event.getModels();
                if (contents.length <= 0) {
                    ContentActions.NEW_CONTENT.setEnabled(true);
                    ContentActions.OPEN_CONTENT.setEnabled(false);
                    ContentActions.EDIT_CONTENT.setEnabled(false);
                    ContentActions.DELETE_CONTENT.setEnabled(false);
                    ContentActions.DUPLICATE_CONTENT.setEnabled(false);
                    ContentActions.MOVE_CONTENT.setEnabled(false);
                } else if (contents.length == 1) {
                    ContentActions.NEW_CONTENT.setEnabled(false);
                    ContentActions.OPEN_CONTENT.setEnabled(true);
                    ContentActions.EDIT_CONTENT.setEnabled(contents[0].data.editable);
                    ContentActions.DELETE_CONTENT.setEnabled(contents[0].data.deletable);
                    ContentActions.DUPLICATE_CONTENT.setEnabled(true);
                    ContentActions.MOVE_CONTENT.setEnabled(true);
                } else {
                    ContentActions.NEW_CONTENT.setEnabled(false);
                    ContentActions.OPEN_CONTENT.setEnabled(true);
                    ContentActions.EDIT_CONTENT.setEnabled(ContentActions.anyEditable(contents));
                    ContentActions.DELETE_CONTENT.setEnabled(ContentActions.anyDeleteable(contents));
                    ContentActions.DUPLICATE_CONTENT.setEnabled(true);
                    ContentActions.MOVE_CONTENT.setEnabled(true);
                }
            });
        };
        ContentActions.anyEditable = function anyEditable(contents) {
            for (var i in contents) {
                var content = contents[i];
                if (content.data.editable) {
                    return true;
                }
            }
            return false;
        };
        ContentActions.anyDeleteable = function anyDeleteable(contents) {
            for (var i in contents) {
                var content = contents[i];
                if (content.data.deletable) {
                    return true;
                }
            }
            return false;
        };
        return ContentActions;
    })();
    app.ContentActions = ContentActions;
})(app || (app = {}));
Ext.define('Ext.ux.toggleslide.Thumb', {
    topZIndex: 10000,
    constructor: function (config) {
        var me = this;
        Ext.apply(me, config || {
        }, {
            cls: (Ext).baseCSSPrefix + 'toggle-slide-thumb',
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
        me.thumb = new (Ext).ux.toggleslide.Thumb({
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
            (Ext).ux.toggleslide.ToggleSlide.superclass.disable.call(me);
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
            (Ext).ux.toggleslide.ToggleSlide.superclass.enable.call(this);
            this.registerToggleListeners();
        }
        return this;
    },
    disable: function () {
        if (!this.disabled) {
            (Ext).ux.toggleslide.ToggleSlide.superclass.disable.call(this);
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
                    api_remote.RemoteService.content_delete({
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
        var FormComponent = (function () {
            function FormComponent() {
                var panel = new Ext.form.Panel();
                this.ext = panel;
            }

            FormComponent.prototype.getContentData = function () {
                var contentData = new api_content_data.ContentData();
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
var admin;
(function (admin) {
    (function (ui) {
        var TextLine = (function (_super) {
            __extends(TextLine, _super);
            function TextLine(input) {
                _super.call(this, input);
                var fieldContainer = Ext.create('Ext.form.FieldContainer');
                fieldContainer.setFieldLabel('');
                fieldContainer.labelWidth = 110;
                fieldContainer.labelPad = 0;
                var textField = Ext.create('Ext.form.Text');
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
        return new Ext.button.Cycle({
            showText: true,
            prependText: 'Order by ',
            menu: {
                items: menuItems
            }
        });
    },
    createOrderByDirectionButton: function () {
        return new Ext.button.Cycle({
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
        directFn: api_remote.RemoteService.content_find,
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
        directFn: api_remote.RemoteService.content_tree,
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
        directFn: api_remote.RemoteService.contentType_list,
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
        directFn: api_remote.RemoteService.contentType_tree,
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
Ext.define('Admin.lib.Sortable', {
    constructor: function (parentComponent, config) {
        var me = this;
        me.config = config || {
        };
        me.parentComponent = parentComponent;
        me.id = Ext.id();
        me.group = me.config.group || '' + me.id;
        me.indicatorEl = me.createDDIndicator(config.name);
        Ext.dd.ScrollManager.register(this.parentComponent.el);
        me.initDragZone();
        me.initDropZone();
    },
    initDragZone: function () {
        var sortable = this;
        var dragZone = new Ext.dd.DragZone(sortable.parentComponent.getEl(), {
            ddGroup: sortable.group,
            containerScroll: true,
            proxy: new Ext.dd.StatusProxy({
            }),
            getDragData: function (event) {
                var sourceDomEl = event.getTarget('.admin-sortable');
                if (!sourceDomEl) {
                    return;
                }
                if (sortable.config.handle && !Ext.fly(sourceDomEl).down(sortable.config.handle).contains(Ext.fly(event.getTarget()))) {
                    return;
                }
                return {
                    ddel: sortable.createDragProxy(sourceDomEl),
                    sourceElement: sourceDomEl,
                    repairXY: Ext.fly(sourceDomEl).getXY()
                };
            },
            onInitDrag: function (x, y) {
                this.proxy.update(this.dragData.ddel.cloneNode(true));
                Ext.fly(this.dragData.sourceElement).hide();
                this.onStartDrag(x, y);
                return true;
            },
            beforeDragOver: function () {
                return true;
            },
            onMouseUp: function (event) {
                Ext.fly(this.dragData.sourceElement).setStyle('opacity', '1');
            },
            beforeInvalidDrop: function (event, id) {
                Ext.fly(this.dragData.sourceElement).show();
            },
            afterInvalidDrop: function (event, id) {
                sortable.hideIndicator();
            },
            getRepairXY: function () {
                return this.dragData.repairXY;
            }
        });
    },
    initDropZone: function () {
        var sortable = this;
        var dropZone = new Ext.dd.DropZone(sortable.parentComponent.getEl(), {
            ddGroup: sortable.group,
            getTargetFromEvent: function (e) {
                return e.getTarget('.admin-sortable');
            },
            onNodeOver: function (target, dd, event, data) {
                var cmpNode = Ext.getCmp(target.id);
                if (!cmpNode) {
                    return;
                }
                if (target === data.sourceElement) {
                    return;
                }
                if (!cmpNode.hasCls('admin-drop-indicator')) {
                    var mouseYPos = event.getY();
                    var componentElementBox = cmpNode.getEl().getPageBox();
                    var nodeMiddle = componentElementBox.top + componentElementBox.height / 2;
                    if (mouseYPos < nodeMiddle) {
                        sortable.currentPos = 'above';
                    } else {
                        sortable.currentPos = 'below';
                    }
                    sortable.showIndicator(cmpNode, sortable.currentPos);
                }
                return Ext.dd.DropZone.prototype.dropAllowed;
            },
            onNodeDrop: function (target, dd, event, data) {
                var draggedCmp = Ext.getCmp(data.sourceElement.id);
                var targetCmp = Ext.getCmp(target.id);
                if (target === data.sourceElement) {
                    return;
                }
                draggedCmp.getEl().setStyle('opacity', 1);
                if (targetCmp) {
                    var targetCmpIndex = sortable.getIndexOfComponent(targetCmp);
                    var draggedCmpOrgIndex = sortable.getIndexOfComponent(draggedCmp);
                    if (sortable.currentPos === 'below') {
                        targetCmpIndex = targetCmpIndex + 1;
                        if (draggedCmpOrgIndex < targetCmpIndex) {
                            targetCmpIndex = targetCmpIndex - 1;
                        }
                    }
                    sortable.parentComponent.insert(targetCmpIndex, draggedCmp);
                    sortable.parentComponent.doLayout();
                }
                draggedCmp.getEl().show();
                sortable.hideIndicator();
                return true;
            }
        });
    },
    createDDIndicator: function (name) {
        var me = this, indicatorEl, arrowLeft;
        indicatorEl = Ext.create('widget.component', {
            html: '<div>Drop ' + name + ' here </div>',
            cls: 'admin-drop-indicator admin-sortable'
        });
        return indicatorEl;
    },
    showIndicator: function (area, position) {
        this.indicatorEl.show();
        var index = this.getIndexOfComponent(area);
        if (index > -1) {
            var insertPoint = position === 'above' ? index : index + 1;
            this.parentComponent.insert(insertPoint, this.indicatorEl);
        }
    },
    hideIndicator: function () {
        this.indicatorEl.hide();
        this.parentComponent.remove(this.indicatorEl, false);
    },
    createDragProxy: function (sourceElement) {
        var proxyEl;
        if (this.config.proxyHtml) {
            proxyEl = Ext.get(document.createElement('div'));
            proxyEl.setHTML(this.config.proxyHtml);
            proxyEl = proxyEl.dom;
        } else {
            proxyEl = sourceElement.cloneNode(true);
        }
        return proxyEl;
    },
    getIndexOfComponent: function (cmp) {
        return this.parentComponent.items.indexOf(cmp);
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
var app_ui;
(function (app_ui) {
    var DeleteContentDialog = (function (_super) {
        __extends(DeleteContentDialog, _super);
        function DeleteContentDialog() {
            var _this = this;
            _super.call(this, "Delete");
            this.deleteAction = new DeleteContentAction();
            this.deleteHandler = new api_handler.DeleteContentHandler();
            this.setDeleteAction(this.deleteAction);
            var deleteCallback = function (obj, success, result) {
                _this.close();
                api_notify.showFeedback('Content was deleted!');
            };
            this.deleteAction.addExecutionListener(function () {
                _this.deleteHandler.doDelete(api_handler.DeleteContentParamFactory.create(_this.contentToDelete), deleteCallback);
            });
            document.body.appendChild(this.getHTMLElement());
        }

        DeleteContentDialog.prototype.setContentToDelete = function (contentModels) {
            this.contentToDelete = contentModels;
            var deleteItems = [];
            for (var i in contentModels) {
                var contentModel = contentModels[i];
                var deleteItem = new api_delete.DeleteItem(contentModel.data.iconUrl, contentModel.data.displayName);
                deleteItems.push(deleteItem);
            }
            this.setDeleteItems(deleteItems);
        };
        return DeleteContentDialog;
    })(api_delete.DeleteDialog);
    app_ui.DeleteContentDialog = DeleteContentDialog;
    var DeleteContentAction = (function (_super) {
        __extends(DeleteContentAction, _super);
        function DeleteContentAction() {
            _super.call(this, "Delete");
        }

        return DeleteContentAction;
    })(api_ui.Action);
    app_ui.DeleteContentAction = DeleteContentAction;
})(app_ui || (app_ui = {}));
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
            if (this.singleSelection.getTabs().length > 0) {
                this.changeTab(this.singleSelection.getTabs()[0].name);
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
        var actionsButton = this.down('#actionMenu');
        if (actionsButton) {
            actionsButton.setVisible(false);
        }
    },
    getActionButton: function () {
        return Ext.apply(new app_ui.ActionMenu().getExt(), {
            itemId: 'actionMenu',
            text: 'Actions',
            height: 30,
            width: 120,
            tdAttrs: {
                width: 120,
                valign: 'top',
                style: {
                    padding: '0 20px 0 0'
                }
            }
        });
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
            data: me.singleSelection.getTabs(),
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
        var tabs = this.singleSelection.getTabs();
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
            target.removeAll();
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
        var urlParamsString = document.URL.split('?'), urlParams = Ext.Object.fromQueryString(urlParamsString[urlParamsString.length - 1]);
        return urlParams.appId ? urlParams.appId.split('#')[0] : null;
    },
    getMenuItemIcon: function (card) {
        var icon;
        if (card.data && card.data instanceof (Ext.data.Model)) {
            icon = card.data.get('iconUrl') || card.data.get('image_url');
        }
        return icon;
    },
    getMenuItemDescription: function (card) {
        var desc;
        if (!card.isNew && card.data && card.data instanceof (Ext.data.Model)) {
            desc = card.data.get('path') || card.data.get('qualifiedName') || card.data.get('displayName');
        }
        if (!desc) {
            desc = card.title;
        }
        return desc;
    },
    getMenuItemDisplayName: function (card) {
        var desc;
        if (!card.isNew && card.data && card.data instanceof (Ext.data.Model)) {
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
            return true;
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
            return true;
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
                return true;
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
var app_ui;
(function (app_ui) {
    var BrowseToolbar = (function (_super) {
        __extends(BrowseToolbar, _super);
        function BrowseToolbar() {
            _super.call(this);
            this.isLiveMode = false;
            _super.prototype.addAction.call(this, app.ContentActions.NEW_CONTENT);
            _super.prototype.addAction.call(this, app.ContentActions.EDIT_CONTENT);
            _super.prototype.addAction.call(this, app.ContentActions.OPEN_CONTENT);
            _super.prototype.addAction.call(this, app.ContentActions.DELETE_CONTENT);
            _super.prototype.addAction.call(this, app.ContentActions.DUPLICATE_CONTENT);
            _super.prototype.addAction.call(this, app.ContentActions.MOVE_CONTENT);
            _super.prototype.addGreedySpacer.call(this);
            _super.prototype.addAction.call(this, app.ContentActions.BROWSE_CONTENT_SETTINGS);
            var displayModeToggle = new api_ui_toolbar.ToggleSlide('PREVIEW', 'DETAILS', false);
            _super.prototype.addElement.call(this, displayModeToggle);
        }

        return BrowseToolbar;
    })(api_ui_toolbar.Toolbar);
    app_ui.BrowseToolbar = BrowseToolbar;
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var ActionMenu = (function (_super) {
        __extends(ActionMenu, _super);
        function ActionMenu() {
            _super.call(this, app.ContentActions.NEW_CONTENT, app.ContentActions.EDIT_CONTENT, app.ContentActions.OPEN_CONTENT,
                app.ContentActions.DELETE_CONTENT, app.ContentActions.DUPLICATE_CONTENT, app.ContentActions.MOVE_CONTENT);
        }

        return ActionMenu;
    })(api_ui_menu.ActionMenu);
    app_ui.ActionMenu = ActionMenu;
})(app_ui || (app_ui = {}));
var app_ui;
(function (app_ui) {
    var ContextMenu = (function (_super) {
        __extends(ContextMenu, _super);
        function ContextMenu() {
            _super.call(this, app.ContentActions.NEW_CONTENT, app.ContentActions.EDIT_CONTENT, app.ContentActions.OPEN_CONTENT,
                app.ContentActions.DELETE_CONTENT, app.ContentActions.DUPLICATE_CONTENT, app.ContentActions.MOVE_CONTENT);
        }

        return ContextMenu;
    })(api_ui_menu.ContextMenu);
    app_ui.ContextMenu = ContextMenu;
})(app_ui || (app_ui = {}));
var admin;
(function (admin) {
    (function (ui) {
        var DetailToolbar = (function () {
            function DetailToolbar(isLiveMode) {
                this.isLiveMode = false;
                this.isLiveMode = isLiveMode;
                var tbar = new Ext.toolbar.Toolbar({
                    itemId: 'contentDetailToolbar',
                    cls: 'admin-toolbar'
                });
                var defaults = {
                    scale: 'medium'
                };
                var btnNew = this.createButton({
                    text: 'New',
                    action: 'newContent'
                }, defaults);
                var btnEdit = this.createButton({
                    text: 'Edit',
                    action: 'editContent'
                }, defaults);
                var btnDelete = this.createButton({
                    text: 'Delete',
                    action: 'deleteContent'
                }, defaults);
                var btnDuplicate = this.createButton({
                    text: 'Duplicate',
                    action: 'duplicateContent'
                }, defaults);
                var btnMove = this.createButton({
                    text: 'Move',
                    action: 'moveContent'
                }, defaults);
                var btnExport = this.createButton({
                    text: 'Export'
                }, defaults);
                var separator = new Ext.toolbar.Fill();
                var cycle = this.createCycle();
                var toggleSlide = this.createToggleSlide();
                var btnClose = this.createButton({
                    text: 'Close',
                    action: 'closeContent'
                }, defaults);
                tbar.add(btnNew, btnEdit, btnDelete, btnDuplicate, btnMove, btnExport, separator, cycle, toggleSlide, btnClose);
                this.ext = tbar;
            }

            DetailToolbar.prototype.createButton = function (config, defaults) {
                return new Ext.button.Button(Ext.apply(config, defaults));
            };
            DetailToolbar.prototype.createCycle = function () {
                return new Ext.button.Cycle({
                    itemId: 'deviceCycle',
                    disabled: !this.isLiveMode,
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
                });
            };
            DetailToolbar.prototype.createToggleSlide = function () {
                return Ext.create({
                    xtype: 'toggleslide',
                    onText: 'Preview',
                    offText: 'Details',
                    action: 'toggleLive',
                    state: this.isLiveMode,
                    listeners: {
                        change: function (toggle, state) {
                            this.isLiveMode = state;
                        }
                    }
                });
            };
            return DetailToolbar;
        })();
        ui.DetailToolbar = DetailToolbar;
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
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
var admin;
(function (admin) {
    (function (ui) {
        var DropDownButton = (function () {
            function DropDownButton(config, menuItems) {
                var menu;
                if (!Ext.isEmpty(menuItems)) {
                    menu = new Ext.menu.Menu({
                        cls: 'admin-context-menu',
                        border: false,
                        shadow: false,
                        width: 120,
                        items: menuItems
                    });
                }
                this.ext = new Ext.button.Button(Ext.apply({
                    cls: 'admin-dropdown-button',
                    width: 120,
                    padding: 5,
                    menu: menu
                }, config));
            }

            return DropDownButton;
        })();
        ui.DropDownButton = DropDownButton;
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
var admin;
(function (admin) {
    (function (ui) {
        var IframeContainer = (function () {
            function IframeContainer(url, iFrameCls) {
                var container = this;
                this.url = url;
                this.iFrameCls = iFrameCls || '';
                var panel = new Ext.panel.Panel({
                    html: '<iframe style="border: 0 none; width: 100%; height: 420px;"></iframe>',
                    autoScroll: false,
                    styleHtmlContent: true,
                    minHeight: 420,
                    listeners: {
                        afterrender: function () {
                            if (container.url) {
                                container.load(container.url);
                            }
                        }
                    }
                });
                this.ext = panel;
            }

            IframeContainer.prototype.load = function (url) {
                var iframe = this.getIframe();
                if (!Ext.isEmpty(url) && Ext.isDefined(iframe)) {
                    iframe.dom.src = api_util.getAbsoluteUri(url);
                } else {
                    iframe.update("<h2 class='message'>Page can't be found.</h2>");
                }
            };
            IframeContainer.prototype.getIframe = function () {
                return this.ext.getEl().down('iframe');
            };
            return IframeContainer;
        })();
        ui.IframeContainer = IframeContainer;
    })(admin.ui || (admin.ui = {}));
    var ui = admin.ui;
})(admin || (admin = {}));
Ext.define('Admin.view.contentManager.DetailPanel', {
    extend: 'Admin.view.BaseDetailPanel',
    alias: 'widget.contentDetail',
    isLiveMode: false,
    keyField: 'path',
    initComponent: function () {
        var me = this;
        this.activeItem = this.resolveActiveItem(this.data);
        this.singleSelection.getTabs = function () {
            var iFrameCls = me.isVertical ? 'admin-detail-vertical' : '';
            var analytics = new admin.ui.IframeContainer('/dev/detailpanel/analytics.html ', iFrameCls);
            return [
                {
                    displayName: 'Analytics',
                    name: 'analytics',
                    items: [
                        analytics.ext
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
        };
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
                return data[0].data.displayName.match(/frogger/gi) !== null ? '/dev/live-edit-page/frogger.jsp'
                    : '/dev/live-edit-page/bootstrap.jsp';
            } else if (data.data) {
                return data.data.displayName.match(/frogger/gi) !== null ? '/dev/live-edit-page/frogger.jsp'
                    : '/dev/live-edit-page/bootstrap.jsp';
            }
        }
        return '/dev/live-edit-page/bootstrap.jsp';
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
Ext.define('Admin.view.FileUploadWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.fileUploadWindow',
    require: [
        'Admin.lib.UriHelper'
    ],
    dialogTitle: undefined,
    dialogSubTitle: undefined,
    dialogInfoTpl: undefined,
    width: 800,
    height: 560,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaultType: 'container',
    initComponent: function () {
        var me = this;
        this.items = [
            me.header('Image uploader',
                'Images uploaded will be embedded directly in this content, you may move them to the library later if desired'),
            me.plupload(),
            me.buttonRow({
                xtype: 'button',
                text: 'Cancel',
                ui: 'grey',
                handler: function (btn, evt) {
                    if (me.uploader) {
                        me.uploader.stop();
                    }
                    me.close();
                }
            })
        ];
        this.callParent(arguments);
        this.addEvents('uploadcomplete');
    },
    plupload: function () {
        return {
            xtype: 'container',
            flex: 1,
            layout: 'card',
            listeners: {
                afterrender: this.onPluploadAfterrender,
                scope: this
            },
            items: [
                {
                    itemId: 'uploadForm',
                    cls: 'admin-upload-form',
                    xtype: 'container',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            emptyText: 'Paste URL to image here',
                            margin: '0 0 20 0',
                            enableKeyEvents: true
                        },
                        {
                            flex: 1,
                            xtype: 'component',
                            itemId: 'dropZone',
                            cls: 'admin-drop-zone',
                            styleHtmlContent: true
                        }
                    ]
                },
                {
                    itemId: 'progressForm',
                    cls: 'admin-upload-progress',
                    xtype: 'component',
                    tpl: '<h4>{percent}% complete</h4>' +
                         '<div class="admin-progress-bar"><div class="admin-progress" style="width: {percent}%;"></div></div>' +
                         '<p>{[values.uploaded + values.failed + 1]} of {[values.uploaded + values.queued + values.failed]}</p>',
                    data: {
                        percent: 40,
                        uploaded: 2,
                        queued: 2,
                        failed: 1
                    }
                }
            ]
        };
    },
    onPluploadAfterrender: function (container) {
        var me = this;
        var dropZoneEl = container.down('#dropZone').el;
        this.uploader = new plupload.Uploader({
            runtimes: 'gears,html5,flash',
            browse_button: dropZoneEl.dom.id,
            url: Admin.lib.UriHelper.getAbsoluteUri('admin/rest/upload'),
            autoStart: false,
            max_file_size: '100mb',
            drop_element: dropZoneEl.dom.id,
            statusQueuedText: 'Ready to upload',
            statusUploadingText: 'Uploading ({0}%)',
            statusFailedText: '<span style="color: red">Error</span>',
            statusDoneText: '<span style="color: green">Complete</span>',
            statusInvalidSizeText: 'File too large',
            statusInvalidExtensionText: 'Invalid file type'
        });
        this.uploader.bind('Init', function (up) {
            var hint = '';
            if (!!up.features.dragdrop) {
                hint = '<h4>Drop files here or click to select</h4>';
            } else {
                hint = '<h4>Click to select</h4>';
            }
            dropZoneEl.update(hint);
        });
        this.uploader.bind('QueueChanged', function (up) {
            var activeItem;
            if (up.files.length > 0) {
                activeItem = 'progressForm';
                up.start();
            } else {
                activeItem = 'uploadForm';
                up.stop();
            }
            container.getLayout().setActiveItem(activeItem);
        });
        this.uploader.bind('UploadProgress', function (up, file) {
            container.down('#progressForm').update(up.total);
        });
        this.uploader.bind('FileUploaded', function (up, file, response) {
            if (response && response.response) {
                var json = Ext.JSON.decode(response.response);
                if (json.success && json.items && json.items.length == 1) {
                    file.response = json.items[0];
                }
            }
        });
        this.uploader.bind('UploadComplete', function (up, files) {
            up.total.reset();
            var uploaded = up.splice();
            container.getLayout().setActiveItem('uploadForm');
            me.fireEvent('uploadcomplete', me, uploaded);
        });
        this.uploader.init();
    }
});
Ext.define('Admin.view.AutosizeTextField', {
    extend: 'Ext.Component',
    alias: 'widget.autosizeTextField',
    margin: '0 20px 0 0',
    style: {
        overflow: 'hidden'
    },
    isEmpty: true,
    emptyText: '',
    fieldHeight: undefined,
    isMouseOver: false,
    isFocused: false,
    tpl: '<div class="autosizeTextField" style="' + 'float: left; ' + 'border: 1px solid #EEEEEE; ' + 'min-width: 200px; ' +
         'padding: 0px 10px; ' + 'margin: 3px; ' + 'white-space: nowrap; ' + 'overflow: hidden; ' + '" contenteditable="true">{value}' +
         '</div>',
    data: {
        value: ''
    },
    ons: undefined,
    initComponent: function () {
        this.data.value = this.value;
        this.isEmpty = !this.data.value;
        if (this.isEmpty) {
            this.data.value = this.emptyText;
        }
        this.callParent(arguments);
    },
    afterRender: function () {
        var me = this;
        me.callParent();
        var textEl = this.el.down('.autosizeTextField');
        textEl.on(this.ons);
        textEl.on({
            focus: function () {
                me.isFocused = true;
                if (me.isEmpty) {
                    me.setRawValue('');
                }
                me.updateComponent();
            },
            blur: function () {
                me.isFocused = false;
                me.isEmpty = !me.getRawValue();
                if (me.isEmpty) {
                    me.setRawValue(me.emptyText);
                }
                me.updateComponent();
            },
            mouseover: function () {
                me.isMouseOver = true;
                me.updateComponent();
            },
            mouseout: function () {
                me.isMouseOver = false;
                me.updateComponent();
            }
        });
        this.textEl = textEl;
        if (me.isEmpty) {
            me.setRawValue(me.emptyText);
        }
        this.updateComponent();
    },
    updateComponent: function () {
        this.textEl.applyStyles({
            boxShadow: this.isFocused ? '0 0 3px #98C9F2' : 'none',
            border: '1px solid ' + (this.isMouseOver || this.isFocused ? '#98C9F2' : '#EEEEEE'),
            color: (this.isEmpty && !this.isFocused) ? '#555555' : 'black',
            fontSize: (this.fieldHeight - 14) + 'px',
            minHeight: (this.fieldHeight) + 'px'
        });
    },
    on: function (ons) {
        var me = this;
        var keyup = ons.keyup;
        ons.keyup = function (field, event, opts) {
            field.getValue = function () {
                return me.getValue();
            };
            keyup.call(ons.scope, field, event, opts);
        };
        ons.input = function (field, newVal, oldVal) {
            newVal = me.getRawValue();
            oldVal = me.getRawValue();
            ons.change.call(ons.scope, field, newVal, oldVal);
        };
        ons.keypress = function (event) {
            var value = String.fromCharCode(event.charCode);
            var newValue = 'ok';
            if (value != '' && event.charCode != 0) {
                newValue = value.replace(me.stripCharsRe, '');
            }
            if (event.keyCode == event.RETURN || newValue == '') {
                event.preventDefault();
            }
            this.isEmpty = false;
        };
        this.ons = ons;
    },
    getValue: function () {
        return this.isEmpty ? '' : this.getRawValue();
    },
    getRawValue: function () {
        return this.textEl.dom.textContent;
    },
    setValue: function (value) {
        this.isEmpty = !value;
        this.setRawValue(value);
    },
    setRawValue: function (value) {
        this.textEl.dom.textContent = value;
    },
    processRawValue: function (value) {
        var me = this, stripRe = me.stripCharsRe, newValue;
        if (stripRe) {
            newValue = value.replace(stripRe, '');
            if (newValue !== value) {
                me.setRawValue(newValue);
                value = newValue;
            }
        }
        return value;
    },
    getFocusEl: function () {
        return this.textEl;
    }
});
Ext.define('Admin.view.WizardHeader', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizardHeader',
    requires: [
        'Admin.view.AutosizeTextField'
    ],
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
        this.displayNameField = Ext.create('Admin.view.AutosizeTextField', Ext.apply({
            xtype: 'textfield',
            fieldHeight: 40,
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
        this.nameField = Ext.create('Admin.view.AutosizeTextField', Ext.apply({
            xtype: 'textfield',
            fieldHeight: 30,
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
        if (!me.pathField.hidden && !me.nameField.hidden) {
            this.items.push({
                xtype: 'fieldcontainer',
                hideLabel: true,
                layout: 'hbox',
                items: [
                    me.pathField,
                    me.nameField
                ]
            });
        } else if (!me.pathField.hidden) {
            this.items.push(me.pathField);
        } else if (!me.nameField.hidden) {
            this.items.push(me.nameField);
        }
        this.callParent(arguments);
        this.addEvents('displaynamechange', 'displaynameoverride', 'namechange', 'nameoverride');
    },
    onDisplayNameAfterrender: function (field) {
        if (!field.readOnly && field.autoFocus) {
            field.focus(false, 100);
            field.selectText(0, 0);
        }
    },
    onDisplayNameKey: function (field, event, opts) {
        var wasAutoGenerate = this.autogenerateDisplayName;
        var autoGenerate = Ext.isEmpty(field.getValue());
        if (wasAutoGenerate != autoGenerate) {
            this.fireEvent('displaynameoverride', !autoGenerate);
        }
        this.autogenerateDisplayName = autoGenerate;
    },
    onDisplayNameChanged: function (field, newVal, oldVal, opts) {
        if (this.fireEvent('displaynamechange', newVal, oldVal) !== false && this.autogenerateName) {
            var processedValue = this.nameField.processRawValue(this.preProcessName(newVal));
            this.nameField.setValue(processedValue);
        }
    },
    onNameKey: function (field, event, opts) {
        var wasAutoGenerate = this.autogenerateName;
        var autoGenerate = Ext.isEmpty(field.getValue());
        if (wasAutoGenerate != autoGenerate) {
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
        return !Ext.isEmpty(displayName) ? displayName.replace('/[\s+\./]/ig', '-').replace(/-{2,}/g, '-').replace(/^-|-$/g,
            '').toLowerCase() : '';
    },
    prepareHeaderData: function (data) {
        return data && data.data || data || {
        };
    },
    setData: function (data) {
        this.data = data;
        this.setDisplayName(data[this.displayNameProperty]);
        this.setName(data[this.nameProperty]);
        this.getForm().setValues(this.resolveHeaderData(data));
    },
    getData: function () {
        var data = this.getForm().getFieldValues();
        data[this.displayNameProperty] = this.getDisplayName();
        data[this.nameProperty] = this.nameField.getValue();
        return data;
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
        if (oldCard !== newCard) {
            owner.fireEvent("animationstarted", newCard, oldCard);
            if (newCard.rendered && this.animation && this.animation !== "none") {
                this.syncFx();
                var target = this.getRenderTarget();
                newCard.setWidth(target.getWidth() - target.getPadding("lr") - Ext.getScrollbarSize().width);
                switch (this.animation) {
                case 'fade':
                    newCard.el.setStyle({
                        position: 'absolute',
                        opacity: 0,
                        top: this.getRenderTarget().getPadding('t') + 'px'
                    });
                    newCard.show();
                    if (oldCard) {
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
                    if (oldCard) {
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
        var wizardPanel = [
            {
                xtype: 'container',
                region: 'west',
                padding: 10,
                width: 130,
                style: {
                    cursor: 'pointer'
                },
                layout: 'absolute',
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
                        iconCls: 'wizard-nav-icon icon-chevron-left icon-6x gray-color',
                        style: {
                            backgroundColor: 'transparent',
                            border: 'none'
                        },
                        height: 80,
                        width: 64,
                        padding: 0,
                        margin: '0 0 0 40'
                    }
                ]
            },
            {
                xtype: 'container',
                itemId: 'nextPanel',
                region: 'east',
                padding: 10,
                width: 100,
                style: {
                    cursor: 'pointer'
                },
                layout: 'absolute',
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
                        style: {
                            backgroundColor: 'transparent',
                            border: 'none'
                        },
                        height: 80,
                        formBind: true,
                        iconCls: 'wizard-nav-icon icon-chevron-right icon-6x gray-color',
                        width: 64,
                        padding: 0
                    }
                ]
            },
            this.wizard
        ];
        var bottomPanel = [
            {
                itemId: 'bottomPanel',
                region: 'center',
                xtype: 'container',
                flex: 1,
                autoScroll: true,
                padding: '20 0 0 0',
                layout: 'border',
                items: wizardPanel,
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
        this.items = [
            this.createHeaderPanel(),
            {
                xtype: 'container',
                padding: '20 0 0 0',
                layout: 'border',
                flex: 1,
                items: bottomPanel
            }
        ];
        this.callParent(arguments);
        this.addEvents(events);
        this.wizard.addEvents(events);
        this.wizard.enableBubble(events);
        this.on({
            animationstarted: this.onAnimationStarted,
            animationfinished: this.onAnimationFinished,
            resize: function () {
                me.updateShadow(me);
            }
        });
        if (this.getActionButton()) {
            this.boundItems.push(this.getActionButton());
        }
        this.down('#progressBar').update(this.wizard.items.items);
        this.on('afterrender', this.bindItemListeners);
        this.on('afterlayout', function () {
            me.updateShadow(me);
        });
    },
    updateShadow: function (me) {
        var bottomPanel = me.down('#bottomPanel');
        if (bottomPanel && bottomPanel.getEl()) {
            bottomPanel = bottomPanel.getEl();
            var hasScroll = bottomPanel.dom.scrollHeight >
                            bottomPanel.dom.clientHeight, positionPanelEl = me.down('#positionPanel').getEl(), wizardHeaderPanelHeight = me.down('#wizardHeaderPanel').getEl().getHeight(), headerShadowEl = Ext.fly('admin-wizard-header-shadow');
            if (hasScroll && bottomPanel.dom.scrollTop !== 0) {
                if (!headerShadowEl) {
                    var dh = Ext.DomHelper, boxShadowOffsets = Ext.isGecko ? '0 5px 6px -3px' : '0 5px 10px -3px';
                    var shadowDomSpec = {
                        id: 'admin-wizard-header-shadow',
                        tag: 'div',
                        style: 'position:absolute; top:' + wizardHeaderPanelHeight +
                               'px; left:0px; z-index:1000; height:10px; background:transparent; width:100%; box-shadow:' +
                               boxShadowOffsets + '#888 inset'
                    };
                    dh.append(positionPanelEl, shadowDomSpec);
                    Ext.fly('admin-wizard-header-shadow').show(true);
                }
            } else {
                if (headerShadowEl) {
                    headerShadowEl.remove();
                }
            }
            var prev = this.down('#prev'), next = this.down('#next'), nextPanel = this.down('#nextPanel');
            var width = bottomPanel.getSize().width;
            var isWide = width > 800;
            nextPanel.setVisible(isWide);
            bottomPanel.setStyle({
                overflowX: (isWide ? 'hidden' : 'auto'),
                overflowY: 'auto'
            });
            var height = nextPanel.getSize().height;
            var top = bottomPanel.dom.scrollTop + height / 2 - 50;
            prev.setPosition(0, top);
            next.setPosition(0, top);
        }
    },
    updateNavButton: function (element, color) {
        var btn = Ext.get(element);
        if (!btn.hasCls('wizard-nav-icon')) {
            btn = btn.down('.wizard-nav-icon');
        } else if (btn.hasCls('x-btn-inner')) {
            btn = btn.next('.x-btn-icon');
        }
        if (btn) {
            btn.setStyle('color', color);
        }
    },
    updateProgress: function (newStep) {
        var progressBar = this.down('#progressBar');
        progressBar.update(this.wizard.items.items);
        var conditionsMet = this.isWizardValid && (this.isWizardDirty || this.isNew);
        progressBar.setDisabled(this.isNew ? !this.isStepValid(newStep) : !conditionsMet);
    },
    bindItemListeners: function (cmp) {
        Ext.each(cmp.validateItems, function (validateItem, i) {
            if (validateItem) {
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
            if (i === 0) {
                cmp.onAnimationFinished(item, null);
            }
            if ('editUserFormPanel' === item.getXType()) {
                item.on('fieldsloaded', checkValidityFn);
            }
            var itemForm = Ext.isFunction(item.getForm) ? item.getForm() : undefined;
            if (itemForm) {
                if (Ext.isFunction(cmp.washDirtyForm)) {
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
        if (boundItems && this.owner === wizardPanel.getActiveItem()) {
            var valid = wizardPanel.isStepValid(this.owner);
            boundItems.each(function (cmp) {
                if (cmp.rendered && cmp.isHidden() === valid) {
                    if (valid) {
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
        if (!boundItems && form.owner.rendered) {
            boundItems = form._boundItems = Ext.create('Ext.util.MixedCollection');
            boundItems.addAll(form.owner.query('[formBind]'));
            boundItems.addAll(this.boundItems);
        }
        return boundItems;
    },
    handleValidityChange: function (form, valid, opts) {
        if (!valid) {
            Ext.Array.include(this.invalidItems, form);
        } else {
            Ext.Array.remove(this.invalidItems, form);
        }
        this.updateProgress();
        var isWizardValid = this.invalidItems.length === 0;
        if (this.isWizardValid !== isWizardValid) {
            this.isWizardValid = isWizardValid;
            var actionButton = this.getActionButton();
            if (actionButton) {
                actionButton.setVisible(isWizardValid);
            }
            this.fireEvent('validitychange', this, isWizardValid);
        }
    },
    handleDirtyChange: function (form, dirty, opts) {
        if (dirty) {
            Ext.Array.include(this.dirtyItems, form);
        } else {
            Ext.Array.remove(this.dirtyItems, form);
        }
        this.updateProgress();
        var isWizardDirty = this.dirtyItems.length > 0;
        if (this.isWizardDirty !== isWizardDirty) {
            this.isWizardDirty = isWizardDirty;
            this.fireEvent('dirtychange', this, isWizardDirty);
        }
    },
    isStepValid: function (step) {
        var isStepValid = Ext.Array.intersect(this.invalidItems, this.validateItems).length === 0;
        var activeStep = step || this.getActiveItem();
        var activeForm;
        if (activeStep && Ext.isFunction(activeStep.getForm)) {
            activeForm = activeStep.getForm();
        }
        if (isStepValid && activeForm) {
            isStepValid = isStepValid && !activeForm.hasInvalidField();
        }
        return isStepValid;
    },
    getProgressBar: function () {
        return this.down('#progressBar');
    },
    createRibbon: function () {
        var Templates_common_wizardPanelSteps = '<div class="navigation-container">' + '<ul class="navigation clearfix">' +
                                                '<tpl for=".">' +
                                                '<li class="{[ this.resolveClsName( xindex, xcount ) ]}" wizardStep="{[xindex]}">' +
                                                '<a href="javascript:;" class="step {[ this.resolveClsName( xindex, xcount ) ]}">{[' +
                                                '(values.stepTitle || values.title) ]}</a></li>' + '</tpl>' + '</ul>' + '</div>';
        var me = this;
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
            tpl: new Ext.XTemplate(Templates_common_wizardPanelSteps, {
                resolveClsName: function (index, total) {
                    var activeIndex = me.wizard.items.indexOf(me.getActiveItem()) + 1;
                    var clsName = '';
                    if (index === 1) {
                        clsName += 'first ';
                    }
                    if (index < activeIndex) {
                        clsName += 'previous ';
                    }
                    if (index + 1 === activeIndex) {
                        clsName += 'immediate ';
                    }
                    if (index === activeIndex) {
                        clsName += 'current ';
                    }
                    if (index > activeIndex) {
                        clsName += 'next ';
                    }
                    if (index - 1 === activeIndex) {
                        clsName += 'immediate ';
                    }
                    if (index === total) {
                        clsName += 'last ';
                    }
                    return clsName;
                }
            })
        };
    },
    onAnimationStarted: function (newStep, oldStep) {
        if (this.showControls) {
            this.updateButtons(this.wizard, true);
        }
        if (this.externalControls) {
            this.updateButtons(this.externalControls, true);
        }
    },
    onAnimationFinished: function (newStep, oldStep) {
        if (newStep) {
            this.updateProgress(newStep);
            this.focusFirstField(newStep);
            this.fireEvent("stepchanged", this, oldStep, newStep);
            if (this.showControls) {
                this.updateButtons(this.wizard);
            }
            if (this.externalControls) {
                this.updateButtons(this.externalControls);
            }
            if (Ext.isFunction(newStep.getForm)) {
                var newForm = newStep.getForm();
                if (newForm) {
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
        if (activeItem && (firstField = activeItem.down('field[disabled=false]'))) {
            firstField.focus(false);
            if (firstField.rendered && firstField.selectText) {
                firstField.selectText(0, 0);
            }
        }
    },
    updateButtons: function (toolbar, disable) {
        if (toolbar) {
            var prev = this.down('#prev'), next = this.down('#next');
            var hasNext = this.getNext(), hasPrev = this.getPrev();
            if (prev) {
                if (disable || !hasPrev) {
                    prev.hide();
                } else {
                    prev.show();
                }
            }
            if (next) {
                if (disable || !hasNext) {
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
        if ((!isDisabled && isNew && li && li.hasCls('next') && li.hasCls('immediate')) || (!isDisabled && !isNew) ||
            (isDisabled && !isNew && li && !li.hasCls('last')) || (li && li.hasCls('previous'))) {
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
        if (btn) {
            this.externalControls = btn.up('toolbar');
        }
        if (this.fireEvent("beforestepchanged", this, oldStep) !== false) {
            var newStep;
            switch (direction) {
            case "-1":
            case "prev":
                if (this.getPrev()) {
                    newStep = this.wizard.getLayout().prev();
                }
                break;
            case "+1":
            case "next":
                if (this.getNext()) {
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
        if (Ext.isEmpty(this.data)) {
            this.data = {
            };
        }
        Ext.merge(this.data, newValues);
    },
    deleteData: function (key) {
        if (key) {
            delete this.data[key];
        }
    },
    getData: function () {
        var me = this;
        me.wizard.items.each(function (item) {
            if (item.getData) {
                me.addData(item.getData());
            } else if (item.getForm) {
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
var app_browse;
(function (app_browse) {
    var ContentTreeGridPanel = (function (_super) {
        __extends(ContentTreeGridPanel, _super);
        function ContentTreeGridPanel(itemId) {
            _super.call(this, this.createColumns(), this.createGridStore(), this.createTreeStore(), this.createGridConfig(),
                this.createTreeConfig());
            this.setActiveList(api_ui_grid.TreeGridPanel.TREE);
            this.setKeyField("path");
            this.setItemId(itemId);
        }

        ContentTreeGridPanel.prototype.createGridStore = function () {
            return new Ext.data.Store({
                model: 'Admin.model.contentManager.ContentModel',
                proxy: {
                    type: 'direct',
                    directFn: api_remote.RemoteService.content_find,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'contents',
                        totalProperty: 'total'
                    }
                }
            });
        };
        ContentTreeGridPanel.prototype.createTreeStore = function () {
            return new Ext.data.TreeStore({
                model: 'Admin.model.contentManager.ContentModel',
                folderSort: true,
                autoLoad: false,
                proxy: {
                    type: 'direct',
                    directFn: api_remote.RemoteService.content_tree,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'contents',
                        totalProperty: 'total'
                    }
                }
            });
        };
        ContentTreeGridPanel.prototype.createColumns = function () {
            return [
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
                    text: 'Modified',
                    dataIndex: 'modifiedTime',
                    renderer: this.prettyDateRenderer,
                    scope: this,
                    sortable: true
                }
            ];
        };
        ContentTreeGridPanel.prototype.createGridConfig = function () {
            return {
                listeners: {
                    selectionchange: function (selModel, selected, opts) {
                        new app_event.GridSelectionChangeEvent(selected).fire();
                    },
                    itemcontextmenu: function (view, rec, node, index, event) {
                        event.stopEvent();
                        new app_event.ShowContextMenuEvent(event.xy[0], event.xy[1]).fire();
                    },
                    itemdblclick: function (grid, record) {
                        new app_event.EditContentEvent(grid.getSelection()).fire();
                    }
                }
            };
        };
        ContentTreeGridPanel.prototype.createTreeConfig = function () {
            return {
                selectionchange: function (selModel, selected, opts) {
                    new app_event.GridSelectionChangeEvent(selected).fire();
                }
            };
        };
        ContentTreeGridPanel.prototype.nameRenderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
            var nameTemplate = '<div class="admin-{0}-thumbnail">' + '<img src="{1}"/>' + '</div>' + '<div class="admin-{0}-description">' +
                               '<h6>{2}</h6>' + '<p>{3}</p>' + '</div>';
            var content = record.data;
            var activeListType = this.getActiveList().getItemId();
            return Ext.String.format(nameTemplate, activeListType, content.iconUrl, value, content.name);
        };
        ContentTreeGridPanel.prototype.statusRenderer = function () {
            return "Online";
        };
        ContentTreeGridPanel.prototype.prettyDateRenderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
            try {
                if (parent && Ext.isFunction(parent['humane_date'])) {
                    return parent['humane_date'](value);
                } else {
                    return value;
                }
            }
            catch (e) {
                return value;
            }
        };
        return ContentTreeGridPanel;
    })(api_ui_grid.TreeGridPanel);
    app_browse.ContentTreeGridPanel = ContentTreeGridPanel;
})(app_browse || (app_browse = {}));
var app_appbar;
(function (app_appbar) {
    var ShowAppLauncherAction = (function (_super) {
        __extends(ShowAppLauncherAction, _super);
        function ShowAppLauncherAction() {
            _super.call(this, 'Start');
            this.addExecutionListener(function () {
                new api_appbar.ShowAppLauncherEvent().fire();
            });
        }

        return ShowAppLauncherAction;
    })(api_ui.Action);
    app_appbar.ShowAppLauncherAction = ShowAppLauncherAction;
    var ShowAppBrowsePanelAction = (function (_super) {
        __extends(ShowAppBrowsePanelAction, _super);
        function ShowAppBrowsePanelAction() {
            _super.call(this, 'Browse');
            this.addExecutionListener(function () {
                new api_appbar.ShowAppBrowsePanelEvent().fire();
            });
        }

        return ShowAppBrowsePanelAction;
    })(api_ui.Action);
    app_appbar.ShowAppBrowsePanelAction = ShowAppBrowsePanelAction;
    var ContentAppBarActions = (function () {
        function ContentAppBarActions() {
        }

        ContentAppBarActions.SHOW_APP_LAUNCHER = new app_appbar.ShowAppLauncherAction();
        ContentAppBarActions.SHOW_APP_BROWSER_PANEL = new ShowAppBrowsePanelAction();
        return ContentAppBarActions;
    })();
    app_appbar.ContentAppBarActions = ContentAppBarActions;
})(app_appbar || (app_appbar = {}));
var app_appbar;
(function (app_appbar) {
    var ContentAppBar = (function (_super) {
        __extends(ContentAppBar, _super);
        function ContentAppBar() {
            _super.call(this, "Content Manager", {
                showAppLauncherAction: app_appbar.ContentAppBarActions.SHOW_APP_LAUNCHER,
                showAppBrowsePanelAction: app_appbar.ContentAppBarActions.SHOW_APP_BROWSER_PANEL
            });
        }

        return ContentAppBar;
    })(api_appbar.AppBar);
    app_appbar.ContentAppBar = ContentAppBar;
})(app_appbar || (app_appbar = {}));
Ext.define('Admin.view.contentManager.wizard.form.FormItemOccurrencesHandler', {
    copyNo: 1,
    handleOccurrences: function (minimum) {
        this.addEvents('copyadded', 'copyremoved');
        this.enableBubble('copyadded', 'copyremoved');
        if (Ext.isEmpty(this.value) && this.copyNo < minimum) {
            this.addCopy();
        } else {
            var value = this.value;
            if (value instanceof Array && value.length > 0) {
                this.setValue(value[0].value);
                if (value.length > 1) {
                    this.addCopy(value.slice(1));
                }
            }
        }
    },
    addCopy: function (value) {
        var parent = this.up();
        var clone = this.cloneConfig({
            copyNo: this.copyNo + 1,
            fieldLabel: '',
            value: value || ''
        });
        this.nextField = clone;
        clone.prevField = this;
        var me = this;
        var index = parent.items.findIndexBy(function (item) {
            if (item.getItemId() === me.getItemId()) {
                return true;
            }
            return false;
        });
        parent.insert(index + 1, clone);
        clone.fireEvent('copyadded', clone);
        return clone;
    },
    removeCopy: function () {
        var parent = this.up();
        var linkedField = this.prevField || this.nextField;
        if (this.prevField) {
            this.prevField.nextField = this.nextField;
        }
        if (this.nextField) {
            this.nextField.prevField = this.prevField;
        }
        if (linkedField) {
            linkedField.updateCopyNo();
        }
        this.fireEvent('copyremoved', this);
        parent.remove(this);
        return linkedField;
    },
    updateCopyNo: function () {
        if (this.prevField) {
            this.copyNo = this.prevField.copyNo + 1;
        } else {
            this.copyNo = 1;
        }
        if (this.nextField) {
            this.nextField.updateCopyNo();
        }
    }
});
Ext.define('Admin.view.contentManager.wizard.form.ImagePopupDialog', {
    extend: 'Ext.Container',
    alias: 'widget.imagePopupDialog',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    height: 150,
    cls: 'admin-inputimage-dlg',
    tpl: "<div style='text-align: center'><h1>{displayName}</h1><p>{path}</p></div>",
    defaultType: 'container',
    padding: '20 0 0 0',
    removeHandler: Ext.emptyFn(),
    editHandler: Ext.emptyFn(),
    initComponent: function () {
        this.items = [
            {
                tpl: this.tpl,
                itemId: 'messageBox',
                data: this.buildTemplateData(this.data)
            },
            {
                layout: {
                    type: 'hbox',
                    pack: 'center'
                },
                items: [
                    {
                        xtype: 'button',
                        text: 'Edit',
                        cls: 'icon-button',
                        scale: 'medium',
                        width: 150,
                        margin: '5 5',
                        listeners: {
                            click: this.editHandler
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Remove',
                        cls: 'icon-button',
                        scale: 'medium',
                        width: 150,
                        margin: '5 5',
                        style: {
                            borderColor: '#7A7A7A',
                            backgroundColor: '#7A7A7A'
                        },
                        listeners: {
                            click: this.removeHandler
                        }
                    }
                ]
            }
        ];
        this.callParent(arguments);
    },
    buildTemplateData: function (data) {
        return {
            displayName: Ext.String.ellipsis(data.displayName, 25),
            path: Ext.String.ellipsis(data.path, 50)
        };
    },
    updateTpl: function (data) {
        this.down('#messageBox').update(this.buildTemplateData(data));
    }
});
Ext.define('Admin.view.contentManager.wizard.form.FormGenerator', {
    addComponentsBasedOnContentType: function (formItemConfigs, parentComponent, dataSet) {
        var me = this;
        var component;
        Ext.each(formItemConfigs, function (item) {
            var formItemConfig = me.getFormItemConfig(item);
            var data = me.getDataForConfig(formItemConfig, dataSet);
            var creationFunction = me.constructCreationFunction(item);
            component = creationFunction.call(me, formItemConfig, data);
            me.addComponent(component, parentComponent);
        });
    },
    addComponent: function (component, parentComponent) {
        if (this.componentIsContainer(parentComponent)) {
            parentComponent.add(component);
        } else {
            parentComponent.items.push(component);
        }
    },
    createLayoutComponent: function (fieldSetLayoutConfig, fieldSetLayoutData) {
        return Ext.create({
            xclass: 'widget.FieldSetLayout',
            name: fieldSetLayoutConfig.name,
            fieldSetLayoutConfig: fieldSetLayoutConfig,
            content: fieldSetLayoutData
        });
    },
    createFormItemSetComponent: function (formItemSetConfig, formItemSetData) {
        var formItemSetComponent = Ext.create({
            xclass: 'widget.FormItemSet',
            name: formItemSetConfig.name,
            formItemSetConfig: formItemSetConfig,
            value: formItemSetData
        });
        return Ext.create({
            xclass: 'widget.formItemSetContainer',
            field: formItemSetComponent
        });
    },
    createInputComponent: function (inputConfig, inputData) {
        var classAlias = 'widget.' + inputConfig.type.name;
        if (!this.formItemIsSupported(classAlias)) {
            console.error('Unsupported input type', inputConfig);
            return;
        }
        var inputComponent = Ext.create({
            xclass: classAlias,
            name: inputConfig.name,
            copyNo: inputConfig.copyNo || 1,
            inputConfig: inputConfig,
            value: inputData
        });
        if (!inputComponent.defaultOccurrencesHandling) {
            inputComponent.setFieldLabel(this.generateLabelHTML(inputConfig));
            return inputComponent;
        } else {
            var fieldLabel = this.createInputLabel(inputConfig);
            return Ext.create({
                xclass: 'widget.inputContainer',
                label: fieldLabel,
                field: inputComponent
            });
        }
    },
    createInputLabel: function (inputConfig) {
        var label = this.generateLabelHTML(inputConfig);
        label += ':';
        return Ext.create('Ext.form.Label', {
            width: 110,
            styleHtmlContent: true,
            html: label
        });
    },
    generateLabelHTML: function (inputConfig) {
        var label = inputConfig.label;
        if (inputConfig.occurrences.minimum > 0) {
            var requiredTitle = "Minimum " + inputConfig.occurrences.minimum + ' ' +
                                (inputConfig.occurrences.minimum == 1 ? 'occurrence is' : 'occurrences are') + ' required';
            label += ' <sup style="color: #E32400" title="' + requiredTitle + '">*</sup>';
        }
        return label;
    },
    getDataForConfig: function (formItemConfig, dataSet) {
        var key, data = [];
        if (formItemConfig.type === 'FieldSet') {
            return dataSet;
        }
        for (key in dataSet) {
            if (dataSet.hasOwnProperty(key)) {
                if (formItemConfig.name === dataSet[key].name) {
                    data.push(dataSet[key]);
                }
            }
        }
        return data;
    },
    constructCreationFunction: function (formItemConfig) {
        var key;
        for (key in formItemConfig) {
            if (formItemConfig.hasOwnProperty(key)) {
                return this["create" + key + "Component"];
            }
        }
        console.error("No handler for ", formItemConfig);
        return null;
    },
    getFormItemConfig: function (formItemConfig) {
        var key;
        for (key in formItemConfig) {
            if (formItemConfig.hasOwnProperty(key)) {
                return formItemConfig[key];
            }
        }
        return null;
    },
    formItemIsSupported: function (classAlias) {
        return Ext.ClassManager.getByAlias(classAlias);
    },
    componentIsContainer: function (component) {
        return component.getXType() === 'FormItemSet' || component.getXType() === 'FieldSetLayout' ||
               component.getXType() === 'fieldcontainer' || component.getXType() === 'container';
    }
});
Ext.define('Admin.view.contentManager.wizard.form.FieldSetLayout', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.FieldSetLayout',
    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator'
    },
    fieldSetLayoutConfig: undefined,
    content: null,
    padding: '0 0 0 0',
    initComponent: function () {
        this.title = this.fieldSetLayoutConfig.label;
        this.callParent(arguments);
        this.initLayout();
    },
    initLayout: function () {
        this.addComponentsBasedOnContentType(this.fieldSetLayoutConfig.items, this, this.content);
    },
    getValue: function () {
        var value = [];
        this.items.each(function (item) {
            var currentItemValue = item.getValue();
            value = value.concat(currentItemValue);
        });
        return value;
    }
});
Ext.define('Admin.view.contentManager.wizard.form.FormItemContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.formItemContainer',
    layout: 'column',
    label: undefined,
    field: undefined,
    padding: '0 0 10 0',
    listeners: {
        afterrender: function () {
            this.updateControlsState();
        }
    },
    initComponent: function () {
        this.maxFields = this.field.getConfig().occurrences.maximum;
        this.minFields = this.field.getConfig().occurrences.minimum;
        this.items = [
            this.label,
            {
                xtype: 'panel',
                itemId: 'formItemsPanel',
                cls: 'admin-droppable',
                layout: 'anchor',
                minWidth: 100,
                bodyStyle: {
                    backgroundColor: 'inherit'
                },
                items: [
                    this.field.cloneConfig()
                ],
                dockedItems: [
                    {
                        xtype: 'container',
                        padding: '0 5px 0 0',
                        dock: 'bottom',
                        items: this.createControls()
                    }
                ]
            }
        ];
        this.callParent(arguments);
        var formItemsPanel = this.down('#formItemsPanel');
        formItemsPanel.items.on('add', this.updateControlsState, this);
        formItemsPanel.items.on('remove', this.updateControlsState, this);
    },
    updateControlsState: function () {
    },
    createControls: function () {
    },
    getValue: function () {
        var value = [];
        var formItemsPanel = this.down('#formItemsPanel');
        if (formItemsPanel) {
            formItemsPanel.items.each(function (formItem) {
                var formItemValue = formItem.getValue();
                if (formItemValue instanceof Array) {
                    value = value.concat(formItemValue);
                } else {
                    value.push(formItemValue);
                }
            });
        }
        return value;
    }
});
Ext.define('Admin.view.contentManager.wizard.form.FormItemSet', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.FormItemSet',
    requires: [
        'Admin.lib.Sortable',
        'Admin.lib.UriHelper'
    ],
    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator',
        formItemOccurrencesHandler: 'Admin.view.contentManager.wizard.form.FormItemOccurrencesHandler'
    },
    formItemSetConfig: undefined,
    content: null,
    isCollapsed: false,
    fieldLabel: '',
    margin: '0 0 10 0',
    cls: 'admin-sortable admin-formitemset-block',
    padding: '0 10 0 10',
    initComponent: function () {
        var min = this.formItemSetConfig.occurrences.minimum;
        var dataSet = !Ext.isEmpty(this.value) ? this.value[0].value : undefined;
        this.dockedItems = [
            this.createFormItemSetHeader(this.copyNo > min)
        ];
        this.items = [];
        this.callParent(arguments);
        this.addComponentsBasedOnContentType(this.formItemSetConfig.items, this, dataSet);
    },
    listeners: {
        beforerender: function () {
            this.handleOccurrences(this.formItemSetConfig.occurrences.minimum);
        },
        render: function () {
            this.initSortable();
        }
    },
    createFormItemSetHeader: function (closable) {
        var me = this;
        var requiredTitle = '';
        var requiredMark = '';
        if (this.formItemSetConfig.occurrences.minimum > 0) {
            requiredTitle = "Minimum " + this.formItemSetConfig.occurrences.minimum + ' ' +
                            (this.formItemSetConfig.occurrences.minimum == 1 ? 'occurrence is' : 'occurrences are') + ' required';
            requiredMark = '<sub title="' + requiredTitle + '">*</sub>';
        }
        var label = {
            xtype: 'component',
            cls: 'admin-drag-handle',
            html: '<h6>' + (me.formItemSetConfig.label || '{No label}') + requiredMark + ': </h6>'
        };
        var removeBtn = {
            tdAttrs: {
                align: 'right'
            },
            xtype: 'button',
            iconCls: 'icon-remove icon-2x icon-grey',
            itemId: 'remove-block-button',
            cls: 'nobg',
            padding: 0,
            scale: 'medium',
            handler: function (btn) {
                me.removeCopy();
            }
        };
        var items = closable ? [
            label,
            removeBtn
        ] : [
            label
        ];
        return {
            xtype: 'container',
            margin: '10 0 10 0',
            padding: '0 0 5 0',
            dock: 'top',
            cls: 'header',
            layout: {
                type: 'table',
                columns: 3,
                tableAttrs: {
                    style: 'width: 100%'
                }
            },
            items: items
        };
    },
    initSortable: function () {
        var proxyIconPath = Admin.lib.UriHelper.getAbsoluteUri('admin/resources/images/icons/128x128/form_blue.png');
        new Admin.lib.Sortable(this.up(), {
            proxyHtml: '<div><img src="' + proxyIconPath + '"/></div>',
            group: this.name,
            name: this.formItemSetConfig.label,
            handle: '.admin-drag-handle'
        });
    },
    setCollapsed: function (collapsed) {
        this.items.each(function (item) {
            item.setVisible(collapsed);
        });
    },
    getValue: function () {
        var value = [];
        var me = this;
        me.items.each(function (item, index) {
            if (item.getValue) {
                var currentItemValue = item.getValue();
                if (currentItemValue instanceof Array) {
                    Ext.each(currentItemValue, function (itemValue) {
                        itemValue.path = me.name.concat('[', me.copyNo - 1, ']', '.', itemValue.path);
                    });
                } else {
                    currentItemValue.path = me.name.concat('[', me.copyNo - 1, ']', '.', currentItemValue);
                }
                value = value.concat(currentItemValue);
            }
        });
        return value;
    },
    setValue: function () {
    },
    getConfig: function () {
        return this.formItemSetConfig;
    }
});
Ext.define('Admin.view.contentManager.wizard.form.FormItemSetContainer', {
    extend: 'Admin.view.contentManager.wizard.form.FormItemContainer',
    alias: 'widget.formItemSetContainer',
    cls: 'admin-formitemset-container',
    padding: '10 10 10 10',
    margin: '0 0 10 0',
    updateControlsState: function () {
        var formItemsPanel = this.down('#formItemsPanel');
        var addButton = this.down('#addButton');
        var collapseButton = this.down('#collapseButton');
        if (formItemsPanel) {
            var last = formItemsPanel.items.last();
            if (addButton) {
                addButton.setDisabled(last && last.copyNo === this.maxFields);
            }
            if (collapseButton) {
                collapseButton.setVisible(formItemsPanel.items.getCount() > 0);
            }
        }
    },
    createControls: function () {
        var me = this;
        var addButton = {
            xtype: 'button',
            itemId: 'addButton',
            style: {
                float: 'left'
            },
            disabled: this.maxFields === 1,
            text: 'Add ' + this.field.formItemSetConfig.label,
            ui: 'dark-grey',
            handler: function () {
                var formItemsPanel = me.down('#formItemsPanel');
                var last = formItemsPanel.items.last();
                if (last) {
                    last.addCopy();
                } else {
                    formItemsPanel.add(me.field.cloneConfig());
                }
                me.updateControlsState();
            }
        };
        var collapseButton = {
            xtype: 'component',
            itemId: 'collapseButton',
            renderSelectors: {
                linkEl: 'span'
            },
            style: {
                float: 'right'
            },
            listeners: {
                click: {
                    element: 'linkEl',
                    fn: function () {
                        var formItemsPanel = me.down('#formItemsPanel');
                        formItemsPanel.items.each(function (item) {
                            item.setCollapsed(me.isCollapsed);
                        });
                        me.isCollapsed = !me.isCollapsed;
                        this.setHTML(me.isCollapsed ? 'Expand' : 'Collapse');
                    }
                }
            },
            html: '<span class="admin-text-button admin-collapse-all-button" href="javascript:;">Collapse</span>'
        };
        if ((this.minFields !== this.maxFields) || (this.maxFields === 0)) {
            return [
                addButton,
                collapseButton
            ];
        } else {
            return [
                collapseButton
            ];
        }
    }
});
Ext.define('Admin.view.contentManager.wizard.form.InputContainer', {
    extend: 'Admin.view.contentManager.wizard.form.FormItemContainer',
    alias: 'widget.inputContainer',
    updateControlsState: function () {
        var formItemsPanel = this.down('#formItemsPanel');
        var addButton = this.down('#addButton');
        if (formItemsPanel && addButton) {
            var last = formItemsPanel.items.last();
            addButton.setDisabled(last && last.copyNo === this.maxFields);
        }
    },
    createControls: function () {
        var me = this;
        if ((this.maxFields > 1 && this.minFields !== this.maxFields) || (this.maxFields === 0)) {
            return [
                {
                    xtype: 'button',
                    itemId: 'addButton',
                    ui: 'dark-grey',
                    text: 'Add',
                    listeners: {
                        click: function () {
                            var formItemsPanel = me.down('#formItemsPanel');
                            var last = formItemsPanel.items.last();
                            if (last) {
                                last.addCopy();
                            } else {
                                formItemsPanel.add(me.field.cloneConfig());
                            }
                            me.updateControlsState();
                        }
                    }
                }
            ];
        } else {
            return [];
        }
    }
});
Ext.define('Admin.view.contentManager.wizard.form.input.Base', {
    extend: 'Ext.form.FieldContainer',
    label: '',
    inputConfig: undefined,
    labelWidth: 110,
    labelPad: 0,
    layout: {
        type: 'column'
    },
    mixins: {
        formItemOccurrencesHandler: 'Admin.view.contentManager.wizard.form.FormItemOccurrencesHandler'
    },
    defaultOccurrencesHandling: true,
    listeners: {
        beforerender: function () {
            if (this.defaultOccurrencesHandling) {
                this.handleOccurrences(this.inputConfig.occurrences.minimum);
            }
        },
        copyadded: function () {
            this.updateButtonState();
        },
        copyremoved: function () {
            this.updateButtonState();
        }
    },
    initComponent: function () {
        this.defaults = {
            width: 500
        };
        if (this.defaultOccurrencesHandling && this.inputConfig.occurrences.maximum !== 1) {
            this.items.push(this.createDeleteButton());
        }
        this.callParent(arguments);
    },
    getValue: function () {
        return {
            path: this.name.concat('[', this.copyNo - 1, ']'),
            value: this.items.items[0].getValue()
        };
    },
    setValue: function (value) {
    },
    createDeleteButton: function () {
        var element = this;
        return {
            xtype: 'button',
            mode: 'delete',
            itemId: 'delete-button',
            iconCls: 'icon-remove icon-2x',
            cls: 'nobg icon-button',
            scale: 'medium',
            width: '24',
            listeners: {
                click: function () {
                    var remainElement = element.removeCopy();
                    if (remainElement) {
                        remainElement.updateButtonState();
                    }
                }
            }
        };
    },
    setButtonDisabled: function (disabled) {
        var button = this.down('#add-delete-button');
        if (button) {
            button.setDisabled(disabled);
        }
    },
    updateButtonState: function () {
        var totalCount = 1;
        var tmp = this;
        while (tmp.prevField) {
            tmp = tmp.prevField;
        }
        var root = tmp;
        while (tmp.nextField) {
            tmp = tmp.nextField;
            totalCount++;
        }
        root.updateButtonStateInternal(totalCount);
    },
    updateButtonStateInternal: function (totalCount) {
        var min = this.inputConfig.occurrences.minimum;
        this.setButtonDisabled(totalCount === min && this.copyNo !== totalCount);
        if (this.nextField) {
            this.nextField.updateButtonStateInternal(totalCount);
        }
    },
    getConfig: function () {
        return this.inputConfig;
    }
});
Ext.define('Admin.view.contentManager.wizard.form.input.HtmlArea', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.HtmlArea',
    initComponent: function () {
        var me = this;
        me.items = [
            me.createEditor(),
            me.createHiddenInput()
        ];
        me.callParent();
        me.getContentEditable().on('afterrender', function (component) {
            me.setContentEditableHtml(me.value.length > 0 ? me.value[0].value : '');
        });
    },
    getValue: function () {
        var me = this;
        me.copyContentEditableHtmlToHiddenDiv();
        var value = {
            path: me.name,
            value: me.getHiddenInput().getValue()
        };
        return value;
    },
    setValue: function (val) {
        this.getHiddenInput().setValue(val);
    },
    setContentEditableHtml: function (html) {
        var me = this, ce = me.getContentEditable();
        if (ce) {
            ce.getEl().setHTML(html);
        }
    },
    getContentEditable: function () {
        return this.down('#contentEditableDiv');
    },
    getHiddenInput: function () {
        return this.down('#' + this.name);
    },
    copyContentEditableHtmlToHiddenDiv: function () {
        var value = this.down('#contentEditableDiv').getEl().getHTML();
        this.down('#' + this.name).setValue(value);
    },
    createEditor: function () {
        var me = this;
        return {
            xtype: 'container',
            layout: 'vbox',
            items: [
                me.createToolbar(),
                me.createContentEditableDiv()
            ]
        };
    },
    createToolbar: function () {
        var ne = this;
        return {
            xtype: 'component',
            layout: 'vbox',
            width: 500,
            height: 40,
            cls: 'admin-htmlarea-dummy-toolbar'
        };
    },
    createContentEditableDiv: function () {
        var me = this;
        return {
            xtype: 'component',
            width: 500,
            itemId: 'contentEditableDiv',
            cls: '.admin-html-area',
            layout: 'vbox',
            autoEl: {
                tag: 'div',
                contenteditable: true
            },
            style: 'border: 1px solid #aaa; min-height: 100px; padding: 4px 9px',
            currentHeight: 100,
            listeners: {
                render: function (component) {
                    component.el.on('DOMSubtreeModified', function (event) {
                        var height = component.getHeight();
                        if (height !== component.currentHeight) {
                            component.currentHeight = height;
                            var parent = component.up();
                            if (Ext.isFunction(parent.doComponentLayout)) {
                                parent.doComponentLayout();
                            }
                        }
                    });
                }
            }
        };
    },
    createHiddenInput: function () {
        return {
            xtype: 'hiddenfield',
            name: this.name,
            itemId: this.name,
            hidden: true,
            value: ''
        };
    }
});
Ext.define('Admin.view.contentManager.wizard.form.input.HtmlArea', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.HtmlArea',
    initComponent: function () {
        this.items = [
            {
                xtype: 'htmleditor',
                name: this.name,
                value: this.value,
                enableFont: false
            }
        ];
        this.callParent(arguments);
    },
    setValue: function (value) {
        this.down('htmleditor').setValue(value);
    }
});
Ext.define('Admin.view.contentManager.wizard.form.input.Image', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.Image',
    cls: 'admin-image-upload',
    width: 500,
    minHeight: 250,
    flex: 1,
    layout: 'card',
    initComponent: function () {
        var me = this;
        me.items = [
            me.createUploadForm(),
            me.createProgressForm(),
            me.createLoadingForm(),
            me.createImageForm()
        ];
        me.listeners = {
            afterrender: me.setupUploader,
            scope: me
        };
        me.callParent(arguments);
        if (me.value && me.value.length > 0) {
            me.on('beforerender', function () {
                me.setValue(me.value);
            });
        }
    },
    createUploadForm: function () {
        return {
            xtype: 'container',
            itemId: 'uploadForm',
            cls: 'admin-upload-input',
            height: 250,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'textfield',
                    emptyText: 'Paste URL to image here',
                    margin: '0 0 5 0',
                    enableKeyEvents: true
                },
                {
                    flex: 1,
                    xtype: 'component',
                    itemId: 'dropZone',
                    cls: 'admin-drop-zone',
                    styleHtmlContent: true
                }
            ]
        };
    },
    createProgressForm: function () {
        return {
            xtype: 'container',
            itemId: 'progressForm',
            cls: 'admin-progress-form',
            height: 250,
            layout: {
                type: 'vbox',
                align: 'center',
                pack: 'center'
            },
            items: [
                {
                    itemId: 'progressBar',
                    width: 500,
                    tpl: '<h3>{percent}% complete</h3>' +
                         '<div class="admin-progress-bar"><div class="admin-progress" style="width: {percent}%;"></div></div>',
                    data: {
                        percent: 0
                    }
                },
                {
                    xtype: 'button',
                    text: 'Cancel',
                    action: 'cancel',
                    cls: 'icon-button',
                    scale: 'medium',
                    width: 125,
                    height: 40,
                    margin: 15,
                    style: {
                        borderColor: '#929292',
                        backgroundColor: '#929292'
                    }
                }
            ]
        };
    },
    createLoadingForm: function () {
        return {
            itemId: 'loadingForm',
            width: '100%',
            height: '100%',
            maxWidth: 500,
            maxHeight: 500,
            html: '<div class="admin-loading-form"><span class="loader"></span></div>'
        };
    },
    createImageForm: function () {
        var me = this;
        return {
            itemId: 'imageForm',
            xtype: 'container',
            width: 500,
            items: [
                {
                    itemId: 'image',
                    xtype: 'component',
                    tpl: [
                        '<tpl for=".">',
                        '<div class="admin-image-form">',
                        '<img src="{iconUrl}?size=500" alt="test image"/>',
                        '</div>',
                        '</tpl>'
                    ]
                },
                {
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'top',
                        pack: 'end'
                    },
                    items: [
                        {
                            xtype: 'button',
                            text: 'Edit',
                            cls: 'icon-button',
                            scale: 'medium',
                            width: 130,
                            height: 30,
                            margin: 10,
                            listeners: {
                                click: {
                                    fn: me.removeUploadedImage,
                                    scope: me
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Remove',
                            cls: 'icon-button',
                            scale: 'medium',
                            width: 130,
                            height: 30,
                            margin: '10 0',
                            style: {
                                borderColor: '#7A7A7A',
                                backgroundColor: '#7A7A7A'
                            },
                            listeners: {
                                click: {
                                    fn: me.removeUploadedImage,
                                    scope: me
                                }
                            }
                        }
                    ]
                }
            ]
        };
    },
    setupUploader: function (container) {
        var me = this, dropZoneEl = container.down('#dropZone').el, cancelBtn = container.down('[action=cancel]');
        me.uploader = new plupload.Uploader({
            runtimes: 'gears,html5,flash',
            browse_button: dropZoneEl.dom.id,
            url: Admin.lib.UriHelper.getAbsoluteUri('admin/rest/upload'),
            multi_selection: false,
            max_file_size: '100mb',
            drop_element: dropZoneEl.dom.id
        });
        me.uploader.bind('Init', function (up) {
            dropZoneEl.update('<h4>' + (!!up.features.dragdrop ? 'Drop files here or click to select' : 'Click to select') + '</h4>');
        });
        me.uploader.bind('QueueChanged', function (up) {
            if (up.files.length == 1) {
                up.start();
            }
        });
        me.uploader.bind('UploadFile', function (up, file) {
            container.getLayout().setActiveItem('progressForm');
        });
        me.uploader.bind('UploadProgress', function (up, file) {
            container.down('#progressBar').update(up.total);
        });
        cancelBtn.on('click', function (up) {
            up.stop();
            up.total.reset();
            up.splice();
            container.getLayout().setActiveItem('uploadForm');
        }, me);
        me.uploader.bind('FileUploaded', function (up, file, response) {
            if (response && response.response) {
                var json = Ext.JSON.decode(response.response);
                if (json.success && json.items && json.items.length == 1) {
                    file.response = json.items[0];
                }
            }
        });
        me.uploader.bind('UploadComplete', function (up, files) {
            container.getLayout().setActiveItem('loadingForm');
            up.total.reset();
            var uploaded = up.splice();
            me.loadFile(uploaded[0]);
        });
        me.uploader.init();
    },
    loadFile: function (file) {
        var me = this;
        me.createTemporaryImageContent(file.response, function (contentModel) {
            me.imageModel = contentModel;
            me.hideLoaderOnImageLoad(contentModel);
        });
    },
    createTemporaryImageContent: function (file, callback) {
        var me = this;
        this.remoteCreateBinary(file.id, function (binaryId) {
            me.remoteCreateImageContent(file.name, file.mimeType, binaryId, function (contentId) {
                var getContentCommand = {
                    contentIds: [
                        contentId
                    ]
                };
                api_remote.RemoteService.content_get(getContentCommand, function (getContentResponse) {
                    if (getContentResponse && getContentResponse.success) {
                        var contentData = getContentResponse.content[0];
                        var contentModel = new Admin.model.contentManager.ContentModel(contentData);
                        callback(contentModel);
                    }
                });
            });
        });
    },
    remoteCreateBinary: function (fileUploadId, callback) {
        var createBinaryCommand = {
            'uploadFileId': fileUploadId
        };
        api_remote.RemoteService.binary_create(createBinaryCommand, function (response) {
            if (response && response.success) {
                callback(response.binaryId);
            } else {
                Ext.Msg.alert("Error", response ? response.error : "Unable to create binary content.");
            }
        });
    },
    remoteCreateImageContent: function (displayName, mimeType, binaryId, callback) {
        var createContentCommand = {
            "contentData": {
                "mimeType": mimeType,
                "binary": binaryId
            },
            "qualifiedContentTypeName": 'system:image',
            "displayName": displayName,
            "temporary": true
        };
        api_remote.RemoteService.content_createOrUpdate(createContentCommand, function (response) {
            if (response && response.success) {
                callback(response.contentId);
            } else {
                Ext.Msg.alert("Error", response ? response.error : "Unable to create image content.");
            }
        });
    },
    hideLoaderOnImageLoad: function (contentModel) {
        var me = this, imageForm = me.getLayout().getLayoutItems()[3];
        imageForm.down('#image').update(contentModel.data);
        Ext.fly(imageForm.getEl().dom).down('img').on('load', function (event, target, opts) {
            me.getLayout().setActiveItem('imageForm');
        });
    },
    removeUploadedImage: function () {
        this.getLayout().setActiveItem('uploadForm');
    },
    getValue: function () {
        var me = this;
        if (!me.imageModel) {
            return null;
        }
        return {
            path: this.name.concat('[0]'),
            value: this.imageModel.data.id
        };
    },
    setValue: function (value) {
        var me = this;
        var getContentCommand = {
            contentIds: [
                value[0].value
            ]
        };
        me.getLayout().setActiveItem('loadingForm');
        api_remote.RemoteService.content_get(getContentCommand, function (getContentResponse) {
            if (getContentResponse && getContentResponse.success) {
                var contentData = getContentResponse.content[0];
                var contentModel = new Admin.model.contentManager.ContentModel(contentData);
                me.imageModel = contentModel;
                me.hideLoaderOnImageLoad(contentModel);
            }
        });
    }
});
Ext.define('Admin.view.contentManager.wizard.form.input.ImageSelector', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.ImageSelector',
    requires: [
        'Admin.store.contentManager.ContentStore',
        'Admin.view.FileUploadWindow',
        'Admin.view.contentManager.wizard.form.ImagePopupDialog'
    ],
    defaultOccurrencesHandling: false,
    initComponent: function () {
        var me = this;
        this.selectedContentStore = this.createSelectedContentStore();
        this.selectedDataView = this.createViewForSelectedContent();
        this.items = [
            this.createHiddenInput(),
            this.createComboBox(),
            this.createOpenLibraryButton(),
            this.createUploadButton(),
            this.selectedDataView
        ];
        if (this.inputConfig && this.inputConfig.type && this.inputConfig.type.config) {
            var getRelationshipTypeCommand = {
                qualifiedRelationshipTypeName: this.inputConfig.type.config.relationshipType,
                format: 'JSON'
            };
            api_remote.RemoteService.relationshipType_get(getRelationshipTypeCommand, function (response) {
                if (response && response.success) {
                    var iconUrl = response.relationshipType.iconUrl;
                    if (me.rendered) {
                        var relationshipTypeIcon = me.el.down('.admin-image-icon');
                        relationshipTypeIcon.set({
                            'src': iconUrl
                        });
                        relationshipTypeIcon.setOpacity(0.5);
                    } else {
                        me.relationshipTypeIconUrl = iconUrl;
                    }
                }
            });
        }
        this.callParent(arguments);
        this.selectedDataView.on('viewready', function () {
            me.setValue(me.value);
        });
    },
    getValue: function () {
        var value = this.items.items[0].getValue();
        if (value && Ext.isString(value)) {
            value = value.split(',');
        } else {
            return [];
        }
        var valueList = [];
        for (var i = 0; i < value.length; i++) {
            var currentItemValue = {
                'path': this.name.concat('[', i, ']'),
                'value': value[i]
            };
            valueList.push(currentItemValue);
        }
        return valueList;
    },
    setValue: function (values) {
        var me = this;
        var getContentCommand = {
            contentIds: Ext.Array.pluck(values, 'value')
        };
        api_remote.RemoteService.content_get(getContentCommand, function (getContentResponse) {
            if (getContentResponse && getContentResponse.success) {
                Ext.each(getContentResponse.content, function (contentData) {
                    var contentModel = new Admin.model.contentManager.ContentModel(contentData);
                    me.selectedContentStore.add(contentModel);
                    me.hideLoaderOnImageLoad(contentModel);
                });
            }
        });
    },
    createSelectedContentStore: function () {
        var me = this;
        return Ext.create('Ext.data.Store', {
            model: 'Admin.model.contentManager.ContentModel',
            data: [],
            listeners: {
                datachanged: function (store) {
                    me.updateHiddenValue();
                    if (me.contentStore) {
                        me.contentStore.clearFilter(true);
                        me.contentStore.filter({
                            filterFn: function (content) {
                                var existing = me.selectedContentStore.findRecord('id', content.get('id'));
                                if (existing) {
                                    content.set('grayedOutComboItem', 'admin-inputimage-combo-grayed-out-item');
                                } else {
                                    content.set('grayedOutComboItem', '');
                                }
                                return true;
                            }
                        });
                    }
                    try {
                        me.down('combobox').setDisabled(me.selectedContentStore.getCount() ===
                                                        me.contentTypeItemConfig.occurrences.maximum);
                    }
                    catch (exception) {
                    }
                }
            }
        });
    },
    createHiddenInput: function () {
        return {
            xtype: 'hiddenfield',
            name: this.name,
            itemId: this.name,
            value: ''
        };
    },
    createComboBox: function () {
        var me = this;
        var fieldTpl = [
            '<div class="{hiddenDataCls}" role="presentation"></div>',
            '<input id="{id}" type="{type}" {inputAttrTpl} class="{fieldCls} {typeCls} {editableCls}" autocomplete="off"',
            '<tpl if="value"> value="{[Ext.util.Format.htmlEncode(values.value)]}"</tpl>',
            '<tpl if="name"> name="{name}"</tpl>',
            '<tpl if="placeholder"> placeholder="{placeholder}"</tpl>',
            '<tpl if="size"> size="{size}"</tpl>',
            '<tpl if="maxLength !== undefined"> maxlength="{maxLength}"</tpl>',
            '<tpl if="readOnly"> readonly="readonly"</tpl>',
            '<tpl if="disabled"> disabled="disabled"</tpl>',
            '<tpl if="tabIdx"> tabIndex="{tabIdx}"</tpl>',
            '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
            '/>',
            '<img src="{relationshipTypeIconUrl}" class="admin-image-icon"/>',
            {
                compiled: true,
                disableFormats: true
            }
        ];
        var listItemTpl = [
            '<tpl for=".">',
            '   <div role="option" class="x-boundlist-item {grayedOutComboItem}">',
            '       <img src="{iconUrl}?size=32" alt="{displayName}" width="32" height="32"/>',
            '       <div class="info">',
            '           <h6>{displayName}</h6>',
            '           <div style="color: #666">{path}</div>',
            '       </div>',
            '       <div class="x-clear"></div>',
            '   </div>',
            '</tpl>'
        ];
        this.contentStore = new Admin.store.contentManager.ContentStore();
        this.contentStore.proxy.extraParams = {
            contentTypes: [
                'system:image'
            ]
        };
        var combo = {
            xtype: 'combo',
            emptyText: 'Start typing',
            submitValue: false,
            hideTrigger: true,
            forceSelection: true,
            minChars: 1,
            queryMode: 'remote',
            queryParam: 'fulltext',
            autoSelect: false,
            width: 435,
            fieldCls: 'admin-inputimage-input',
            displayField: 'displayName',
            valueField: 'id',
            tpl: listItemTpl,
            fieldSubTpl: fieldTpl,
            cls: 'admin-inputimage-combo',
            listConfig: {
                cls: 'admin-inputimage-list',
                emptyText: 'No matching items'
            },
            store: this.contentStore,
            listeners: {
                select: function (combo, records) {
                    combo.setValue('');
                    me.onContentSelected(records);
                },
                beforeselect: function (combo, record, index) {
                    return record.data['grayedOutComboItem'];
                }
            }
        };
        return combo;
    },
    createUploadButton: function () {
        var me = this;
        return {
            xtype: 'button',
            itemId: 'uploadButton',
            tooltip: 'Upload image',
            iconCls: 'admin-inputimage-upload-icon',
            cls: 'nobg icon-button',
            scale: 'medium',
            width: '24',
            margin: '5 5',
            handler: function () {
                me.getFileUploadWindow().show();
            }
        };
    },
    createOpenLibraryButton: function () {
        var me = this;
        return {
            xtype: 'button',
            itemId: 'openLibraryButton',
            tooltip: 'Open Library',
            iconCls: 'admin-inputimage-library-icon',
            cls: 'nobg icon-button',
            scale: 'medium',
            width: '24',
            margin: '5 5',
            handler: function () {
                me.onLibraryButtonClicked();
            }
        };
    },
    createViewForSelectedContent: function () {
        var me = this;
        var template = new Ext.XTemplate('<tpl for=".">', '   <div class="admin-inputimage">',
            '       <img class="image" src="{iconUrl}?size=140&thumbnail=false"/>', '       <div class="loader"></div>',
            '       <div class="bottom-bar">', '           <h6>{displayName}</h6>', '       </div>',
            '       <div class="admin-zoom" style="background-image: url({iconUrl}?size=140&thumbnail=false);"></div>', '   </div>',
            '</tpl>');
        return Ext.create('Ext.view.View', {
            store: me.selectedContentStore,
            tpl: template,
            itemSelector: 'div.admin-inputimage',
            selectedItemCls: 'admin-inputimage-selected',
            itemId: 'selectionView',
            emptyText: '',
            trackOver: true,
            overItemCls: 'over',
            deferEmptyText: false,
            deferInitialRefresh: false,
            width: 500,
            listeners: {
                itemclick: function (view, contentModel, item, index, e) {
                    var clickedElement = Ext.fly(e.target);
                    var viewEl = view.getEl();
                    if (clickedElement.hasCls('admin-zoom')) {
                        view.getSelectionModel().deselectAll();
                        return false;
                    } else {
                        var offset = (index + 1) % 3 > 0 ? 3 - (index + 1) % 3 : 0;
                        var insertPoint = viewEl.query('.admin-inputimage')[index + offset];
                        var picker = me.createImageDialog(view, contentModel);
                        if (insertPoint) {
                            picker.getEl().insertAfter(insertPoint);
                        } else {
                            picker.getEl().insertAfter(viewEl.last());
                        }
                    }
                },
                itemadd: function (contentModel, index, htmlElements) {
                    this.getSelectionModel().deselectAll();
                    if ((index + 1) % 3 === 0) {
                        Ext.fly(htmlElements[0]).addCls('admin-inputimage-last');
                    }
                },
                itemupdate: function () {
                    me.refreshListLayout();
                },
                itemremove: function () {
                    me.refreshListLayout();
                },
                deselect: function () {
                    if (me.getImageDialog()) {
                        me.getImageDialog().hide();
                        var parent = me.up();
                        if (Ext.isFunction(parent.doComponentLayout)) {
                            parent.doComponentLayout();
                        }
                    }
                },
                select: function () {
                    if (me.getImageDialog()) {
                        me.getImageDialog().show();
                        var parent = me.up();
                        if (Ext.isFunction(parent.doComponentLayout)) {
                            parent.doComponentLayout();
                        }
                    }
                },
                refresh: function () {
                    me.refreshListLayout();
                }
            }
        });
    },
    refreshListLayout: function () {
        var imageItems = this.selectedDataView.getEl().query('.admin-inputimage');
        Ext.each(imageItems, function (imageItem, index) {
            Ext.fly(imageItem).removeCls('admin-inputimage-last');
            if ((index + 1) % 3 === 0) {
                Ext.fly(imageItem).addCls('admin-inputimage-last');
            }
        });
    },
    alertContentIsAdded: function (contentModel) {
        console.log('Temporary alert! Can not have duplicates in Image input\n"' + contentModel.get('path') + '" has already been added');
        this.down('combobox').focus('');
    },
    updateHiddenValue: function () {
        var me = this;
        var ids = [];
        if (this.items) {
            Ext.Array.each(me.selectedContentStore.data.items, function (item) {
                ids.push(item.data.id);
            });
            this.getComponent(this.name).setValue(ids);
        }
    },
    onContentSelected: function (contentModels) {
        var contentModel = contentModels[0];
        var isAlreadyAdded = this.selectedContentStore.findRecord('id', contentModel.get('id'));
        if (isAlreadyAdded) {
            this.alertContentIsAdded(contentModel);
            return;
        }
        this.selectedContentStore.add(contentModel);
        this.hideLoaderOnImageLoad(contentModel);
    },
    onLibraryButtonClicked: function () {
        alert('Open library now');
    },
    onFilesUploaded: function (win, files) {
        var me = this;
        Ext.each(files, function (file) {
            me.createTemporaryImageContent(file.response, function (contentModel) {
                me.selectedContentStore.add(contentModel);
                me.hideLoaderOnImageLoad(contentModel);
            });
        });
        win.close();
    },
    hideLoaderOnImageLoad: function (contentModel) {
        if (this.selectedDataView) {
            var node = this.selectedDataView.getNode(contentModel);
            if (node) {
                Ext.fly(node).down('img').on('load', function (event, target, opts) {
                    Ext.fly(target).next('.loader').destroy();
                });
            }
        }
    },
    createTemporaryImageContent: function (file, callback) {
        var me = this;
        this.remoteCreateBinary(file.id, function (binaryId) {
            me.remoteCreateImageContent(file.name, file.mimeType, binaryId, function (contentId) {
                var getContentCommand = {
                    contentIds: [
                        contentId
                    ]
                };
                api_remote.RemoteService.content_get(getContentCommand, function (getContentResponse) {
                    if (getContentResponse && getContentResponse.success) {
                        var contentData = getContentResponse.content[0];
                        var contentModel = new Admin.model.contentManager.ContentModel(contentData);
                        callback(contentModel);
                    }
                });
            });
        });
    },
    remoteCreateBinary: function (fileUploadId, callback) {
        var createBinaryCommand = {
            'uploadFileId': fileUploadId
        };
        api_remote.RemoteService.binary_create(createBinaryCommand, function (response) {
            if (response && response.success) {
                callback(response.binaryId);
            } else {
                Ext.Msg.alert("Error", response ? response.error : "Unable to create binary content.");
            }
        });
    },
    remoteCreateImageContent: function (displayName, mimeType, binaryId, callback) {
        var createContentCommand = {
            "contentData": {
                "mimeType": mimeType,
                "binary": binaryId
            },
            "qualifiedContentTypeName": 'system:image',
            "displayName": displayName,
            "temporary": true
        };
        api_remote.RemoteService.content_createOrUpdate(createContentCommand, function (response) {
            if (response && response.success) {
                callback(response.contentId);
            } else {
                Ext.Msg.alert("Error", response ? response.error : "Unable to create image content.");
            }
        });
    },
    getFileUploadWindow: function () {
        var win = Ext.ComponentQuery.query('fileUploadWindow')[0];
        if (!win) {
            win = Ext.create('widget.fileUploadWindow');
            win.on('uploadcomplete', this.onFilesUploaded, this);
        }
        return win;
    },
    createImageDialog: function (view, model) {
        var me = this;
        if (this.dialog) {
            this.dialog.updateTpl(model.data);
            return this.dialog;
        } else {
            this.dialog = Ext.create('widget.imagePopupDialog', {
                renderTo: view.getEl(),
                data: model.data,
                removeHandler: function () {
                    var selectionModel = view.getSelectionModel();
                    var selection = selectionModel.getSelection();
                    if (selection.length > 0) {
                        selectionModel.deselectAll();
                        me.selectedContentStore.remove(selection[0]);
                    }
                },
                editHandler: function () {
                    alert('TODO: Implement Edit functionality');
                }
            });
            return this.dialog;
        }
    },
    getImageDialog: function () {
        return this.dialog;
    }
});
Ext.define('Admin.view.contentManager.wizard.form.input.Relationship', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.Relationship',
    requires: [
        'Admin.store.contentManager.ContentStore'
    ],
    defaultOccurrencesHandling: false,
    contentStore: null,
    initComponent: function () {
        var me = this;
        me.selectedContentStore = me.createSelectedContentStore();
        me.items = [
            me.createHiddenInput(),
            me.createComboBox(),
            me.createOpenLibraryButton(),
            me.createViewForSelectedContent()
        ];
        if (me.inputConfig && me.inputConfig.type && me.inputConfig.type.config) {
            var getRelationshipTypeCommand = {
                qualifiedRelationshipTypeName: me.inputConfig.type.config.relationshipType,
                format: 'JSON'
            };
            api_remote.RemoteService.relationshipType_get(getRelationshipTypeCommand, function (response) {
                if (response && response.success) {
                    var iconUrl = response.relationshipType.iconUrl;
                    if (me.rendered) {
                        me.el.down('.admin-image-icon').set({
                            'src': iconUrl
                        });
                    } else {
                        me.relationshipTypeIconUrl = iconUrl;
                    }
                }
            });
        }
        me.callParent(arguments);
        this.setValue(this.value);
    },
    createHiddenInput: function () {
        return {
            xtype: 'hiddenfield',
            name: this.name,
            itemId: this.name,
            value: ''
        };
    },
    createComboBox: function () {
        var me = this;
        var fieldTpl = [
            '<div class="{hiddenDataCls}" role="presentation"></div>',
            '<input id="{id}" type="{type}" {inputAttrTpl} class="{fieldCls} {typeCls} {editableCls}" autocomplete="off"',
            '<tpl if="value"> value="{[Ext.util.Format.htmlEncode(values.value)]}"</tpl>',
            '<tpl if="name"> name="{name}"</tpl>',
            '<tpl if="placeholder"> placeholder="{placeholder}"</tpl>',
            '<tpl if="size"> size="{size}"</tpl>',
            '<tpl if="maxLength !== undefined"> maxlength="{maxLength}"</tpl>',
            '<tpl if="readOnly"> readonly="readonly"</tpl>',
            '<tpl if="disabled"> disabled="disabled"</tpl>',
            '<tpl if="tabIdx"> tabIndex="{tabIdx}"</tpl>',
            '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
            '/>',
            '<img src="{relationshipTypeIconUrl}" class="admin-image-icon"/>',
            {
                compiled: true,
                disableFormats: true
            }
        ];
        var listItemTpl = [
            '<tpl for=".">',
            '   <div role="option" class="x-boundlist-item {grayedOutComboItem}">',
            '       <img src="{iconUrl}?size=32" alt="{displayName}" width="32" height="32"/>',
            '       <div class="info">',
            '           <h6>{displayName}</h6>',
            '           <div style="color: #666">{path}</div>',
            '       </div>',
            '       <div class="x-clear"></div>',
            '   </div>',
            '</tpl>'
        ];
        me.contentStore = new Admin.store.contentManager.ContentStore({
            filters: [
                function (content) {
                    return !me.selectedContentStore.findRecord('id', content.get('id'));
                }            ]
        });
        var relationshipTypeName = me.inputConfig.type.config.relationshipType;
        me.remoteGetRelationshipType(relationshipTypeName, function (relationshipType) {
            var allowedContentTypes = relationshipType.allowedToTypes;
            if (!Ext.isEmpty(allowedContentTypes)) {
                me.contentStore.proxy.extraParams = {
                    'contentTypes': allowedContentTypes
                };
            }
        });
        var combo = {
            xtype: 'combo',
            itemId: 'relationshipCombo',
            name: '_system_relation_combo',
            submitValue: false,
            hideTrigger: true,
            forceSelection: true,
            minChars: 1,
            queryMode: 'remote',
            queryParam: 'fulltext',
            autoSelect: false,
            displayField: 'displayName',
            valueField: 'id',
            width: 468,
            fieldCls: 'admin-relationship-input',
            emptyText: 'Start typing',
            fieldSubTpl: fieldTpl,
            tpl: listItemTpl,
            cls: 'admin-relationship-combo',
            listConfig: {
                cls: 'admin-relationship-list',
                emptyText: 'No matching items'
            },
            displayTpl: new Ext.XTemplate('<tpl for=".">', '{displayName}', '</tpl>'),
            store: me.contentStore,
            listeners: {
                select: function (combo, records) {
                    combo.setValue('');
                    me.onSelectContent(records);
                },
                beforeselect: function (combo, record, index) {
                    return record.data['grayedOutComboItem'];
                }
            }
        };
        return combo;
    },
    onSelectContent: function (contentModels) {
        var contentModel = contentModels[0];
        var isAlreadyAdded = this.selectedContentStore.findRecord('id', contentModel.get('id'));
        if (isAlreadyAdded) {
            this.alertContentIsAdded(contentModel);
            return;
        }
        this.selectedContentStore.add(contentModel);
    },
    getValue: function () {
        var value = this.items.items[0].getValue();
        if (value && Ext.isString(value)) {
            value = value.split(',');
        } else {
            return [];
        }
        var valueList = [];
        var i;
        for (i = 0; i < value.length; i++) {
            var currentItemValue = {
                'path': this.name.concat('[', i, ']'),
                'value': value[i]
            };
            valueList.push(currentItemValue);
        }
        return valueList;
    },
    setValue: function (values) {
        var me = this;
        var getContentCommand = {
            contentIds: Ext.Array.pluck(values, 'value')
        };
        api_remote.RemoteService.content_get(getContentCommand, function (getContentResponse) {
            if (getContentResponse && getContentResponse.success) {
                Ext.each(getContentResponse.content, function (contentData) {
                    var contentModel = new Admin.model.contentManager.ContentModel(contentData);
                    me.selectedContentStore.add(contentModel);
                });
            }
        });
    },
    createSelectedContentStore: function () {
        var me = this;
        var max = this.inputConfig.occurrences.maximum;
        var min = this.inputConfig.occurrences.minimum;
        return Ext.create('Ext.data.Store', {
            model: 'Admin.model.contentManager.ContentModel',
            data: [],
            listeners: {
                datachanged: function (store) {
                    me.updateHiddenValue();
                    if (me.contentStore) {
                        me.contentStore.clearFilter(true);
                        me.contentStore.filter({
                            filterFn: function (content) {
                                var existing = me.selectedContentStore.findRecord('id', content.get('id'));
                                if (existing) {
                                    content.set('grayedOutComboItem', 'admin-relationship-combo-grayed-out-item');
                                } else {
                                    content.set('grayedOutComboItem', '');
                                }
                                return true;
                            }
                        });
                    }
                    try {
                        if (max > 0) {
                            me.down('#relationshipCombo').setDisabled(store.getCount() === max);
                        }
                    }
                    catch (exception) {
                    }
                }
            }
        });
    },
    createOpenLibraryButton: function () {
        var me = this;
        return {
            xtype: 'button',
            itemId: 'openLibraryButton',
            tooltip: 'Open Library',
            iconCls: 'admin-relationship-library-icon',
            cls: 'nobg icon-button',
            scale: 'medium',
            width: '24',
            margin: '6',
            handler: function () {
                me.onLibraryButtonClicked();
            }
        };
    },
    createViewForSelectedContent: function () {
        var me = this;
        var min = this.inputConfig.occurrences.minimum;
        var template = new Ext.XTemplate('<tpl for=".">', '   <div class="admin-related-item">',
            '       <img src="{iconUrl}" alt="{displayName}" width="32" height="32"/>', '       <span class="center-column">',
            '           {displayName}', '           <p style="color: #666">{path}</p>', '       </span>',
            '       <span class="right-column"><a href="javascript:;" class="icon-remove icon-2x"></a></span>', '   </div>', '</tpl>');
        return Ext.create('Ext.view.View', {
            store: me.selectedContentStore,
            itemId: 'relationshipView',
            tpl: template,
            itemSelector: 'div.admin-related-item',
            emptyText: '',
            deferEmptyText: false,
            listeners: {
                itemclick: function (view, contentModel, item, index, e) {
                    var clickedElement = Ext.fly(e.target);
                    if (clickedElement.hasCls('icon-remove')) {
                        me.selectedContentStore.remove(contentModel);
                    }
                }
            }
        });
    },
    onLibraryButtonClicked: function () {
        alert('Open library now');
    },
    alertContentIsAdded: function (contentModel) {
        console.log('Temporary alert! Can not have duplicates in Relationship input\n"' + contentModel.get('path') +
                    '" has already been added');
        this.down('combobox').focus('');
    },
    updateHiddenValue: function () {
        var me = this;
        var keys = [];
        if (this.items) {
            Ext.Array.each(me.selectedContentStore.getRange(), function (item) {
                keys.push(item.get('id'));
            });
            this.getComponent(this.name).setValue(keys);
        }
    },
    remoteGetRelationshipType: function (relationshipTypeName, callback) {
        var getRelationshipTypeCommand = {
            'qualifiedRelationshipTypeName': relationshipTypeName,
            'format': 'JSON'
        };
        api_remote.RemoteService.relationshipType_get(getRelationshipTypeCommand, function (response) {
            if (response && response.success) {
                callback(response.relationshipType);
            } else {
                Ext.Msg.alert("Error", response ? response.error : "Unable to load relationship type");
            }
        });
    }
});
Ext.define('Admin.view.contentManager.wizard.form.input.TextArea', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.TextArea',
    label: 'Text Area',
    initComponent: function () {
        this.items = [
            {
                xtype: 'textarea',
                displayNameSource: true,
                name: this.name,
                value: this.value,
                enableKeyEvents: true
            }
        ];
        this.callParent(arguments);
    },
    setValue: function (value) {
        this.down('textarea').setValue(value);
    }
});
Ext.define('Admin.view.contentManager.wizard.form.input.TextLine', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.TextLine',
    initComponent: function () {
        this.items = [
            {
                xtype: 'textfield',
                displayNameSource: true,
                name: this.name,
                value: this.value,
                enableKeyEvents: true
            }
        ];
        this.callParent(arguments);
    },
    setValue: function (value) {
        this.down('textfield').setValue(value);
    }
});
Ext.define('Admin.view.contentManager.wizard.ContentWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentWizardToolbar',
    requires: [
        'Ext.ux.toggleslide.ToggleSlide'
    ],
    border: false,
    cls: 'admin-toolbar',
    isNewGroup: true,
    isLiveMode: false,
    defaults: {
        scale: 'medium'
    },
    initComponent: function () {
        var me = this;
        this.items = [
            {
                text: 'Save',
                itemId: 'save',
                action: 'saveContent'
            },
            {
                text: 'Preview',
                itemId: 'preview',
                action: 'previewContent'
            },
            {
                text: 'Publish',
                itemId: 'publish',
                action: 'publishContent'
            },
            {
                text: 'Delete',
                itemId: 'delete',
                action: 'deleteContent'
            },
            {
                text: 'Duplicate',
                itemId: 'duplicate',
                action: 'duplicateContent'
            },
            {
                text: 'Move',
                itemId: 'move',
                action: 'moveContent'
            },
            {
                text: 'Export',
                itemId: 'export',
                action: 'exportContent'
            },
            '-',
            {
                text: 'Close',
                action: 'closeWizard'
            },
            '->',
            {
                xtype: 'toggleslide',
                onText: 'Live',
                offText: 'Form',
                action: 'toggleLive',
                state: this.isLiveMode,
                listeners: {
                    change: function (toggle, state) {
                        me.isLiveMode = state;
                    }
                }
            },
            {
                text: 'Close',
                action: 'closeWizard'
            }
        ];
        this.callParent(arguments);
    }
});
Ext.define('Admin.view.contentManager.wizard.WizardToolbarMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.wizardToolbarMenu',
    cls: 'admin-context-menu',
    border: false,
    items: [
        {
            text: 'Open Context Window',
            icon: undefined,
            action: 'fixme'
        }
    ]
});
Ext.define('Admin.view.contentManager.wizard.ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',
    requires: [
        'Admin.view.contentManager.wizard.form.FieldSetLayout',
        'Admin.view.contentManager.wizard.form.FormItemSet',
        'Admin.view.contentManager.wizard.form.input.HtmlArea',
        'Admin.view.contentManager.wizard.form.input.Relationship',
        'Admin.view.contentManager.wizard.form.input.TextArea',
        'Admin.view.contentManager.wizard.form.input.TextLine',
        'Admin.view.contentManager.wizard.form.input.Image',
        'Admin.view.contentManager.wizard.form.input.ImageSelector',
        'Admin.view.contentManager.wizard.form.InputContainer',
        'Admin.view.contentManager.wizard.form.FormItemSetContainer'
    ],
    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator'
    },
    maxWidth: 680,
    contentType: undefined,
    bodyPadding: '0 0 100 0',
    content: null,
    jsonSubmit: true,
    autoDestroy: true,
    listeners: {
        afterlayout: function () {
            this.setBodyStyle('height', 'auto');
        }
    },
    initComponent: function () {
        this.items = [];
        var contentData = !Ext.isEmpty(this.content) ? this.content.data : undefined;
        this.addComponentsBasedOnContentType(this.contentType.form, this, contentData);
        this.callParent(arguments);
    },
    getData: function () {
        return this.getContentData();
    },
    getContentData: function () {
        return this.buildContentData();
    },
    buildContentData: function () {
        var me = this, formItems = me.items.items, contentData = {
        };
        Ext.Array.each(formItems, function (item) {
            var currentItemValue = (item).getValue();
            if (currentItemValue instanceof Array) {
                Ext.each(currentItemValue, function (itemValue) {
                    contentData[itemValue.path] = itemValue.value;
                });
            } else {
                contentData[currentItemValue.path] = currentItemValue.value;
            }
        });
        return contentData;
    }
});
Ext.define('Admin.view.contentManager.wizard.ContentWizardPanel', {
    extend: 'Admin.view.WizardPanel',
    alias: 'widget.contentWizardPanel',
    requires: [
        'Admin.view.WizardHeader',
        'Admin.view.contentManager.wizard.ContentWizardToolbar',
        'Admin.view.contentManager.wizard.ContentDataPanel'
    ],
    header: false,
    border: 0,
    autoScroll: false,
    evaluateDisplayName: true,
    contentNameOverridden: false,
    defaults: {
        border: false
    },
    listeners: {
        copyremoved: function (copy) {
            var me = this;
            var data = this.getData();
            var copyData = copy.getValue();
            if (copyData instanceof Array) {
                Ext.each(copyData, function (copyDataItem) {
                    me.deleteData(copyDataItem.path);
                });
            } else {
                this.deleteData(copyData.path);
            }
        }
    },
    initComponent: function () {
        var me = this;
        this.evaluateDisplayName = this.isNewContent();
        this.callParent(arguments);
    },
    prepareHeaderData: function (data) {
        var contentPath = '';
        var isRoot = false;
        var isNew = true;
        if (this.content) {
            if (!Ext.isEmpty(this.content.path)) {
                contentPath = this.content.path;
                isNew = false;
            }
            if (Ext.isDefined(this.content.isRoot)) {
                isRoot = this.content.isRoot;
            }
        }
        if (isNew && this.contentParent) {
            if (!Ext.isEmpty(this.contentParent.path)) {
                var isParentRoot = !this.contentParent.deletable || false;
                contentPath = this.contentParent.path + (isParentRoot ? '' : '/');
            }
        }
        var lastSlashIndex = contentPath.lastIndexOf('/');
        var contentName = '/';
        if (lastSlashIndex >= 0) {
            contentName = contentPath.substring(lastSlashIndex + (isRoot ? 0 : 1));
            contentPath = contentPath.substring(0, lastSlashIndex + (isRoot ? 0 : 1));
        }
        return {
            imageUrl: this.content ? this.content.iconUrl : undefined,
            displayName: this.content ? this.content.displayName : undefined,
            path: contentPath,
            name: isNew ? undefined : contentName,
            isRoot: isRoot,
            isNew: isNew
        };
    },
    createSteps: function () {
        var dataStep = {
            stepTitle: this.contentType ? this.contentType.displayName : "Data",
            xtype: 'contentDataPanel',
            contentType: this.contentType,
            content: this.content
        };
        var metaStep = {
            stepTitle: 'Meta',
            xtype: 'panel'
        };
        var pageStep = {
            stepTitle: 'Page',
            xtype: 'panel'
        };
        var securityStep = {
            stepTitle: 'Security',
            xtype: 'panel'
        };
        var summaryStep = {
            stepTitle: 'Summary',
            xtype: 'panel'
        };
        return [
            dataStep,
            metaStep,
            pageStep,
            securityStep,
            summaryStep
        ];
    },
    isNewContent: function () {
        return !this.data || !this.content || Ext.isEmpty(this.content.path);
    },
    washDirtyForms: function () {
        for (var i = this.dirtyItems.length - 1; i >= 0; i--) {
            this.washDirtyForm(this.dirtyItems[i]);
        }
        this.dirtyItems = [];
        this.isWizardDirty = false;
    },
    washDirtyForm: function (dirtyForm) {
        if (dirtyForm.isDirty()) {
            dirtyForm.getFields().each(function (me) {
                me.originalValue = me.getValue();
                me.checkDirty();
            });
        }
    },
    createWizardHeader: function () {
        var headerData = this.prepareHeaderData(this.data);
        var evaluateFn = this.data && this.contentType && this.contentType.contentDisplayNameScript;
        var wizardHeader = Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            nameConfig: {
                readOnly: headerData.isRoot,
                stripCharsRe: /[^a-z0-9\-_]+/ig,
                vtype: 'path'
            },
            displayNameConfig: {
                emptyText: headerData.isNew ? 'New Content' : 'Display Name',
                autoFocus: headerData.isNew && Ext.isEmpty(evaluateFn)
            },
            data: this.data,
            content: this.content,
            prepareHeaderData: this.prepareHeaderData
        });
        this.validateItems.push(wizardHeader);
        return wizardHeader;
    },
    createActionButton: function () {
        return {
            xtype: 'button',
            text: 'Publish',
            action: 'publishContent'
        };
    },
    createIcon: function () {
        var me = this;
        var headerData = this.prepareHeaderData(this.data);
        return {
            xtype: 'image',
            width: 110,
            height: 110,
            src: headerData.imageUrl,
            listeners: {
                render: function (cmp) {
                    var contentType = (me.data && me.contentType) ? me.contentType : undefined;
                    if (contentType) {
                        var toolText = '<strong>' + contentType.displayName + '</strong></br>' + contentType.module + ':' +
                                       contentType.name;
                        var tip = Ext.create('Ext.tip.ToolTip', {
                            target: cmp.el,
                            html: toolText,
                            padding: 10,
                            styleHtmlContent: true,
                            dismissDelay: 10000
                        });
                    }
                }
            }
        };
    },
    getWizardHeader: function () {
        return this.down('wizardHeader');
    },
    setLiveMode: function (mode) {
        this.getLayout().setActiveItem(mode ? 1 : 0);
        if (mode) {
            var livePreview = this.down('#livePreview');
            livePreview.load('/dev/live-edit-page/bootstrap.jsp', true);
        }
    },
    getData: function () {
        var data = {
            contentData: this.callParent()
        };
        Ext.apply(data, this.getWizardHeader().getData());
        return data;
    }
});
Ext.define('Admin.view.contentManager.wizard.ContentLiveEditPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentLiveEditPanel',
    requires: [
        'Admin.view.contentManager.wizard.ContentWizardToolbar',
        'Admin.view.contentManager.wizard.ContentWizardPanel'
    ],
    layout: {
        type: 'card'
    },
    header: false,
    border: 0,
    autoScroll: false,
    isLiveMode: false,
    defaults: {
        border: false
    },
    listeners: {
        afterrender: function () {
            this.setLiveMode(this.isLiveMode);
        }
    },
    initComponent: function () {
        this.tbar = Ext.createByAlias('widget.contentWizardToolbar', {
            isLiveMode: this.isLiveMode
        });
        var wizardPanel = {
            xtype: 'contentWizardPanel',
            content: this.content,
            contentType: this.contentType,
            contentParent: this.contentParent,
            data: this.data
        };
        var liveEdit = {
            flex: 1,
            itemId: 'livePreview',
            xtype: 'contentLive',
            border: false,
            hidden: true
        };
        this.items = [
            wizardPanel,
            liveEdit
        ];
        this.callParent(arguments);
    },
    setLiveMode: function (mode) {
        this.getLayout().setActiveItem(mode ? 1 : 0);
        if (mode) {
            var livePreviewPanel = this.down('#livePreview');
            if (!livePreviewPanel.iFrameLoaded) {
                livePreviewPanel.load(this.getLiveUrl(this.data), true);
            }
        }
    },
    getLiveUrl: function (data) {
        var str = '';
        if (this.content) {
            if (this.content.displayName) {
                str = this.content.displayName;
            } else if (this.content.path) {
                str = this.content.path;
            }
        }
        return str.match(/frogger/gi) !== null ? '/dev/live-edit-page/frogger.jsp' : '/dev/live-edit-page/bootstrap.jsp';
    },
    toggleLive: function () {
        this.isLiveMode = !this.isLiveMode;
        this.setLiveMode(this.isLiveMode);
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
                    api_remote.RemoteService.contentType_get(getContentTypeCommand, function (rpcResponse) {
                        getContentTypeResponse = rpcResponse;
                        if (getContentTypeResponse && getContentTypeResponse.success && getContentResponse && getContentResponse.success) {
                            getContentTypeResponse.content = getContentResponse.content;
                            handleRpcResponse(getContentTypeResponse);
                        }
                    });
                    var getContentCommand = {
                        path: selectedContent.get('path')
                    };
                    api_remote.RemoteService.content_get(getContentCommand, function (rpcResponse) {
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
                api_remote.RemoteService.contentType_get({
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
        if (toolbar) {
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
            deleteContentButton = detailPanel.down('*[action=deleteContent]');
            if (deleteContentButton) {
                deleteContentButton.setDisabled(disabled);
            }
        }
    },
    loadContentAndFacets: function (values) {
        var me = this, filter = this.getContentFilter(), params = this.createLoadContentParams(values || filter.getValues());
        api_remote.RemoteService.content_find(params, function (response) {
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
    getContentBrowseToolbar: function () {
        return Ext.ComponentQuery.query('browseToolbar')[0];
    },
    getContentManagerContextMenu: function () {
        var menu = components.contextMenu;
        if (!menu) {
            menu = components.contextMenu = new app_ui.ContextMenu();
        }
        return menu;
    },
    getContentTreeGridPanel: function () {
        return components.gridPanel;
    },
    getContentDetailPanel: function () {
        var contentDetail = Ext.ComponentQuery.query('contentDetail');
        var vertical = contentDetail[0].isVisible();
        return Ext.ComponentQuery.query('contentDetail')[vertical ? 0 : 1];
    },
    getPersistentGridSelectionPlugin: function () {
        return this.getContentGridPanel().getPlugin('persistentGridSelection');
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
            '#contentTreeGrid treepanel, #contentTreeGrid grid': {
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
        this.getContentManagerContextMenu().showAt(e.getX(), e.getY());
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
        api_remote.RemoteService.content_createOrUpdate(contentParams, function (r) {
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
        api_remote.RemoteService.content_delete({
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
        var previewUrl = '/dev/live-edit-page/frogger.jsp';
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
    }
});
var components;
(function (components) {
    components.browseToolbar;
    components.contextMenu;
    components.gridPanel;
})(components || (components = {}));
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
        var contentIsOpenedFromPortal = document.location.href.indexOf('/open') > -1;
        var filter = new Admin.view.contentManager.FilterPanel({
            region: 'west',
            xtype: 'contentFilter',
            width: 200
        });
        var toolbar = components.browseToolbar = new app_ui.BrowseToolbar();
        var grid = components.gridPanel = new app_browse.ContentTreeGridPanel('contentTreeGrid').create('center');
        var detailsHorizontal = new Admin.view.contentManager.DetailPanel({
            region: 'south',
            split: true,
            collapsible: true,
            header: false,
            xtype: 'contentDetail',
            isLiveMode: contentIsOpenedFromPortal,
            showToolbar: false,
            flex: 1
        });
        var detailsVertical = new Admin.view.contentManager.DetailPanel({
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
        });
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
                                filter,
                                {
                                    region: 'center',
                                    xtype: 'container',
                                    layout: 'border',
                                    border: false,
                                    items: [
                                        toolbar.ext,
                                        grid.ext,
                                        detailsHorizontal,
                                        detailsVertical
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        });
        var deleteContentDialog = new app_ui.DeleteContentDialog();
        app_event.DeleteContentEvent.on(function (event) {
            deleteContentDialog.setContentToDelete(event.getModels());
            deleteContentDialog.open();
        });
    }
});
app.ContentContext.init();
app.ContentActions.init();
//@ sourceMappingURL=all.js.map
