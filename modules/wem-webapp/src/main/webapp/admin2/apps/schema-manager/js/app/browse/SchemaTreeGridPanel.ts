module app_browse {

    export class SchemaTreeGridPanel extends api_ui_grid.TreeGridPanel {

        constructor(itemId?:string) {
            super(
                this.createColumns(),
                this.createGridStore(),
                this.createTreeStore(),
                this.createGridConfig(),
                this.createTreeConfig()
            );

            this.setActiveList(api_ui_grid.TreeGridPanel.TREE);
            this.setKeyField("key");
            this.setItemId(itemId);

            GridDeselectEvent.on((event) => {
                this.deselect(event.getModels()[0].data.name);
            });


            app_browse_filter.SchemaBrowseSearchEvent.on((event) => {
                this.setActiveList('grid');
                if (event.getFilterParams()) {
                    // show  ids
                    this.setRemoteSearchParams(event.getFilterParams());
                    this.refresh();
                } else {
                    // show none
                    this.removeAll();
                    this.updateResultCount(0);
                }
            });

            app_browse_filter.SchemaBrowseResetEvent.on((event) => {
                this.setActiveList('tree');
                this.setRemoteSearchParams({});
                this.refresh();
            })
        }

        private createColumns() {
            return <any[]> [
                {
                    header: 'Name',
                    dataIndex: 'displayName',
                    flex: 1,
                    renderer: this.nameRenderer,
                    scope: this
                },
                {
                    header: 'Module',
                    dataIndex: 'module'
                },
                {
                    header: 'Type',
                    dataIndex: 'type'
                },
                {
                    header: 'Modified',
                    dataIndex: 'modifiedTime',
                    renderer: this.prettyDateRenderer
                }
            ];
        }

        private createGridStore() {
            return new Ext.data.Store({
                model: 'Admin.model.schemaManager.SchemaModel',

                pageSize: 50,
                remoteSort: true,
                sorters: [
                    {
                        property: 'modifiedTime',
                        direction: 'DESC'
                    }
                ],
                autoLoad: false,

                proxy: {
                    type: 'direct',
                    directFn: api_remote_schema.RemoteSchemaService.schema_list,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'schemas',
                        totalProperty: 'total'
                    }
                }
            });
        }

        private createTreeStore() {
            return new Ext.data.TreeStore({

                model: 'Admin.model.schemaManager.SchemaModel',

                folderSort: true,

                proxy: {
                    type: 'direct',
                    directFn: api_remote_schema.RemoteSchemaService.schema_tree,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'schemas',
                        totalProperty: 'total'
                    }
                }

            });
        }

        private createGridConfig() {
            return {
                listeners: {
                    selectionchange: (selModel, selected, opts) => {
                        new GridSelectionChangeEvent(selected).fire();
                    },
                    itemcontextmenu: (view, rec, node, index, event) => {
                        event.stopEvent();
                        new ShowContextMenuEvent(event.xy[0], event.xy[1]).fire();
                    },
                    itemdblclick: (grid, record) => {
                        new EditSchemaEvent(grid.getSelection()).fire();
                    }
                }
            }
        }

        private createTreeConfig() {
            return {
                listeners: {
                    selectionchange: (selModel, selected, opts) => {
                        new GridSelectionChangeEvent(selected).fire();
                    },
                    itemcontextmenu: (view, rec, node, index, event) => {
                        event.stopEvent();
                        new ShowContextMenuEvent(event.xy[0], event.xy[1]).fire();
                    },
                    itemdblclick: (grid, record) => {
                        new EditSchemaEvent(grid.getSelection()).fire();
                    }
                }
            }
        }

        private nameRenderer(value, metaData, record) {
            // typescript swears when extracting this as the class field
            var nameTemplate = '<div class="admin-{0}-thumbnail">' +
                               '<img src="{1}"/>' +
                               '</div>' +
                               '<div class="admin-{0}-description">' +
                               '<h6>{2}</h6>' +
                               '<p>{3}</p>' +
                               '</div>';

            var content = record.data;
            var activeListType = this.getActiveList().getItemId();
            return Ext.String.format(nameTemplate, activeListType, content.iconUrl, value, content.name);
        }

        private prettyDateRenderer(value) {
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
    }
}