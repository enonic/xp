module app.browse.action {
    export class NewSchemaAction extends api.ui.Action {

        constructor() {
            super("New");
            this.onExecuted(() => {
                new ShowNewSchemaDialogEvent().fire();
            });
        }
    }
}