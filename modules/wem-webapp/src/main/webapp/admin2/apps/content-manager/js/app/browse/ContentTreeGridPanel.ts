module app_browse {

    export class ContentTreeGridPanel extends api_ui_grid.TreeGridPanel {

        constructor(itemId?:string) {
            super(this.createColumns(), this.createGridStore(), this.createTreeStore(), this.createGridConfig(), this.createTreeConfig());

            this.setActiveList(api_ui_grid.TreeGridPanel.TREE);
            this.setKeyField("path");
            this.setItemId(itemId);

        }

        private createGridStore() {
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
        }

        private createTreeStore() {
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
                        new app_event.GridSelectionChangeEvent(selected).fire();
                    },
                    itemcontextmenu: (view, rec, node, index, event) => {
                        event.stopEvent();
                        new app_event.ShowContextMenuEvent(event.xy[0], event.xy[1]).fire();
                    },
                    itemdblclick: (grid, record) => {
                        new app_event.EditContentEvent(grid.getSelection()).fire();
                    }
                }
            }
        }

        private createTreeConfig() {
            return {
                selectionchange: (selModel, selected, opts) => {
                    new app_event.GridSelectionChangeEvent(selected).fire();
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
            return Ext.String.format(nameTemplate, activeListType, content.iconUrl, value, content.name);
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