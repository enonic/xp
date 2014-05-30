module api.liveedit {

    import ComponentPath = api.content.page.ComponentPath;
    import PageComponentView = api.liveedit.ItemView;

    export class PageComponentSelectComponentEvent extends api.event.Event2 {

        private pageItemView: ItemView;

        private position: Position;

        constructor(itemView: ItemView, position:Position) {
            super();
            this.pageItemView = itemView;
            this.position = position;
        }

        getItemView(): ItemView {
            return this.pageItemView;
        }

        getPosition(): Position {
            return this.position;
        }

        static on(handler: (event: PageComponentSelectComponentEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentSelectComponentEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}