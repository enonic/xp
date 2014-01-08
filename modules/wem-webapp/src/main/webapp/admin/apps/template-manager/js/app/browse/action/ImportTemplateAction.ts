module app.browse.action {
    export class ImportTemplateAction extends api.ui.Action {

        constructor() {
            super("Import");
            this.addExecutionListener(() => {
                new app.browse.event.ImportTemplateEvent().fire();
            });
        }
    }
}