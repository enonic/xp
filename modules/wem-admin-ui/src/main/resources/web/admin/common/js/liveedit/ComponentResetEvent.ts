module api.liveedit {

    import Event = api.event.Event;
    import ComponentView = api.liveedit.ComponentView;
    import Component = api.content.page.Component;

    export class ComponentResetEvent extends api.event.Event {

        private pageComponentView: ComponentView<Component>;

        constructor(pageComponentView: ComponentView<Component>) {
            super();
            this.pageComponentView = pageComponentView;
        }

        getComponentView(): ComponentView<Component> {
            return this.pageComponentView;
        }

        static on(handler: (event: ComponentResetEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentResetEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}