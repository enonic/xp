module app.browse {

    export class BaseSiteTemplateModelEvent extends api.event.Event {
        private model: api.content.site.template.SiteTemplateSummary[];

        constructor(name: string, model: api.content.site.template.SiteTemplateSummary[]) {
            this.model = model;
            super(name);
        }

        getSiteTemplates(): api.content.site.template.SiteTemplateSummary[] {
            return this.model;
        }
    }

    export class DeleteSiteTemplatePromptEvent extends BaseSiteTemplateModelEvent {

        constructor(siteTemplateModel: api.content.site.template.SiteTemplateSummary) {
            super('deleteSitetemplatePrompt', [siteTemplateModel]);
        }

        getSiteTemplate(): api.content.site.template.SiteTemplateSummary {
            return this.getSiteTemplates()[0];
        }

        static on(handler: (event: DeleteSiteTemplatePromptEvent) => void) {
            api.event.onEvent('deleteSiteTemplatePrompt', handler);
        }
    }

    export class ImportTemplateEvent extends api.event.Event {

        constructor() {
            super('importTemplate');
        }

        static on(handler:(event:ImportTemplateEvent) => void) {
            api.event.onEvent('importTemplate', handler);
        }
    }

    export class ExportTemplateEvent extends BaseSiteTemplateModelEvent {

        constructor(siteTemplate: api.content.site.template.SiteTemplateSummary) {
            super('exportTemplate', [siteTemplate]);
        }

        static on(handler:(event:ExportTemplateEvent) => void) {
            api.event.onEvent('exportTemplate', handler);
        }

        getSiteTemplate(): api.content.site.template.SiteTemplateSummary {
            return this.getSiteTemplates()[0];
        }
    }

    export class NewTemplateEvent extends api.event.Event {
        constructor() {
            super('newTemplate');
        }

        static on(handler:(event:NewTemplateEvent) => void) {
            api.event.onEvent('newTemplate', handler);
        }
    }
}
