module app.wizard.site {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class SiteTemplateChangedEvent {

        private siteTemplate: SiteTemplate;

        constructor(siteTemplate: SiteTemplate) {
            this.siteTemplate = siteTemplate;
        }

        getSiteTemplate(): SiteTemplate {
            return this.siteTemplate;
        }
    }
}