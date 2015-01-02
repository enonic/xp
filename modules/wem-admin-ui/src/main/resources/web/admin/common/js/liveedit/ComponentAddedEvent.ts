module api.liveedit {

    import RegionPath = api.content.page.RegionPath;
    import Component = api.content.page.Component;

    export class ComponentAddedEvent extends api.event.Event {

        private pageComponentView: ComponentView<Component>;

        setPageComponentView(pageComponentView: ComponentView<Component>): ComponentAddedEvent {
            this.pageComponentView = pageComponentView;
            return this;
        }

        getPageComponentView(): ComponentView<Component> {
            return this.pageComponentView;
        }

        static on(handler: (event: ComponentAddedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentAddedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}