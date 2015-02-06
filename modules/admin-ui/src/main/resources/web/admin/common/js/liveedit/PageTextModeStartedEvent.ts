module api.liveedit {

    export class PageTextModeStartedEvent extends api.event.Event {

        private pageView: PageView;

        constructor(pageView: PageView) {
            super();
            this.pageView = pageView;
        }

        getPageView(): PageView {
            return this.pageView;
        }

        static on(handler: (event: PageLockedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageLockedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}