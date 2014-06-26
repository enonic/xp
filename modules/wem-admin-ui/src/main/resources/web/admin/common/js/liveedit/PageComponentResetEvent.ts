module api.liveedit {

    import Event2 = api.event.Event2;
    import PageComponentView = api.liveedit.PageComponentView;
    import PageComponent = api.content.page.PageComponent;

    export class PageComponentResetEvent extends api.event.Event2 {

        private pageComponentView: PageComponentView<PageComponent>;

        constructor(pageComponentView: PageComponentView<PageComponent>) {
            super();
            this.pageComponentView = pageComponentView;
        }

        getComponentView(): PageComponentView<PageComponent> {
            return this.pageComponentView;
        }

        static on(handler: (event: PageComponentResetEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentResetEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}