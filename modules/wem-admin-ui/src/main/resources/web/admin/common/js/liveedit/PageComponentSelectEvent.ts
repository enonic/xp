module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;
    import ItemView = api.liveedit.ItemView;

    export class PageComponentSelectEvent extends Event2 {

        private path: ComponentPath;

        private pageItemView: ItemView;

        constructor(path: ComponentPath, componentView: ItemView) {
            super();
            this.path = path;
            this.pageItemView = componentView;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getItemView(): ItemView {
            return this.pageItemView;
        }

        static on(handler: (event: PageComponentSelectEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentSelectEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}