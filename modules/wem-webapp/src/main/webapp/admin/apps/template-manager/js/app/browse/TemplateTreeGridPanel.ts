module app_browse {

    export interface TemplateTreeGridParams {

        contextMenu: api_ui_menu.ContextMenu;

    }

    export class TemplateTreeGridPanel extends api_app_browse_grid.TreeGridPanel {

        constructor(params:TemplateTreeGridParams) {
            super({
                columns: this.createColumns(),
                gridStore: new app_browse_grid.TemplateGridStore().getExtDataStore(),
                treeStore: new app_browse_grid.TemplateTreeStore().getExtDataStore(),
                gridConfig: this.createGridConfig(),
                treeConfig: this.createTreeConfig(),
                contextMenu: params.contextMenu
            });

            this.setItemId("TemplateTreeGridPanel");

            this.setActiveList(api_app_browse_grid.TreeGridPanel.GRID);
            this.setKeyField("id");
        }

        private createColumns() {
            return <any[]> [
                {
                    header: 'Display Name',
                    dataIndex: 'displayName',
                    sortable: true,
                    flex: 1,
                    renderer: this.nameRenderer,
                    scope: this
                }
            ];
        }

        private createGridConfig() {
            return {
                selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36})
            };
        }

        private createTreeConfig() {
            return {
                selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36})
            };
        }

        private nameRenderer(value, metaData, record, rowIndex, colIndex, store, view) {
            var nameTemplate = '<div class="admin-{0}-thumbnail">' +
                '<img src="{1}"/>' +
                '</div>' +
                '<div class="admin-{0}-description">' +
                '<h6>{2}</h6>' +
                '<p>{3}</p>' +
                '</div>';

            var template = record.data;
            var activeListType = this.getActiveList().getItemId();
            return Ext.String.format(nameTemplate, activeListType, template.iconUrl, value, template.path);
        }
    }



}