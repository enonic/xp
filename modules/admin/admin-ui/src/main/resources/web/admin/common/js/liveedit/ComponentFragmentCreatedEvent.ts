module api.liveedit {

    import Event = api.event.Event;
    import Component = api.content.page.region.Component;

    export class ComponentFragmentCreatedEvent extends api.event.Event {

        private componentView: ComponentView<Component>;

        private fragmentContent: api.content.Content;

        constructor(componentView: ComponentView<Component>, fragmentContent: api.content.Content) {
            super();
            this.componentView = componentView;
            this.fragmentContent = fragmentContent;
        }

        getComponentView(): ComponentView<Component> {
            return this.componentView;
        }

        getFragmentContent(): api.content.Content {
            return this.fragmentContent;
        }

        static on(handler: (event: ComponentFragmentCreatedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentFragmentCreatedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}