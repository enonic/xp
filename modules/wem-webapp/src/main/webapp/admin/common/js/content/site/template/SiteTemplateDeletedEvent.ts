module api_content_site_template {

    export class SiteTemplateDeletedEvent extends api_event.Event {

        private siteTemplateKey: SiteTemplateKey;

        constructor(siteTemplateKey: SiteTemplateKey) {
            super('SiteTemplateDeletedEvent');
            this.siteTemplateKey = siteTemplateKey;
        }

        getSiteTemplateKey(): SiteTemplateKey {
            return this.siteTemplateKey;
        }

        static on(handler: (event: SiteTemplateDeletedEvent) => void) {
            api_event.onEvent('SiteTemplateDeletedEvent', handler);
        }

    }
}