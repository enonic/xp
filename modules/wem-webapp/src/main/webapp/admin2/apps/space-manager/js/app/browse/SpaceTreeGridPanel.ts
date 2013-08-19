module  app_browse {

    export interface SpaceTreeGridPanelParams {

        contextMenu:api_ui_menu.ContextMenu;
    }

    export class SpaceTreeGridPanel extends api_app_browse_grid.TreeGridPanel {


        constructor(params?:SpaceTreeGridPanelParams) {

            super({
                columns: this.createColumns(),
                gridStore: new app_browse_grid.SpaceGridStore().getExtDataStore(),
                treeStore: new app_browse_grid.SpaceTreeStore().getExtDataStore(),
                gridConfig: this.createGridConfig(),
                treeConfig: this.createTreeConfig(),
                contextMenu: params.contextMenu});

            this.setItemId("SpaceTreeGridPanel");

            app_wizard.SpaceCreatedEvent.on((event) => {
                this.setRefreshNeeded(true);
            });

            app_wizard.SpaceUpdatedEvent.on((event) => {
                this.setRefreshNeeded(true);
            });

            this.addListener({
                onItemDoubleClicked: (event:api_app_browse_grid.TreeItemDoubleClickedEvent) => {
                new app_browse.EditSpaceEvent([<any>event.clickedModel]).fire();
            }});
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
                selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36})
            }
        }

        private createTreeConfig() {
            return {
                selModel: Ext.create('Ext.selection.CheckboxModel', {headerWidth: 36})
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
