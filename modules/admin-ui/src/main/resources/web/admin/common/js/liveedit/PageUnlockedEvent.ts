module api.liveedit {

    export class PageUnlockedEvent extends api.event.Event {

        private pageView: PageView;

        constructor(pageView: PageView) {
            super();
            this.pageView = pageView;
        }

        getPageView(): PageView {
            return this.pageView;
        }

        static on(handler: (event: PageUnlockedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageUnlockedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}