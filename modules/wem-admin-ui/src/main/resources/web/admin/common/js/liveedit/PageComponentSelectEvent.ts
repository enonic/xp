module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;

    export class PageComponentSelectEvent extends Event2 {

        private path: ComponentPath;

        private componentView: any;

        constructor(path: ComponentPath, componentView: any) {
            super();
            this.path = path;
            this.componentView = componentView;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getComponentView(): any {
            return this.componentView;
        }

        static on(handler: (event: PageComponentSelectEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentSelectEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}