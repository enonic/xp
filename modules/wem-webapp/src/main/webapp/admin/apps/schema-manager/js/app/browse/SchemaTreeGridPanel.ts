module app.browse {

    export interface SchemaTreeGridPanelParams {

        contextMenu:api.ui.menu.ContextMenu;
    }

    export class SchemaTreeGridPanel extends api.app.browse.grid.TreeGridPanel {

        constructor(params:SchemaTreeGridPanelParams) {

            super({
                columns: this.createColumns(),
                gridStore: new app.browse.grid.SchemaGridStore().getExtDataStore(),
                treeStore: new app.browse.grid.SchemaTreeStore().getExtDataStore(),
                gridConfig: this.createGridConfig(),
                treeConfig: this.createTreeConfig(),
                contextMenu: params.contextMenu});

            this.setItemId("SchemaTreeGridPanel");

            this.setActiveList(api.app.browse.grid.TreeGridPanel.TREE);
            this.setKeyField("key");

            this.addListener({
                onItemDoubleClicked: (event:api.app.browse.grid.TreeItemDoubleClickedEvent) => {
                    new app.browse.EditSchemaEvent([<any>event.clickedModel]).fire();
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

            var schema = record.data;
            var activeListType = this.getActiveList().getItemId();
            return Ext.String.format( nameTemplate, activeListType, schema.iconUrl, value, schema.name, schema.schemaKind);
        }

        private prettyDateRenderer(value) {
            try {
                if (parent && Ext.isFunction(parent['humane.date'])) {
                    return parent['humane.date'](value);
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
