module app.browse.action {

    export class EditTemplateAction extends api.ui.Action {

        constructor() {
            super("Edit");
            this.setEnabled(false);
            this.onExecuted(() => {
                var selection = components.gridPanel.getSelection();
                var template = app.browse.TemplateSummary.fromExtModelArray(selection);
                new app.browse.event.EditTemplateEvent(template).fire();
            });
        }

    }
}