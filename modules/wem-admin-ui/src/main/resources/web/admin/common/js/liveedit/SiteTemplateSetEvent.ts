module api.liveedit {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class SiteTemplateSetEvent extends api.event.Event2 {

        private siteTemplate: SiteTemplate;

        constructor(siteTemplate: SiteTemplate) {
            super();
            this.siteTemplate = siteTemplate;
        }

        getSiteTemplate(): SiteTemplate {
            return this.siteTemplate;
        }

        static on(handler: (event: SiteTemplateSetEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: SiteTemplateSetEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}