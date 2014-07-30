module app.browse.action {

    export class EditTemplateAction extends api.ui.Action {

        private templateTreeGrid: app.browse.TemplateTreeGrid;

        constructor() {
            super("Edit");
            this.templateTreeGrid = null;
            this.setEnabled(false);
            this.onExecuted(() => {
                if (this.templateTreeGrid) {
                    var templates = this.templateTreeGrid.getSelectedDataNodes();
                    new app.browse.event.EditTemplateEvent(templates).fire();
                }
            });
        }

        setTemplateTreeGrid(templateTreeGrid: app.browse.TemplateTreeGrid) {
            this.templateTreeGrid = templateTreeGrid;
        }

    }
}