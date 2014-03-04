module app.browse {

    export class SchemaBrowsePanel extends api.app.browse.BrowsePanel<api.schema.Schema> {

        private browseActions: app.browse.SchemaBrowseActions;

        private toolbar: SchemaBrowseToolbar;

        constructor() {
            var treeGridContextMenu = new app.browse.SchemaTreeGridContextMenu();
            var treeGridPanel = components.gridPanel = new SchemaTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = SchemaBrowseActions.init(treeGridPanel);
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new SchemaBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new SchemaBrowseItemPanel();


            super({
                browseToolbar: this.toolbar,
                treeGridPanel: treeGridPanel,
                browseItemPanel: browseItemPanel
            });

            api.schema.SchemaDeletedEvent.on((event) => {
                var schemas: api.schema.Schema[] = event.getSchemas();
                console.log('On schema deleted', event.getSchemas());
                for (var i = 0; i < schemas.length; i++) {
                    var schema: api.schema.Schema = schemas[i];
                    // make up schema key
                    treeGridPanel.remove(schema.getSchemaKind().toString() + ":" + schema.getName());
                }
            });

            api.schema.SchemaCreatedEvent.on((event) => {
                console.log('On schema created', event.getSchema());
                this.setRefreshNeeded(true);
            });

            api.schema.SchemaUpdatedEvent.on((event) => {
                console.log('On schema updated', event.getSchema());
                this.setRefreshNeeded(true);
            });

            treeGridPanel.onTreeGridSelectionChanged((event: api.app.browse.grid.TreeGridSelectionChangedEvent) => {
                this.browseActions.updateActionsEnabledState(<any[]>event.getSelectedModels());
            });
        }

        extModelsToBrowseItems(models: Ext_data_Model[]): api.app.browse.BrowseItem<api.schema.Schema>[] {

            var browseItems: api.app.browse.BrowseItem<api.schema.Schema>[] = [];

            models.forEach((model: Ext_data_Model, index: number) => {

                var schema: api.schema.Schema = api.schema.Schema.fromExtModel(model);

                var item = new api.app.browse.BrowseItem<api.schema.Schema>(schema).
                    setDisplayName(model.data['displayName']).
                    setPath(model.data['name']).
                    setIconUrl(model.data['iconUrl']);

                browseItems.push(item);
            });
            return browseItems;
        }
    }

}