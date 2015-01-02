module api.liveedit {

    import Component = api.content.page.Component;

    export class PageComponentRemoveEvent extends api.event.Event {

        private pageComponentView: PageComponentView<Component>;

        constructor(pageComponentView: PageComponentView<Component>) {
            super();
            this.pageComponentView = pageComponentView;
        }

        getPageComponentView(): PageComponentView<Component> {
            return this.pageComponentView;
        }

        static on(handler: (event: PageComponentRemoveEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentRemoveEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}