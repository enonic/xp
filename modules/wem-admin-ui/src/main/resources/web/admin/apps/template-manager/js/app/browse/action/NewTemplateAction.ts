module app.browse.action {

    export class NewTemplateAction extends api.ui.Action {

        constructor() {
            super("New");
            this.onExecuted(() => {
                new app.browse.event.NewTemplateEvent().fire();
            });
        }

    }
}