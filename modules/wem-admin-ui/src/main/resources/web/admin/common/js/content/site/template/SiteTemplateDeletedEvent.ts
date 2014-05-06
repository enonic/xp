module api.content.site.template {

    export class SiteTemplateDeletedEvent extends api.event.Event {

        private siteTemplateKey: SiteTemplateKey;

        constructor(siteTemplateKey: SiteTemplateKey) {
            super('SiteTemplateDeletedEvent');
            this.siteTemplateKey = siteTemplateKey;
        }

        getSiteTemplateKey(): SiteTemplateKey {
            return this.siteTemplateKey;
        }

        static on(handler: (event: SiteTemplateDeletedEvent) => void) {
            api.event.onEvent('SiteTemplateDeletedEvent', handler);
        }

    }
}