module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;

    export class SortableStopEvent extends Event2 {

        private pageItemView: ItemView;

        private componentPath: ComponentPath;

        private empty: boolean;

        constructor(itemView: ItemView) {
            super();
            this.pageItemView = itemView;
            this.componentPath = itemView ? itemView.getComponentPath() : null;
            this.empty = itemView ? itemView.isEmpty() : false;
        }

        getComponentPath(): ComponentPath {
            return this.componentPath;
        }

        isEmpty(): boolean {
            return this.empty;
        }

        getItemView(): ItemView {
            return this.pageItemView;
        }

        static on(handler: (event: SortableStopEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: SortableStopEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}