module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;

    export class SortableStopEvent extends Event2 {

        private component: any; // LiveEdit.component.Component

        private path: ComponentPath;

        private empty: boolean;

        constructor(component:any) {
            super();
            this.component = component;
            this.path = component ? ComponentPath.fromString(component.getComponentPath()) : null;
            this.empty = component ? component.isEmpty() : false;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        isEmpty(): boolean {
            return this.empty;
        }

        getComponent(): any {
            return this.component;
        }

        static on(handler: (event: SortableStopEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: SortableStopEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}