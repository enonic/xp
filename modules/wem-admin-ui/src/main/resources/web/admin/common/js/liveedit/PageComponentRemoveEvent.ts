module api.liveedit {

    import PageComponent = api.content.page.PageComponent;

    export class PageComponentRemoveEvent extends api.event.Event {

        private pageComponentView: PageComponentView<PageComponent>;

        constructor(pageComponentView: PageComponentView<PageComponent>) {
            super();
            this.pageComponentView = pageComponentView;
        }

        getPageComponentView(): PageComponentView<PageComponent> {
            return this.pageComponentView;
        }

        static on(handler: (event: PageComponentRemoveEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentRemoveEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}