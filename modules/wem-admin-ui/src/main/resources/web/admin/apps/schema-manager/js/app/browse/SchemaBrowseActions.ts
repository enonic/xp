module app.browse {

    export class BaseSchemaBrowseAction extends api.ui.Action {

        constructor(label: string, shortcut?: string) {
            super(label, shortcut);
        }

        extModelsToSchemas(models: Ext_data_Model[]) {
            var schemas: api.schema.Schema[] = [];
            models.forEach((model: Ext_data_Model) => {
                schemas.push(api.schema.Schema.fromExtModel(model));
            });
            return schemas;
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

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Edit");
            this.setEnabled(false);
            this.onExecuted(() => {
                new EditSchemaEvent(this.extModelsToSchemas(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class OpenSchemaAction extends BaseSchemaBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Open");
            this.setEnabled(false);
            this.onExecuted(() => {
                new OpenSchemaEvent(this.extModelsToSchemas(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class DeleteSchemaAction extends BaseSchemaBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.onExecuted(() => {
                new DeleteSchemaPromptEvent(this.extModelsToSchemas(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class ReindexSchemaAction extends BaseSchemaBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Re-index");
            this.onExecuted(() => {
                new ReindexSchemaEvent(this.extModelsToSchemas(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class ExportSchemaAction extends BaseSchemaBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Export");
            this.onExecuted(() => {
                new ExportSchemaEvent(this.extModelsToSchemas(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class SchemaBrowseActions {

        public NEW_SCHEMA: api.ui.Action;
        public EDIT_SCHEMA: api.ui.Action;
        public OPEN_SCHEMA: api.ui.Action;
        public DELETE_SCHEMA: api.ui.Action;
        public REINDEX_SCHEMA: api.ui.Action;
        public EXPORT_SCHEMA: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        private static INSTANCE: SchemaBrowseActions;

        static init(treeGridPanel: api.app.browse.grid.TreeGridPanel): SchemaBrowseActions {
            new SchemaBrowseActions(treeGridPanel);
            return SchemaBrowseActions.INSTANCE;
        }

        static get(): SchemaBrowseActions {
            return SchemaBrowseActions.INSTANCE;
        }

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {

            this.NEW_SCHEMA = new NewSchemaAction();
            this.EDIT_SCHEMA = new EditSchemaAction(treeGridPanel);
            this.OPEN_SCHEMA = new OpenSchemaAction(treeGridPanel);
            this.DELETE_SCHEMA = new DeleteSchemaAction(treeGridPanel);
            this.REINDEX_SCHEMA = new ReindexSchemaAction(treeGridPanel);
            this.EXPORT_SCHEMA = new ExportSchemaAction(treeGridPanel);

            this.allActions.push(this.NEW_SCHEMA, this.EDIT_SCHEMA, this.OPEN_SCHEMA, this.DELETE_SCHEMA, this.REINDEX_SCHEMA,
                this.EXPORT_SCHEMA);

            SchemaBrowseActions.INSTANCE = this;
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

    }
}