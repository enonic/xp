module  app_browse {

    export class SpaceTreeGridPanel extends api_ui_grid.TreeGridPanel {

        constructor(itemId?:string) {
            super(this.createColumns(), this.createGridStore(), this.createTreeStore(), this.createGridConfig(), this.createTreeConfig());

            this.setItemId(itemId);

            GridDeselectEvent.on((event) => {
                this.deselect(event.getModels()[0].data.name);
            });

            app_wizard.SpaceCreatedEvent.on((event) => {
                this.setRefreshNeeded(true);
            });

            app_wizard.SpaceUpdatedEvent.on((event) => {
                this.setRefreshNeeded(true);
            });

            api_ui.DeckPanelShownPanelChangedEvent.on((event:api_ui.DeckPanelShownPanelChangedEvent) => {
                if (event.index == 0 && this.isRefreshNeeded()) {
                    this.refresh();
                }
            })
        }

        private createGridStore() {
            return new Ext.data.Store({
                pageSize: 100,
                autoLoad: true,
                model: 'Admin.model.SpaceModel',
                proxy: {
                    type: 'direct',
                    directFn: api_remote.RemoteSpaceService.space_list,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'spaces',
                        totalProperty: 'total'
                    }
                }
            });
        }

        private createTreeStore() {
            return new Ext.data.TreeStore();
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
                        new EditSpaceEvent(grid.getSelection()).fire();
                    }
                }
            }
        }

        private createTreeConfig() {
            return {
                selectionchange: (selModel, selected, opts) => {
                    new GridSelectionChangeEvent(selected).fire();
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

            var space = record.data;
            var activeListType = this.getActiveList().getItemId();
            return Ext.String.format(nameTemplate, activeListType, space.iconUrl, value, space.name);
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
