module app_browse {

    export class BaseSiteTemplateModelEvent extends api_event.Event {
        private model: api_content_site_template.SiteTemplateSummary[];

        constructor(name: string, model: api_content_site_template.SiteTemplateSummary[]) {
            this.model = model;
            super(name);
        }

        getSiteTemplates(): api_content_site_template.SiteTemplateSummary[] {
            return this.model;
        }
    }

    export class DeleteSiteTemplatePromptEvent extends BaseSiteTemplateModelEvent {

        constructor(siteTemplateModel: api_content_site_template.SiteTemplateSummary) {
            super('deleteSitetemplatePrompt', [siteTemplateModel]);
        }

        getSiteTemplate(): api_content_site_template.SiteTemplateSummary {
            return this.getSiteTemplates()[0];
        }

        static on(handler: (event: DeleteSiteTemplatePromptEvent) => void) {
            api_event.onEvent('deleteSiteTemplatePrompt', handler);
        }
    }

    export class ImportTemplateEvent extends api_event.Event {

        constructor() {
            super('importTemplate');
        }

        static on(handler:(event:ImportTemplateEvent) => void) {
            api_event.onEvent('importTemplate', handler);
        }
    }

    export class ExportTemplateEvent extends BaseSiteTemplateModelEvent {

        constructor(siteTemplate: api_content_site_template.SiteTemplateSummary) {
            super('exportTemplate', [siteTemplate]);
        }

        static on(handler:(event:ExportTemplateEvent) => void) {
            api_event.onEvent('exportTemplate', handler);
        }

        getSiteTemplate(): api_content_site_template.SiteTemplateSummary {
            return this.getSiteTemplates()[0];
        }
    }

    export class NewTemplateEvent extends api_event.Event {
        constructor() {
            super('newTemplate');
        }

        static on(handler:(event:NewTemplateEvent) => void) {
            api_event.onEvent('newTemplate', handler);
        }
    }
}
