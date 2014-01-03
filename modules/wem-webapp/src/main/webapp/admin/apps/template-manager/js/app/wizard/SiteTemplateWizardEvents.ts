module app.wizard {
    export class BaseSiteTemplateModelEvent extends api.event.Event {

        private model:api.content.site.template.SiteTemplateSummary[];

        constructor(name:string, model:api.content.site.template.SiteTemplateSummary[]) {
            this.model = model;
            super(name);
        }

        getModels():api.content.site.template.SiteTemplateSummary[] {
            return this.model;
        }
    }

    export class ShowSiteTemplateFormEvent extends api.event.Event {

        constructor() {
            super('showSiteTemplateForm');
        }

        static on(handler:(event:ShowSiteTemplateFormEvent) => void) {
            api.event.onEvent('showSiteTemplateForm', handler);
        }

    }

}
