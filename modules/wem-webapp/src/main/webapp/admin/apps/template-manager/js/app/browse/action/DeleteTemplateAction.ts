module app.browse.action {

    export class DeleteTemplateAction extends api.ui.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.onExecuted(() => {
                var selection = components.gridPanel.getSelection()[0];
                var template = app.browse.TemplateSummary.fromExtModel(selection);
                new app.browse.event.DeleteTemplatePromptEvent(template).fire();
            });
        }
    }
}