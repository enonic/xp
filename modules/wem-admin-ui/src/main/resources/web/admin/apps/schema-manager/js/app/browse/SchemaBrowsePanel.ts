module app.browse {

    import Schema = api.schema.Schema;
    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;

    export class SchemaBrowsePanel extends api.app.browse.BrowsePanel<api.schema.Schema> {

        private schemaTreeGrid: SchemaTreeGrid;

        private browseActions: app.browse.action.SchemaBrowseActions;

        private toolbar: SchemaBrowseToolbar;

        private schemaTreeGridPanelMask: api.ui.mask.LoadMask;

        constructor(browseActions: app.browse.action.SchemaBrowseActions, schemaTreeGrid: SchemaTreeGrid) {
            var treeGridContextMenu = new app.browse.SchemaTreeGridContextMenu();

            this.schemaTreeGrid = schemaTreeGrid;
            this.schemaTreeGridPanelMask = new api.ui.mask.LoadMask(schemaTreeGrid);
            schemaTreeGrid.onRendered((event: api.dom.ElementRenderedEvent) => {
                this.schemaTreeGridPanelMask.show();
            });
            schemaTreeGrid.onLoaded(() => {
                this.schemaTreeGridPanelMask.hide();
            });

            this.browseActions = browseActions;
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new SchemaBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new SchemaBrowseItemPanel();


            super({
                browseToolbar: this.toolbar,
                treeGridPanel2: schemaTreeGrid,
                browseItemPanel: browseItemPanel
            });

            api.schema.SchemaDeletedEvent.on((event) => {
                this.schemaTreeGrid.reload();
                // TODO remove deleted item instead of reloading the whole list
            });

            api.schema.SchemaCreatedEvent.on((event) => {
                console.log('On schema created', event.getSchema());
                this.setRefreshNeeded(true);
            });

            api.schema.SchemaUpdatedEvent.on((event) => {
                console.log('On schema updated', event.getSchema());
                this.setRefreshNeeded(true);
            });

            schemaTreeGrid.onRowSelectionChanged((selectedRows: TreeNode<Schema>[]) => {
                this.browseActions.updateActionsEnabledState(<Schema[]>selectedRows.map((elem) => {
                    return elem.getData();
                }));
            });
        }

        treeNodesToBrowseItems(nodes: TreeNode<Schema>[]): BrowseItem<Schema>[] {
            var browseItems: BrowseItem<Schema>[] = [];

            nodes.forEach((node: TreeNode<Schema>, index: number) => {
                for (var i = 0; i <= index; i++) {
                    if (nodes[i].getData().getId() === node.getData().getId()) {
                        break;
                    }
                }
                if (i === index) {
                    var schema = node.getData();
                    var item = new BrowseItem<Schema>(schema).
                        setId(schema.getId()).
                        setDisplayName(schema.getDisplayName()).
                        setPath(schema.getKey()).
                        setIconUrl(schema.getIconUrl());
                    browseItems.push(item);
                }
            });

            return browseItems;
        }

    }

}