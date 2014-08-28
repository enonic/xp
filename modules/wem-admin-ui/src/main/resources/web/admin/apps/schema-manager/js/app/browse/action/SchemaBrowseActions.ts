module app.browse.action {

    import Action = api.ui.Action;

    export class SchemaBrowseActions {

        public NEW_SCHEMA: Action;
        public EDIT_SCHEMA: EditSchemaAction;
        public OPEN_SCHEMA: OpenSchemaAction;
        public DELETE_SCHEMA: DeleteSchemaAction;
        public REINDEX_SCHEMA: ReindexSchemaAction;
        public EXPORT_SCHEMA: ExportSchemaAction;

        private allActions: Action[] = [];

        constructor() {

            this.NEW_SCHEMA = new NewSchemaAction();
            this.EDIT_SCHEMA = new EditSchemaAction();
            this.OPEN_SCHEMA = new OpenSchemaAction();
            this.DELETE_SCHEMA = new DeleteSchemaAction();
            this.REINDEX_SCHEMA = new ReindexSchemaAction();
            this.EXPORT_SCHEMA = new ExportSchemaAction();

            this.allActions.push(this.NEW_SCHEMA, this.EDIT_SCHEMA, this.OPEN_SCHEMA, this.DELETE_SCHEMA, this.REINDEX_SCHEMA,
                this.EXPORT_SCHEMA);
        }

        getAllActions(): Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(schemas: api.schema.Schema[]) {

            if (schemas.length <= 0) {
                this.NEW_SCHEMA.setEnabled(true);
                this.EDIT_SCHEMA.setEnabled(false);
                this.OPEN_SCHEMA.setEnabled(false);
                this.DELETE_SCHEMA.setEnabled(false);
                this.REINDEX_SCHEMA.setEnabled(false);
                this.EXPORT_SCHEMA.setEnabled(false);
            } else {
                this.NEW_SCHEMA.setEnabled(true);
                this.EDIT_SCHEMA.setEnabled(true);
                this.OPEN_SCHEMA.setEnabled(true);
                this.DELETE_SCHEMA.setEnabled(true);
                this.REINDEX_SCHEMA.setEnabled(true);
                this.EXPORT_SCHEMA.setEnabled(true);
            }
        }

        setSchemaTreeGrid(templateTreeGrid: app.browse.SchemaTreeGrid) {
            this.EDIT_SCHEMA.setSchemaTreeGrid(templateTreeGrid);
            this.OPEN_SCHEMA.setSchemaTreeGrid(templateTreeGrid);
            this.DELETE_SCHEMA.setSchemaTreeGrid(templateTreeGrid);
            this.REINDEX_SCHEMA.setSchemaTreeGrid(templateTreeGrid);
            this.EXPORT_SCHEMA.setSchemaTreeGrid(templateTreeGrid);
        }

    }
}