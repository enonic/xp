module app.browse.action {
    export class OpenSchemaAction extends BaseSchemaBrowseAction {

        private schemaTreeGrid: app.browse.SchemaTreeGrid;

        constructor() {
            super("Open");
            this.schemaTreeGrid = null;
            this.setEnabled(false);
            this.onExecuted(() => {
                if (this.schemaTreeGrid) {
                    new OpenSchemaEvent(this.schemaTreeGrid.getSelectedDataList()).fire();
                }
            });
        }

        setSchemaTreeGrid(schemaTreeGrid: app.browse.SchemaTreeGrid) {
            this.schemaTreeGrid = schemaTreeGrid;
        }
    }
}