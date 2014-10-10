module api.liveedit {

    import Content = api.content.Content;
    import Site = api.content.site.Site;
    import Page = api.content.page.Page;

    export class InitializeLiveEditEvent extends api.event.Event {

        private content: Content;

        private site: Site;

        private page: Page;

        constructor(content: Content, site: Site, page: Page) {
            super();
            this.content = content;
            this.site = site;
            this.page = page;
        }

        getContent(): Content {
            return this.content;
        }

        getSite(): Site {
            return this.site;
        }

        getPage(): Page {
            return this.page;
        }

        static on(handler: (event: InitializeLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: InitializeLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}