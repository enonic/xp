module api.liveedit {

    import Event = api.event.Event;
    import Component = api.content.page.region.Component;

    export class ComponentViewDragDroppedEvent extends Event {

        private componentView: ComponentView<Component>;
        private regionView: RegionView;

        constructor(view: ComponentView<Component>, region: RegionView) {
            super();

            this.componentView = view;
            this.regionView = region;
        }

        getComponentView(): ComponentView<Component> {
            return this.componentView;
        }

        getRegionView(): RegionView {
            return this.regionView;
        }

        static on(handler: (event: ComponentViewDragDroppedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ComponentViewDragDroppedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}