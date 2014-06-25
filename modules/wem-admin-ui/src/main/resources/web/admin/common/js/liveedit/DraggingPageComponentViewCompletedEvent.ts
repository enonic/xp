module api.liveedit {

    import Event2 = api.event.Event2;
    import PageComponent = api.content.page.PageComponent;

    export class DraggingPageComponentViewCompletedEvent extends Event2 {

        private pageComponentView: PageComponentView<PageComponent>;

        private empty: boolean;

        constructor(pageComponentView: PageComponentView<PageComponent>) {
            super();
            this.pageComponentView = pageComponentView;
            this.empty = pageComponentView ? pageComponentView.isEmpty() : false;
        }

        getPageComponentView(): PageComponentView<PageComponent> {
            return this.pageComponentView;
        }

        static on(handler: (event: DraggingPageComponentViewCompletedEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: DraggingPageComponentViewCompletedEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}