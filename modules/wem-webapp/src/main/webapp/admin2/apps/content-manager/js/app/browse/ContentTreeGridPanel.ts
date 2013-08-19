module app_browse {

    export interface ContentTreeGridPanelParams {

        contextMenu:api_ui_menu.ContextMenu;
    }

    export class ContentTreeGridPanel extends api_app_browse_grid.TreeGridPanel {

        constructor(params:ContentTreeGridPanelParams) {

            super({
                columns: this.createColumns(),
                gridStore: new app_browse_grid.ContentGridStore().getExtDataStore(),
                treeStore: new app_browse_grid.ContentTreeStore().getExtDataStore(),
                gridConfig: this.createGridConfig(),
                treeConfig: this.createTreeConfig(),
                contextMenu: params.contextMenu});

            this.setActiveList(api_app_browse_grid.TreeGridPanel.TREE);
            this.setKeyField("path");
            this.setItemId("ContentTreeGridPanel");

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
            });


            this.addListener({
                onItemDoubleClicked: (event:api_app_browse_grid.TreeItemDoubleClickedEvent) => {
                new app_browse.EditContentEvent([<any>event.clickedModel]).fire();
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