module app_browse {

    export interface SpaceTreeGridPanelParams {

        contextMenu:api_ui_menu.ContextMenu;
    }

    export class SchemaTreeGridPanel extends api_app_browse_grid.TreeGridPanel {

        constructor(params:SpaceTreeGridPanelParams) {

            super({
                columns: this.createColumns(),
                gridStore: new app_browse_grid.SchemaGridStore().getExtDataStore(),
                treeStore: new app_browse_grid.SchemaTreeStore().getExtDataStore(),
                gridConfig: this.createGridConfig(),
                treeConfig: this.createTreeConfig(),
                contextMenu: params.contextMenu});

            this.setItemId("SchemaTreeGridPanel");

            this.setActiveList(api_app_browse_grid.TreeGridPanel.TREE);
            this.setKeyField("key");

            app_browse_filter.SchemaBrowseSearchEvent.on((event) => {
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
                this.setRemoteSearchParams({});
                this.refresh();
            });

            this.addListener({
                onItemDoubleClicked: (event:api_app_browse_grid.TreeItemDoubleClickedEvent) => {
                    new app_browse.EditSchemaEvent([<any>event.clickedModel]).fire();
                },
                onSelectionChanged: null,
                onSelect: null,
                onDeselect: null
            });
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

        private createGridConfig() {
            return {
                selModel: {
                    allowDeselect: false,
                    ignoreRightMouseSelection: true
                }
            }
        }

        private createTreeConfig() {
            return {
                selModel: {
                    allowDeselect: false,
                    ignoreRightMouseSelection: true
                }
            }
        }

        private nameRenderer(value, metaData, record) {
            // typescript swears when extracting this as the class field
            var nameTemplate = '<div class="admin-{0}-thumbnail {4}">' +
                               '<img src="{1}"/>' +
                               '<span class="overlay"/>' +
                               '</div>' +
                               '<div class="admin-{0}-description">' +
                               '<h6>{2}</h6>' +
                               '<p>{3}</p>' +
                               '</div>';

            var content = record.data;
            var activeListType = this.getActiveList().getItemId();
            return Ext.String.format( nameTemplate, activeListType, content.iconUrl, value, content.name, content.type);
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
