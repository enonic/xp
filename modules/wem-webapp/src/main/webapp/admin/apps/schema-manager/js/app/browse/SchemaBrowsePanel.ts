module app_browse {

    export class SchemaBrowsePanel extends api_app_browse.BrowsePanel {

        private browseActions:app_browse.SchemaBrowseActions;

        private toolbar:SchemaBrowseToolbar;

        constructor() {
            var treeGridContextMenu = new app_browse.SchemaTreeGridContextMenu();
            var treeGridPanel = components.gridPanel = new SchemaTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = SchemaBrowseActions.init(treeGridPanel);
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new SchemaBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new SchemaBrowseItemPanel({actionMenuActions: [
                this.browseActions.NEW_SCHEMA,
                this.browseActions.EDIT_SCHEMA,
                this.browseActions.OPEN_SCHEMA,
                this.browseActions.DELETE_SCHEMA,
                this.browseActions.REINDEX_SCHEMA,
                this.browseActions.EXPORT_SCHEMA]});

            var filterPanel = new app_browse_filter.SchemaBrowseFilterPanel();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: treeGridPanel,
                browseItemPanel: browseItemPanel,
                filterPanel: filterPanel
            });

            api_schema.SchemaDeletedEvent.on((event) => {
                var names:string[] = event.getSchemaNames();
                console.log('On schemas deleted', event.getSchemaType(), names);
                for (var i = 0; i < names.length; i++) {
                    treeGridPanel.remove(names[i]);
                }
            });

            api_schema.SchemaCreatedEvent.on((event) => {
                console.log('On schema created', event.getSchemaType(), event.getSchemaName());
                this.setRefreshNeeded(true);
            });

            api_schema.SchemaUpdatedEvent.on((event) => {
                console.log('On schema updated', event.getSchemaType(), event.getSchemaName());
                this.setRefreshNeeded(true);
            });

            treeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api_app_browse_grid.TreeGridSelectionChangedEvent) => {
                    this.browseActions.updateActionsEnabledState(<any[]>event.selectedModels);
                }
            });
        }

        extModelsToBrowseItems(models:Ext_data_Model[]) {

            var browseItems:api_app_browse.BrowseItem[] = [];

            models.forEach((model:Ext_data_Model, index:number) => {
                var item = new api_app_browse.BrowseItem(model).
                    setDisplayName(model.data['displayName']).
                    setPath(model.data['name']).
                    setIconUrl(model.data['iconUrl']);
                browseItems.push(item);
            });
            return browseItems;
        }
    }

}