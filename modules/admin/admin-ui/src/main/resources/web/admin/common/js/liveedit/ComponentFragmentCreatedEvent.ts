module api.liveedit {

    import Event = api.event.Event;
    import Component = api.content.page.region.Component;
    import FragmentComponentView = api.liveedit.fragment.FragmentComponentView;
    import ComponentType = api.content.page.region.ComponentType;

    export class ComponentFragmentCreatedEvent extends api.event.Event {

        private sourceComponentType: ComponentType;

        private fragmentComponentView: FragmentComponentView;

        private fragmentContent: api.content.Content;

        constructor(fragmentComponentView: FragmentComponentView, sourceComponentType: ComponentType,
                    fragmentContent: api.content.Content) {
            super();
            this.fragmentComponentView = fragmentComponentView;
            this.sourceComponentType = sourceComponentType;
            this.fragmentContent = fragmentContent;
        }

        getComponentView(): FragmentComponentView {
            return this.fragmentComponentView;
        }

        getFragmentContent(): api.content.Content {
            return this.fragmentContent;
        }

        getSourceComponentType(): ComponentType {
            return this.sourceComponentType;
        }

        static on(handler: (event: ComponentFragmentCreatedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentFragmentCreatedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}