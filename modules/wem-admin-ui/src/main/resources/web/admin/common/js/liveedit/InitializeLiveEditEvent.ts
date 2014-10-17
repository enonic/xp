module api.liveedit {

    import Content = api.content.Content;
    import Site = api.content.site.Site;
    import PageModel = api.content.page.PageModel;

    export class InitializeLiveEditEvent extends api.event.Event {

        private content: Content;

        private site: Site;

        private pageModel: PageModel;

        constructor(content: Content, site: Site, pageModel: PageModel) {
            super();
            this.content = content;
            this.site = site;
            this.pageModel = pageModel;
        }

        getContent(): Content {
            return this.content;
        }

        getSite(): Site {
            return this.site;
        }

        getPageModel(): PageModel {
            return this.pageModel;
        }

        static on(handler: (event: InitializeLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: InitializeLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}