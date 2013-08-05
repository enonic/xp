module app_new {

    export class NewSchemaDialog extends api_ui_dialog.ModalDialog {

        private cancelAction:api_ui.Action = new CancelNewDialogAction();

        private schemaTypesList:SchemaTypesList;

        constructor() {
            super({
                title: "Select Kind",
                width: 400,
                height: 300
            });

            this.addClass("new-schema-dialog");

            this.schemaTypesList = new SchemaTypesList();
            this.appendChildToContentPanel(this.schemaTypesList);

            this.setCancelAction(this.cancelAction);
            this.cancelAction.addExecutionListener(()=> {
                this.close();
            });

            api_dom.Body.get().appendChild(this);

            NewSchemaEvent.on((event) => {
                    this.close();
                }
            );
        }

    }

    export class CancelNewDialogAction extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }

    }

}