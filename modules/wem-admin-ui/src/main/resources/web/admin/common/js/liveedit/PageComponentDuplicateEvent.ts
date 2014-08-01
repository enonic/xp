module api.liveedit {

    import Event = api.event.Event;
    import RegionPath = api.content.page.RegionPath;
    import PageComponentType = api.content.page.PageComponentType;
    import PageComponent = api.content.page.PageComponent;

    export class PageComponentDuplicateEvent extends api.event.Event {

        private originalPageComponentView: PageComponentView<PageComponent>;

        private duplicatedPageComponentView: PageComponentView<PageComponent>;

        constructor(originalPageComponentView: PageComponentView<PageComponent>,
                    duplicatedPageComponentView: PageComponentView<PageComponent>) {
            super();
            this.originalPageComponentView = originalPageComponentView;
            this.duplicatedPageComponentView = duplicatedPageComponentView;
        }

        getOriginalPageComponentView(): PageComponentView<PageComponent> {
            return this.originalPageComponentView
        }

        getDuplicatedPageComponentView(): PageComponentView<PageComponent> {
            return this.duplicatedPageComponentView;
        }

        static on(handler: (event: PageComponentDuplicateEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentDuplicateEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}