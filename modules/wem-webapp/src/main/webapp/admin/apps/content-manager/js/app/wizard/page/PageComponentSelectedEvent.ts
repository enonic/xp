module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;

    export class PageComponentSelectedEvent {

        private path: ComponentPath;

        private componentEmpty: boolean;

        constructor(path: ComponentPath, componentEmpty:boolean) {
            this.path = path;
            this.componentEmpty = componentEmpty;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        isComponentEmpty(): boolean {
            return this.componentEmpty;
        }
    }
}