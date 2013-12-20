module app_browse {

    export class ImportTemplateAction extends api_ui.Action {

        constructor() {
            super("Import");
            this.addExecutionListener(() => {
                new ImportTemplateEvent().fire();
            });
        }
    }

    export class NewTemplateAction extends api_ui.Action {

        constructor() {
            super("New");
            this.addExecutionListener(() => {
                new NewTemplateEvent().fire();
            });
        }

    }

    export class EditTemplateAction extends api_ui.Action {

        constructor() {
            super("Edit");
            this.addExecutionListener(() => {
                console.log("edit template action");
            });
        }

    }

    export class OpenTemplateAction extends api_ui.Action {

        constructor() {
            super("Open");
            this.addExecutionListener(() => {
                console.log("open template action");
            });
        }

    }

    export class DeleteTemplateAction extends api_ui.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                var selection = components.gridPanel.getSelection()[0];
                var siteTemplateModel = api_content_site_template.SiteTemplateSummary.fromExtModel(selection);
                new DeleteSiteTemplatePromptEvent(siteTemplateModel).fire();
            });
        }
    }

    export class DuplicateTemplateAction extends api_ui.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                console.log("duplicate template action");
            });
        }

    }

    export class ExportTemplateAction extends api_ui.Action {

        constructor() {
            super("Export");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                var selection = components.gridPanel.getSelection()[0];
                var siteTemplateModel = api_content_site_template.SiteTemplateSummary.fromExtModel(selection);
                new ExportTemplateEvent(siteTemplateModel).fire();
            });
        }

    }

    export class TemplateBrowseActions {

        public IMPORT_TEMPLATE:api_ui.Action;
        public NEW_TEMPLATE: api_ui.Action;
        public EDIT_TEMPLATE: api_ui.Action;
        public OPEN_TEMPLATE: api_ui.Action;
        public DELETE_TEMPLATE: api_ui.Action;
        public DUPLICATE_TEMPLATE: api_ui.Action;
        public EXPORT_TEMPLATE: api_ui.Action;

        private allActions: api_ui.Action[] = [];

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

        updateActionsEnabledState(modules: any[]) {
            var modulesSelected = modules.length;
            this.DELETE_TEMPLATE.setEnabled(modulesSelected > 0);
            this.EXPORT_TEMPLATE.setEnabled(modulesSelected === 1);
        }

    }

}