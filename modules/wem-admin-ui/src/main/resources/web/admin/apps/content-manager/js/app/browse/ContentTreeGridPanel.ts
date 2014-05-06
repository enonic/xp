module app.browse {

    export interface ContentTreeGridPanelParams {

        contextMenu:api.ui.menu.ContextMenu;
    }

    export class ContentTreeGridPanel extends api.app.browse.grid.TreeGridPanel {

        constructor(params: ContentTreeGridPanelParams) {

            super({
                columns: this.createColumns(),
                gridStore: new app.browse.grid.ContentGridStore().getExtDataStore(),
                treeStore: new app.browse.grid.ContentTreeStore().getExtDataStore(),
                gridConfig: this.createGridConfig(),
                treeConfig: this.createTreeConfig(),
                contextMenu: params.contextMenu});

            this.setActiveList(api.app.browse.grid.TreeGridPanel.TREE);
            this.setKeyField("path");
            this.setItemId("ContentTreeGridPanel");

            app.browse.filter.ContentBrowseSearchEvent.on((event) => {
                this.loadData(event.getJsonModels());
            });

            app.browse.filter.ContentBrowseResetEvent.on((event) => {
                this.removeAll();
                this.refresh();
            });

            this.onTreeGridItemDoubleClicked((event: api.app.browse.grid.TreeGridItemDoubleClickedEvent) => {
                new app.browse.EditContentEvent([<any>event.getClickedModel()]).fire();
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
                    sortable: true,
                    maxWidth: 190,
                    flex: 1
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
            var content = record.data;
            // typescript swears when extracting this as the class field
            var nameTemplate = '<div class="admin-{0}-thumbnail' + (content.isSite ? ' site' : '') + '">' +
                               '<img src="{1}"/>' +
                               '<span class="overlay"></span>' +
                               '</div>' +
                               '<div class="admin-{0}-description">' +
                               '<h6>{2}</h6>' +
                               '<p>{3}</p>' +
                               '</div>';

            var activeListType = this.getActiveList().getItemId();
            var iconUrl = content.iconUrl + '?crop=false';
            return Ext.String.format(nameTemplate, activeListType, iconUrl, value, content.path);
        }

        private statusRenderer() {
            return "Online";
        }

        private prettyDateRenderer(value) {
            try {
                if (parent && Ext.isFunction(parent['humane.date'])) {
                    return parent['humane.date'](value);
                } else {
                    return api.util.DateHelper.formUtcDate(value);
                }
            }
            catch (e) {
                return value;
            }
        }

    }

}