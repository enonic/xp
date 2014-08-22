module app.browse.action {
    export class DeleteSchemaAction extends BaseSchemaBrowseAction {

        private schemaTreeGrid: app.browse.SchemaTreeGrid;

        constructor() {
            super("Delete", "mod+del");
            this.schemaTreeGrid = null;
            this.setEnabled(false);
            this.onExecuted(() => {
                if (this.schemaTreeGrid) {
                    new DeleteSchemaPromptEvent(this.schemaTreeGrid.getSelectedDataList()).fire();
                }
            });
        }

        setSchemaTreeGrid(schemaTreeGrid: app.browse.SchemaTreeGrid) {
            this.schemaTreeGrid = schemaTreeGrid;
        }
    }
}