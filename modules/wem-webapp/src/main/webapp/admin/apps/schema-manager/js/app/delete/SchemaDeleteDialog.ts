module app_delete {

    export class SchemaDeleteDialog extends api_app_delete.DeleteDialog {

        private schemaToDelete:api_schema.Schema[];

        constructor() {
            super("Schema");

            this.setDeleteAction(new SchemaDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {

                var deleteRequest = new api_schema.DeleteSchemaRequest();
                for (var i = 0; i < this.schemaToDelete.length; i++) {
                    deleteRequest.addQualifiedName(this.schemaToDelete[i].getName());
                }

                var type:api_schema.SchemaKind = this.schemaToDelete.length > 0 ? this.schemaToDelete[0].getSchemaKind() : null;
                deleteRequest.setType(type);

                deleteRequest.send().done((jsonResponse:api_rest.JsonResponse) => {
                    var json = jsonResponse.getJson();

                    var names:string[] = [];
                    for (var i = 0; i < json.successes.length; i++) {
                        names.push(json.successes[i].name);
                    }

                    this.close();

                    api_notify.showFeedback('Schema [' + names.join(', ') + '] deleted!');

                    new api_schema.SchemaDeletedEvent(type, names).fire();
                });
            });
        }

        setSchemaToDelete(schemas:api_schema.Schema[]):SchemaDeleteDialog {
            this.schemaToDelete = schemas;

            var deleteItems:api_app_delete.DeleteItem[] = [];
            for (var i = 0; i < schemas.length; i++) {
                deleteItems.push(new api_app_delete.DeleteItem(schemas[i].getIcon(), schemas[i].getDisplayName()));
            }
            this.setDeleteItems(deleteItems);
            return this;
        }
    }

    export class SchemaDeleteDialogAction extends api_ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}