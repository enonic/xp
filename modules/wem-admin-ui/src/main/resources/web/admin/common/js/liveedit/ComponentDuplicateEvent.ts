module api.liveedit {

    import Event = api.event.Event;
    import RegionPath = api.content.page.RegionPath;
    import Component = api.content.page.Component;

    export class ComponentDuplicateEvent extends api.event.Event {

        private originalPageComponentView: ComponentView<Component>;

        private duplicatedPageComponentView: ComponentView<Component>;

        constructor(originalPageComponentView: ComponentView<Component>,
                    duplicatedPageComponentView: ComponentView<Component>) {
            super();
            this.originalPageComponentView = originalPageComponentView;
            this.duplicatedPageComponentView = duplicatedPageComponentView;
        }

        getOriginalPageComponentView(): ComponentView<Component> {
            return this.originalPageComponentView
        }

        getDuplicatedPageComponentView(): ComponentView<Component> {
            return this.duplicatedPageComponentView;
        }

        static on(handler: (event: ComponentDuplicateEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentDuplicateEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}