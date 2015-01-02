module api.liveedit {

    import Component = api.content.page.Component;

    export class ComponentRemoveEvent extends api.event.Event {

        private pageComponentView: ComponentView<Component>;

        constructor(pageComponentView: ComponentView<Component>) {
            super();
            this.pageComponentView = pageComponentView;
        }

        getPageComponentView(): ComponentView<Component> {
            return this.pageComponentView;
        }

        static on(handler: (event: ComponentRemoveEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentRemoveEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}