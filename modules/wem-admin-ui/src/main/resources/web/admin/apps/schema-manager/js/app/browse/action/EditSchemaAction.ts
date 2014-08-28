module app.browse.action {
    export class EditSchemaAction extends BaseSchemaBrowseAction {

        private schemaTreeGrid: app.browse.SchemaTreeGrid;

        constructor() {
            super("Edit");
            this.schemaTreeGrid = null;
            this.setEnabled(false);
            this.onExecuted(() => {
                if (this.schemaTreeGrid) {
                    new EditSchemaEvent(this.schemaTreeGrid.getSelectedDataList()).fire();
                }
            });
        }

        setSchemaTreeGrid(schemaTreeGrid: app.browse.SchemaTreeGrid) {
            this.schemaTreeGrid = schemaTreeGrid;
        }
    }
}