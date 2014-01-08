module app.browse.action {

    export class NewTemplateAction extends api.ui.Action {

        constructor() {
            super("New");
            this.addExecutionListener(() => {
                new app.browse.event.NewTemplateEvent().fire();
            });
        }

    }
}