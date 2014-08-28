module app.browse.action {
    export class ReindexSchemaAction extends BaseSchemaBrowseAction {

        private schemaTreeGrid: app.browse.SchemaTreeGrid;

        constructor() {
            super("Re-index");
            this.schemaTreeGrid = null;
            this.onExecuted(() => {
                if (this.schemaTreeGrid) {
                    new ReindexSchemaEvent(this.schemaTreeGrid.getSelectedDataList()).fire();
                }
            });
        }

        setSchemaTreeGrid(schemaTreeGrid: app.browse.SchemaTreeGrid) {
            this.schemaTreeGrid = schemaTreeGrid;
        }
    }
}