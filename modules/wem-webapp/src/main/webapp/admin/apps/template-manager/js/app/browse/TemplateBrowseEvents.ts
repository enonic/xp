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

        constructor(sitetemplateModel: api_content_site_template.SiteTemplateSummary) {
            super('deleteSitetemplatePrompt', [sitetemplateModel]);
        }

        getSiteTemplate(): api_content_site_template.SiteTemplateSummary {
            return this.getSiteTemplates()[0];
        }

        static on(handler: (event: DeleteSiteTemplatePromptEvent) => void) {
            api_event.onEvent('deleteSiteTemplatePrompt', handler);
        }
    }

}
