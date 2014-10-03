module api.liveedit {

    import Content = api.content.Content;
    import Site = api.content.site.Site;
    import PageRegions = api.content.page.PageRegions;

    export class InitializeLiveEditEvent extends api.event.Event {

        private content: Content;

        private site: Site;

        private pageRegions: PageRegions;

        constructor(content: Content, site: Site, pageRegions: PageRegions) {
            super();
            this.content = content;
            this.site = site;
            this.pageRegions = pageRegions;
        }

        getContent(): Content {
            return this.content;
        }

        getSite(): Site {
            return this.site;
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