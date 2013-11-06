module app_browse {

    export class SchemaBrowsePanel extends api_app_browse.BrowsePanel<api_schema.Schema> {

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
                var schemas:api_schema.Schema[] = event.getSchemas();
                console.log('On schema deleted', event.getSchemas());
                for (var i = 0; i < schemas.length; i++) {
                    var schema:api_schema.Schema = schemas[i];
                    // make up schema key
                    treeGridPanel.remove(schema.getSchemaKind().toString() + ":" + schema.getName());
                }
            });

            api_schema.SchemaCreatedEvent.on((event) => {
                console.log('On schema created', event.getSchema());
                this.setRefreshNeeded(true);
            });

            api_schema.SchemaUpdatedEvent.on((event) => {
                console.log('On schema updated', event.getSchema());
                this.setRefreshNeeded(true);
            });

            treeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api_app_browse_grid.TreeGridSelectionChangedEvent) => {
                    this.browseActions.updateActionsEnabledState(<any[]>event.selectedModels);
                }
            });
        }

        extModelsToBrowseItems(models:Ext_data_Model[]):api_app_browse.BrowseItem<api_schema.Schema>[] {

            var browseItems:api_app_browse.BrowseItem<api_schema.Schema>[] = [];

            models.forEach((model:Ext_data_Model, index:number) => {

                var schema:api_schema.Schema = api_schema.Schema.fromExtModel(model);

                var item = new api_app_browse.BrowseItem<api_schema.Schema>(schema).
                    setDisplayName(model.data['displayName']).
                    setPath(model.data['name']).
                    setIconUrl(model.data['iconUrl']);

                browseItems.push(item);
            });
            return browseItems;
        }
    }

}