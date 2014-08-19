module app.browse.action {

    import TemplateType = app.browse.TemplateType;
    import TemplateSummary = app.browse.TemplateSummary;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class TemplateBrowseActions implements TreeGridActions {

        public IMPORT_TEMPLATE: api.ui.Action;
        public NEW_TEMPLATE: api.ui.Action;
        public EDIT_TEMPLATE: EditTemplateAction;
        public OPEN_TEMPLATE: api.ui.Action;
        public DELETE_TEMPLATE: DeleteTemplateAction;
        public DUPLICATE_TEMPLATE: api.ui.Action;
        public EXPORT_TEMPLATE: ExportTemplateAction;

        private allActions: api.ui.Action[] = [];

        constructor() {
            this.IMPORT_TEMPLATE = new ImportTemplateAction();
            this.NEW_TEMPLATE = new NewTemplateAction();
            this.EDIT_TEMPLATE = new EditTemplateAction();
            this.OPEN_TEMPLATE = new OpenTemplateAction();
            this.DELETE_TEMPLATE = new DeleteTemplateAction();
            this.DUPLICATE_TEMPLATE = new DuplicateTemplateAction();
            this.EXPORT_TEMPLATE = new ExportTemplateAction();

            this.allActions.push(this.IMPORT_TEMPLATE, this.NEW_TEMPLATE, this.EDIT_TEMPLATE, this.OPEN_TEMPLATE,
                this.DELETE_TEMPLATE, this.DUPLICATE_TEMPLATE, this.EXPORT_TEMPLATE);
        }

        updateActionsEnabledState(templates: TemplateSummary[]) {
            var modulesSelected = templates.length;
            var siteTemplateSelected: boolean = templates.some(function (templateSummary) {
                return templateSummary.isSiteTemplate();
            });
            this.DELETE_TEMPLATE.setEnabled(siteTemplateSelected);
            this.EDIT_TEMPLATE.setEnabled(siteTemplateSelected);
            this.OPEN_TEMPLATE.setEnabled(siteTemplateSelected);
            this.DUPLICATE_TEMPLATE.setEnabled(siteTemplateSelected);
            this.EXPORT_TEMPLATE.setEnabled((modulesSelected === 1) && siteTemplateSelected);
        }

        getAllActions(): api.ui.Action[] {
            return this.allActions;
        }

        setTemplateTreeGrid(templateTreeGrid: app.browse.TemplateTreeGrid) {
            this.EDIT_TEMPLATE.setTemplateTreeGrid(templateTreeGrid);
            this.DELETE_TEMPLATE.setTemplateTreeGrid(templateTreeGrid);
            this.EXPORT_TEMPLATE.setTemplateTreeGrid(templateTreeGrid);
        }

    }

}