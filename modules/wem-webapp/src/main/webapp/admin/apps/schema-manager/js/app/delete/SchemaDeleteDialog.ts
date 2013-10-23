module app_delete {

    export class SchemaDeleteDialog extends api_app_delete.DeleteDialog {

        private schemaToDelete:api_model.SchemaExtModel[];

        constructor() {
            super("Schema");

            this.setDeleteAction(new SchemaDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {
                var deleteRequest = new api_schema.DeleteSchemaRequest();

                for (var i = 0; i < this.schemaToDelete.length; i++) {
                    deleteRequest.addQualifiedName(this.schemaToDelete[i].data.qualifiedName);
                }

                var type = this.schemaToDelete.length > 0 ? this.schemaToDelete[0].data.type : undefined;
                deleteRequest.setType(type);

                deleteRequest.send().done((jsonResponse:api_rest.JsonResponse) => {
                    var json = jsonResponse.getJson();

                    var names = [];
                    for ( var i = 0; i < json.successes.length; i++ ) {
                        names.push(json.successes[i].qualifiedName);
                    }

                    this.close();
                    //components.gridPanel.refresh();

                    api_notify.showFeedback('Schema [' + names.join(', ') + '] deleted!')
                });
            });
        }

        setSchemaToDelete(schemaModels:api_model.SchemaExtModel[]):SchemaDeleteDialog {
            this.schemaToDelete = schemaModels;

            var deleteItems:api_app_delete.DeleteItem[] = [];
            for (var i in schemaModels) {
                var schemaModel = schemaModels[i];

                var deleteItem = new api_app_delete.DeleteItem(schemaModel.data.iconUrl, schemaModel.data.displayName);
                deleteItems.push(deleteItem);
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