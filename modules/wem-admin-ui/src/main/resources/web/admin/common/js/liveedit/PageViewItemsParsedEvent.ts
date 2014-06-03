module api.liveedit {

    export class PageViewItemsParsedEvent extends api.event.Event2 {

        private pageView: PageView;

        constructor(pageView: PageView) {
            super();
            this.pageView = pageView;
        }

        getPageView(): PageView {
            return this.pageView;
        }

        static on(handler: (event: PageComponentSelectEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentSelectEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}