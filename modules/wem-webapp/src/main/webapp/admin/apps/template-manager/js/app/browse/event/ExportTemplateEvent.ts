module app.browse.event {
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
}