module app_browse {

    export class BaseSchemaBrowseAction extends api_ui.Action {

        constructor(label:string, shortcut?:string) {
            super(label, shortcut);
        }

        extModelsToSchemas(models:Ext_data_Model[]) {
            var schemas:api_schema.Schema[] = [];
            models.forEach((model:Ext_data_Model) => {
                schemas.push(api_schema.Schema.fromExtModel(model));
            });
            return schemas;
        }
    }

    export class NewSchemaAction extends api_ui.Action {

        constructor() {
            super("New");
            this.addExecutionListener(() => {
                new ShowNewSchemaDialogEvent().fire();
            });
        }
    }

    export class EditSchemaAction extends BaseSchemaBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Edit");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new EditSchemaEvent(this.extModelsToSchemas(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class OpenSchemaAction extends BaseSchemaBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Open");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new OpenSchemaEvent(this.extModelsToSchemas(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class DeleteSchemaAction extends BaseSchemaBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new DeleteSchemaPromptEvent(this.extModelsToSchemas(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class ReindexSchemaAction extends BaseSchemaBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Re-index");
            this.addExecutionListener(() => {
                new ReindexSchemaEvent(this.extModelsToSchemas(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class ExportSchemaAction extends BaseSchemaBrowseAction {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Export");
            this.addExecutionListener(() => {
                new ExportSchemaEvent(this.extModelsToSchemas(treeGridPanel.getSelection())).fire();
            });
        }
    }

    export class SchemaBrowseActions {

        public NEW_SCHEMA:api_ui.Action;
        public EDIT_SCHEMA:api_ui.Action;
        public OPEN_SCHEMA:api_ui.Action;
        public DELETE_SCHEMA:api_ui.Action;
        public REINDEX_SCHEMA:api_ui.Action;
        public EXPORT_SCHEMA:api_ui.Action;

        private allActions:api_ui.Action[] = [];

        private static INSTANCE:SchemaBrowseActions;

        static init(treeGridPanel:api_app_browse_grid.TreeGridPanel):SchemaBrowseActions {
            new SchemaBrowseActions(treeGridPanel);
            return SchemaBrowseActions.INSTANCE;
        }

        static get():SchemaBrowseActions {
            return SchemaBrowseActions.INSTANCE;
        }

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {

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

        getAllActions():api_ui.Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(schemas:api_schema.Schema[]) {

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