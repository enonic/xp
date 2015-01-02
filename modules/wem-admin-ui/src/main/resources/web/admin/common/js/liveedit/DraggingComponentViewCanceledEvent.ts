module api.liveedit {

    import Event = api.event.Event;
    import Component = api.content.page.Component;

    export class DraggingComponentViewCanceledEvent extends Event {

        private pageComponentView: ComponentView<Component>;

        constructor(pageComponentView: ComponentView<Component>) {
            super();
            this.pageComponentView = pageComponentView;
        }

        getPageComponentView(): ComponentView<Component> {
            return this.pageComponentView;
        }

        static on(handler: (event: DraggingComponentViewCompletedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: DraggingComponentViewCompletedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}