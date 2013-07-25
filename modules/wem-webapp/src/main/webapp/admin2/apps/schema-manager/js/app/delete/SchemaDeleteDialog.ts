module app_delete {

    export class SchemaDeleteDialog extends api_app_delete.DeleteDialog {

        private deleteAction:api_ui.Action = new SchemaDeleteDialogAction();

        private schemaToDelete:api_model.SchemaModel[];

        private deleteHandler:api_handler.DeleteSchemaHandler = new api_handler.DeleteSchemaHandler();

        constructor() {
            super("Schema");

            this.setDeleteAction(this.deleteAction);

            var deleteCallback = (obj, success, result) => {
                this.close();
                //components.gridPanel.refresh();
                api_notify.showFeedback('Schema was deleted!');
            };

            this.deleteAction.addExecutionListener(() => {
                this.deleteHandler.doDelete(api_handler.DeleteSchemaParamFactory.create(this.schemaToDelete), deleteCallback);
            });

            api_dom.Body.get().appendChild(this);
        }

        setSchemaToDelete(schemaModels:api_model.SchemaModel[]):SchemaDeleteDialog {
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