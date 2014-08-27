module app.browse.action {

    export class ExportSchemaAction extends BaseSchemaBrowseAction {

        private schemaTreeGrid: app.browse.SchemaTreeGrid;

        constructor() {
            super("Export");
            this.schemaTreeGrid = null;
            this.onExecuted(() => {
                if (this.schemaTreeGrid) {
                    new ExportSchemaEvent(this.schemaTreeGrid.getSelectedDataList()).fire();
                }
            });
        }

        setSchemaTreeGrid(schemaTreeGrid: app.browse.SchemaTreeGrid) {
            this.schemaTreeGrid = schemaTreeGrid;
        }
    }
}