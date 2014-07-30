module app.browse.action {

    export class ExportTemplateAction extends api.ui.Action {

        private templateTreeGrid: app.browse.TemplateTreeGrid;

        constructor() {
            super("Export");
            this.templateTreeGrid = null;
            this.setEnabled(false);
            this.onExecuted(() => {
                if (this.templateTreeGrid) {
                    var templates = this.templateTreeGrid.getSelectedDataNodes();
                    var template = templates.length > 0? templates[0] : null;
                    new app.browse.event.ExportTemplateEvent(template).fire();
                }
            });
        }

        setTemplateTreeGrid(templateTreeGrid: app.browse.TemplateTreeGrid) {
            this.templateTreeGrid = templateTreeGrid;
        }
    }
}