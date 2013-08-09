module app_browse {

    export class ContentTreeGridPanel extends api_ui_grid.TreeGridPanel {

        constructor(itemId?:string) {
            super(this.createColumns(), this.createGridStore(), this.createTreeStore(), this.createGridConfig(), this.createTreeConfig());

            this.setActiveList(api_ui_grid.TreeGridPanel.TREE);
            this.setKeyField("path");
            this.setItemId(itemId);

            app_browse_filter.ContentBrowseSearchEvent.on((event) => {
                if (event.getResultContentIds().length > 0) {
                    // show  ids
                    this.setRemoteSearchParams({ contentIds: event.getResultContentIds() });
                    this.refresh();
                } else {
                    // show none
                    this.removeAll();
                    this.updateResultCount(0);
                }
            });

            app_browse_filter.ContentBrowseResetEvent.on((event) => {
                this.setRemoteSearchParams({});
                this.refresh();
            })

            var contentContextMenu = new app_browse.ContentTreeGridContextMenu();
            app_browse.ShowContextMenuEvent.on((event) => {
                contentContextMenu.showAt(event.getX(), event.getY());
            });
        }

        private createGridStore() {
            return new Ext.data.Store({

                model: 'Admin.model.contentManager.ContentModel',

                proxy: {
                    type: 'direct',
                    directFn: api_remote_content.RemoteContentService.content_find,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'contents',
                        totalProperty: 'total'
                    }
                }
            });
        }

        private createTreeStore() {
            return new Ext.data.TreeStore({

                model: 'Admin.model.contentManager.ContentModel',

                folderSort: true,
                autoLoad: false,

                proxy: {
                    type: 'direct',
                    directFn: api_remote_content.RemoteContentService.content_tree,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'contents',
                        totalProperty: 'total'
                    }
                }

            });
        }

        private createColumns() {
            return <any[]> [
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
                    text: 'Modified',
                    dataIndex: 'modifiedTime',
                    renderer: this.prettyDateRenderer,
                    scope: this,
                    sortable: true
                }
            ];
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
                        new EditContentEvent(grid.getSelection()).fire();
                    }
                }
            }
        }

        private createTreeConfig() {
            return {
                selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36}),
                listeners: {
                    selectionchange: (selModel, selected, opts) => {
                        new GridSelectionChangeEvent(selected).fire();
                    },
                    itemcontextmenu: (view, rec, node, index, event) => {
                        event.stopEvent();
                        new ShowContextMenuEvent(event.xy[0], event.xy[1]).fire();
                    },
                    itemdblclick: (grid, record) => {
                        new EditContentEvent(grid.getSelection()).fire();
                    }
                }
            }
        }

        private nameRenderer(value, metaData, record, rowIndex, colIndex, store, view) {
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
            return Ext.String.format(nameTemplate, activeListType, content.iconUrl, value, content.path);
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

    }

}