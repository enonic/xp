module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;

    export class SortableStopEvent {

        private path: ComponentPath;

        private empty: boolean;

        private component: any; // LiveEdit.component.Component

        constructor(path: ComponentPath, empty:boolean, component:any) {
            this.path = path;
            this.empty = empty;
            this.component = component;
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
    }
}