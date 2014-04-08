module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;

    export class SortableStopEvent {

        private path: ComponentPath;

        private empty: boolean;

        constructor(path: ComponentPath, empty:boolean) {
            this.path = path;
            this.empty = empty;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        isEmpty(): boolean {
            return this.empty;
        }
    }
}