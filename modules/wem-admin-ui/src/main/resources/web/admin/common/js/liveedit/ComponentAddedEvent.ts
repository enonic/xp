module api.liveedit {

    import RegionPath = api.content.page.region.RegionPath;
    import Component = api.content.page.region.Component;

    export class ComponentAddedEvent extends api.event.Event {

        private componentView: ComponentView<Component>;

        setComponentView(componentView: ComponentView<Component>): ComponentAddedEvent {
            this.componentView = componentView;
            return this;
        }

        getComponentView(): ComponentView<Component> {
            return this.componentView;
        }

        static on(handler: (event: ComponentAddedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentAddedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}