module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;
    import PageComponent = api.content.page.PageComponent;

    export class SortableStopEvent extends Event2 {

        private pageComponentView: PageComponentView<PageComponent>;

        private componentPath: ComponentPath;

        private empty: boolean;

        constructor(pageComponentView: PageComponentView<PageComponent>) {
            super();
            this.pageComponentView = pageComponentView;
            this.componentPath = pageComponentView ? pageComponentView.getComponentPath() : null;
            this.empty = pageComponentView ? pageComponentView.isEmpty() : false;
        }

        getPageComponentView(): PageComponentView<PageComponent> {
            return this.pageComponentView;
        }

        static on(handler: (event: SortableStopEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: SortableStopEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}