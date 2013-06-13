module app_ui {

    interface PersistentGridSelectionPlugin extends Ext_AbstractPlugin {

        getSelection():api_model.SpaceModel[];
        clearSelection():void;

    }

    export class TreeGridPanel {
        ext:Ext_panel_Panel;

        private store = new Ext.data.Store({
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
        private keyField = 'name';
        private nameTemplate = '<div class="admin-{0}-thumbnail">' +
                               '<img src="{1}"/>' +
                               '</div>' +
                               '<div class="admin-{0}-description">' +
                               '<h6>{2}</h6>' +
                               '<p>{3}</p>' +
                               '</div>';


        constructor(region?:String) {
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
                    selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36})
                },
                treeConf: {
                    selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36})
                }
            });

            var gp = new Ext.grid.Panel({
                itemId: 'grid',
                cls: 'admin-grid',
                border: false,
                hideHeaders: true,
                columns: <any[]>[
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
                        //dataIndex: 'type',
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
                selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36}),
                plugins: [gridSelectionPlugin],
                listeners: {
                    selectionchange: (selModel, selected, opts) => {
                        new app_event.GridSelectionChangeEvent(selected).fire();
                    },
                    itemcontextmenu: (view, rec, node, index, event) => {
                        event.stopEvent();
                        new app_event.ShowContextMenuEvent(event.xy[0], event.xy[1]).fire();
                    },
                    itemdblclick: (grid, record) => {
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
                plugins: ['gridToolbarPlugin']
            }));
            gp.getStore().on('datachanged', this.fireUpdateEvent, this);
            p.add(gp);

            app_event.GridDeselectEvent.on((event) => {
                this.deselect(event.getModels()[0].data.name);
            });
        }

        private fireUpdateEvent(values) {
            this.ext.fireEvent('datachanged', values);
        }

        private getActiveList():Ext_panel_Table {
            // returns either grid or tree that both extend table
            return <Ext_panel_Table> (<Ext_layout_container_Card> this.ext.getLayout()).getActiveItem();
        }

        private nameRenderer(value, metaData, record, rowIndex, colIndex, store, view) {
            var space = record.data;
            var activeListType = this.getActiveList().getItemId();
            return Ext.String.format(this.nameTemplate, activeListType, space.iconUrl, value, space.name);
        }

        private statusRenderer() {
            return "Online";
        }

        private prettyDateRenderer(value, metaData, record, rowIndex, colIndex, store, view) {
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
        }

        refresh() {
            this.store.loadPage(this.store.currentPage);
        }


        deselect(key) {
            var activeList = this.getActiveList(),
                selModel = activeList.getSelectionModel();

            if (!key || key === -1) {
                selModel.deselectAll();
            } else {
                var selNodes = selModel.getSelection();
                var i;

                for (i = 0; i < selNodes.length; i++) {
                    var selNode = selNodes[i];
                    if (key == selNode.get(this.keyField)) {
                        selModel.deselect([selNode]);
                    }
                }
            }
        }


        getSelection() {
            var selection = [],
                activeList = this.getActiveList(),
                plugin = <PersistentGridSelectionPlugin> activeList.getPlugin('persistentGridSelection');

            if (plugin) {
                selection = plugin.getSelection();
            } else {
                selection = activeList.getSelectionModel().getSelection();
            }

            return selection;
        }
    }
}
