module app.browse.action {
    export class ImportTemplateAction extends api.ui.Action {

        constructor() {
            super("Import");
            this.onExecuted(() => {
                new app.browse.event.ImportTemplateEvent().fire();
            });
        }
    }
}