module app.browse.event {
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
}