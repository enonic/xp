module app.browse.action {
    export class TemplateBrowseActions {

        public IMPORT_TEMPLATE:api.ui.Action;
        public NEW_TEMPLATE: api.ui.Action;
        public EDIT_TEMPLATE: api.ui.Action;
        public OPEN_TEMPLATE: api.ui.Action;
        public DELETE_TEMPLATE: api.ui.Action;
        public DUPLICATE_TEMPLATE: api.ui.Action;
        public EXPORT_TEMPLATE: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        private static INSTANCE: TemplateBrowseActions;

        static get(): TemplateBrowseActions {
            if (!TemplateBrowseActions.INSTANCE) {
                TemplateBrowseActions.INSTANCE = new TemplateBrowseActions();
            }
            return TemplateBrowseActions.INSTANCE;
        }

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

        updateActionsEnabledState(templates: any[]) {
            var modulesSelected = templates.length;
            var siteTemplateSelected: boolean = templates.some(function (templateRecord){
                var type: TemplateType = TemplateType[<string>templateRecord.get('templateType')];
                return type === TemplateType.SITE;
            });
            this.DELETE_TEMPLATE.setEnabled(siteTemplateSelected);
            this.EDIT_TEMPLATE.setEnabled(siteTemplateSelected);
            this.OPEN_TEMPLATE.setEnabled(siteTemplateSelected);
            this.DUPLICATE_TEMPLATE.setEnabled(siteTemplateSelected);
            this.EXPORT_TEMPLATE.setEnabled((modulesSelected === 1) && siteTemplateSelected);
        }

    }

}