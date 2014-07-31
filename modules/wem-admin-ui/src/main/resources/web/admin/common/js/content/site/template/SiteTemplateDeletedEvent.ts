module api.content.site.template {

    export class SiteTemplateDeletedEvent extends api.event.Event {

        private siteTemplateKey: SiteTemplateKey;

        constructor(siteTemplateKey: SiteTemplateKey) {
            super();
            this.siteTemplateKey = siteTemplateKey;
        }

        getSiteTemplateKey(): SiteTemplateKey {
            return this.siteTemplateKey;
        }

        static on(handler: (event: SiteTemplateDeletedEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: SiteTemplateDeletedEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }
}