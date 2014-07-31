module api.content.site.template {

    export class SiteTemplateDeletedEvent extends api.event.Event2 {

        private siteTemplateKey: SiteTemplateKey;

        constructor(siteTemplateKey: SiteTemplateKey) {
            super();
            this.siteTemplateKey = siteTemplateKey;
        }

        getSiteTemplateKey(): SiteTemplateKey {
            return this.siteTemplateKey;
        }

        static on(handler: (event: SiteTemplateDeletedEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: SiteTemplateDeletedEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}