module api.liveedit {

    import Content = api.content.Content;
    import PageRegions = api.content.page.PageRegions;
    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class InitializeLiveEditEvent extends api.event.Event {

        private content: Content;

        private siteTemplate: SiteTemplate;

        private pageRegions: PageRegions;

        constructor(content: Content, siteTemplate: SiteTemplate, pageRegions: PageRegions) {
            super();
            this.content = content;
            this.siteTemplate = siteTemplate;
            this.pageRegions = pageRegions;
        }

        getContent(): Content {
            return this.content;
        }

        getSiteTemplate(): SiteTemplate {
            return this.siteTemplate;
        }

        getPageRegions(): PageRegions {
            return this.pageRegions;
        }

        static on(handler: (event: InitializeLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: InitializeLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}