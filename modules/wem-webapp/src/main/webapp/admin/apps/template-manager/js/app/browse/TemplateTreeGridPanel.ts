module app.browse {

    export interface TemplateTreeGridParams {

        contextMenu: api.ui.menu.ContextMenu;

    }

    export class TemplateTreeGridPanel extends api.app.browse.grid.TreeGridPanel {

        private pageTemplateIconUri: string;

        private siteTemplateIconUri: string;

        constructor(params: TemplateTreeGridParams) {
            super({
                columns: this.createColumns(),
                gridStore: new app.browse.grid.TemplateGridStore().getExtDataStore(),
                treeStore: new app.browse.grid.TemplateTreeStore().getExtDataStore(),
                gridConfig: this.createGridConfig(),
                treeConfig: this.createTreeConfig(),
                contextMenu: params.contextMenu
            });

            this.setItemId("TemplateTreeGridPanel");

            this.setActiveList(api.app.browse.grid.TreeGridPanel.TREE);
            this.setKeyField("key");

            this.pageTemplateIconUri = api.util.getAdminUri('common/images/icons/icoMoon/32x32/newspaper.png');
            this.siteTemplateIconUri = api.util.getAdminUri('common/images/icons/icoMoon/32x32/earth.png');
        }

        private createColumns() {
            return <any[]> [
                {
                    header: 'Display Name',
                    dataIndex: 'displayName',
                    flex: 0.5,
                    renderer: this.nameRenderer,
                    scope: this
                },
                {
                    header: 'Version',
                    dataIndex: 'version'
                },
                {
                    header: 'Vendor Name',
                    dataIndex: 'vendorName'
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

            var type: TemplateType = TemplateType[<string>record.get('templateType')];
            var iconUrl = type === TemplateType.PAGE ? this.pageTemplateIconUri : this.siteTemplateIconUri;
            return Ext.String.format(nameTemplate, activeListType, iconUrl, value, template.name);
        }
    }


}