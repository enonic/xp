module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;

    export class SortableStopEvent extends Event2 {

        private pageComponentView: PageComponentView;

        private componentPath: ComponentPath;

        private empty: boolean;

        constructor(pageComponentView: PageComponentView) {
            super();
            this.pageComponentView = pageComponentView;
            this.componentPath = pageComponentView ? pageComponentView.getComponentPath() : null;
            this.empty = pageComponentView ? pageComponentView.isEmpty() : false;
        }

        getComponentPath(): ComponentPath {
            return this.componentPath;
        }

        isEmpty(): boolean {
            return this.empty;
        }

        getItemView(): PageComponentView {
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