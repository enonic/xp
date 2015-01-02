module api.liveedit {

    import Event = api.event.Event;
    import RegionPath = api.content.page.RegionPath;
    import Component = api.content.page.Component;

    export class ComponentDuplicateEvent extends api.event.Event {

        private originalPageComponentView: PageComponentView<Component>;

        private duplicatedPageComponentView: PageComponentView<Component>;

        constructor(originalPageComponentView: PageComponentView<Component>,
                    duplicatedPageComponentView: PageComponentView<Component>) {
            super();
            this.originalPageComponentView = originalPageComponentView;
            this.duplicatedPageComponentView = duplicatedPageComponentView;
        }

        getOriginalPageComponentView(): PageComponentView<Component> {
            return this.originalPageComponentView
        }

        getDuplicatedPageComponentView(): PageComponentView<Component> {
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