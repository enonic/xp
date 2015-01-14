module api.liveedit {

    import Event = api.event.Event;
    import Component = api.content.page.region.Component;

    export class DraggingComponentViewCompletedEvent extends Event {

        private componentView: ComponentView<Component>;

        constructor(componentView: ComponentView<Component>) {
            super();
            api.util.assertNotNull(componentView, "componentView cannot be null");
            this.componentView = componentView;
        }

        getComponentView(): ComponentView<Component> {
            return this.componentView;
        }

        static on(handler: (event: DraggingComponentViewCompletedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: DraggingComponentViewCompletedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}