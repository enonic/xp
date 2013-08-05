module app_browse {

    export class NewSchemaAction extends api_ui.Action {

        constructor() {
            super("New");
            this.addExecutionListener(() => {
                new ShowNewSchemaDialogEvent().fire();
            });
        }
    }

    export class EditSchemaAction extends api_ui.Action {

        constructor() {
            super("Edit");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new EditSchemaEvent(app.SchemaContext.get().getSelectedSchemes()).fire();
            });
        }
    }

    export class OpenSchemaAction extends api_ui.Action {

        constructor() {
            super("Open");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new OpenSchemaEvent(app.SchemaContext.get().getSelectedSchemes()).fire();
            });
        }
    }

    export class DeleteSchemaAction extends api_ui.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new DeleteSchemaPromptEvent(app.SchemaContext.get().getSelectedSchemes()).fire();
            });
        }
    }

    export class ReindexSchemaAction extends api_ui.Action {

        constructor() {
            super("Re-index");
            this.addExecutionListener(() => {
                new ReindexSchemaEvent(app.SchemaContext.get().getSelectedSchemes()).fire();
            });
        }
    }

    export class ExportSchemaAction extends api_ui.Action {

        constructor() {
            super("Export");
            this.addExecutionListener(() => {
                new ExportSchemaEvent(app.SchemaContext.get().getSelectedSchemes()).fire();
            });
        }
    }

    export class SchemaBrowseActions {

        static NEW_SCHEMA:api_ui.Action = new NewSchemaAction();
        static EDIT_SCHEMA:api_ui.Action = new EditSchemaAction();
        static OPEN_SCHEMA:api_ui.Action = new OpenSchemaAction();
        static DELETE_SCHEMA:api_ui.Action = new DeleteSchemaAction();
        static REINDEX_SCHEMA:api_ui.Action = new ReindexSchemaAction();
        static EXPORT_SCHEMA:api_ui.Action = new ExportSchemaAction();

        static ACTIONS:api_ui.Action[] = [];

        static init() {
            ACTIONS.push(NEW_SCHEMA, EDIT_SCHEMA, OPEN_SCHEMA, DELETE_SCHEMA, REINDEX_SCHEMA, EXPORT_SCHEMA);

            GridSelectionChangeEvent.on((event) => {
                var spaces:api_model.SchemaExtModel[] = event.getModels();

                if (spaces.length <= 0) {
                    NEW_SCHEMA.setEnabled(true);
                    EDIT_SCHEMA.setEnabled(false);
                    OPEN_SCHEMA.setEnabled(false);
                    DELETE_SCHEMA.setEnabled(false);
                    REINDEX_SCHEMA.setEnabled(true);
                    EXPORT_SCHEMA.setEnabled(true);
                } else {
                    NEW_SCHEMA.setEnabled(true);
                    EDIT_SCHEMA.setEnabled(true);
                    OPEN_SCHEMA.setEnabled(true);
                    DELETE_SCHEMA.setEnabled(true);
                    REINDEX_SCHEMA.setEnabled(true);
                    EXPORT_SCHEMA.setEnabled(true);
                }
            });
        }

    }
}