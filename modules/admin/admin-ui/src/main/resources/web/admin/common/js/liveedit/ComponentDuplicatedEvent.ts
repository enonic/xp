module api.liveedit {

    import Event = api.event.Event;
    import RegionPath = api.content.page.region.RegionPath;
    import Component = api.content.page.region.Component;

    export class ComponentDuplicatedEvent extends api.event.Event {

        private originalComponentView: ComponentView<Component>;

        private duplicatedComponentView: ComponentView<Component>;

        constructor(originalComponentView: ComponentView<Component>,
                    duplicatedComponentView: ComponentView<Component>) {
            super();
            this.originalComponentView = originalComponentView;
            this.duplicatedComponentView = duplicatedComponentView;
        }

        getOriginalComponentView(): ComponentView<Component> {
            return this.originalComponentView
        }

        getDuplicatedComponentView(): ComponentView<Component> {
            return this.duplicatedComponentView;
        }

        static on(handler: (event: ComponentDuplicatedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentDuplicatedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}