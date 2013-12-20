module app_wizard {
    export class BaseSiteTemplateModelEvent extends api_event.Event {

        private model:api_content_site_template.SiteTemplateSummary[];

        constructor(name:string, model:api_content_site_template.SiteTemplateSummary[]) {
            this.model = model;
            super(name);
        }

        getModels():api_content_site_template.SiteTemplateSummary[] {
            return this.model;
        }
    }

    export class ShowSiteTemplateFormEvent extends api_event.Event {

        constructor() {
            super('showSiteTemplateForm');
        }

        static on(handler:(event:ShowSiteTemplateFormEvent) => void) {
            api_event.onEvent('showSiteTemplateForm', handler);
        }

    }

}
