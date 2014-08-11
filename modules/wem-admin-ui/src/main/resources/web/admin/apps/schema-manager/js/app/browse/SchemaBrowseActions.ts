module app.browse {

    export class BaseSchemaBrowseAction extends api.ui.Action {

        constructor(label: string, shortcut?: string) {
            super(label, shortcut);
        }
    }

    export class NewSchemaAction extends api.ui.Action {

        constructor() {
            super("New");
            this.onExecuted(() => {
                new ShowNewSchemaDialogEvent().fire();
            });
        }
    }

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

    export class SchemaBrowseActions {

        public NEW_SCHEMA: api.ui.Action;
        public EDIT_SCHEMA: EditSchemaAction;
        public OPEN_SCHEMA: OpenSchemaAction;
        public DELETE_SCHEMA: DeleteSchemaAction;
        public REINDEX_SCHEMA: ReindexSchemaAction;
        public EXPORT_SCHEMA: ExportSchemaAction;

        private allActions: api.ui.Action[] = [];

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

        getAllActions(): api.ui.Action[] {
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