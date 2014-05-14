module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;

    export class SortableStopEvent extends Event2 {

        private componentView: any;

        private componentPath: ComponentPath;

        private empty: boolean;

        constructor(componentView:any) {
            super();
            this.componentView = componentView;
            this.componentPath = componentView ? ComponentPath.fromString(componentView.getComponentPath()) : null;
            this.empty = componentView ? componentView.isEmpty() : false;
        }

        getComponentPath(): ComponentPath {
            return this.componentPath;
        }

        isEmpty(): boolean {
            return this.empty;
        }

        getComponentView(): any {
            return this.componentView;
        }

        static on(handler: (event: SortableStopEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: SortableStopEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}