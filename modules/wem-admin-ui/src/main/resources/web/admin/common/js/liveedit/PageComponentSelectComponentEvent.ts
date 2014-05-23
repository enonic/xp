module api.liveedit {

    import ComponentPath = api.content.page.ComponentPath;

    export interface Position {
        x: number;
        y: number;
    }

    export class PageComponentSelectComponentEvent extends api.event.Event2 {

        private itemView: ItemView;

        private position: Position;

        constructor(itemView: ItemView, position: Position) {
            super();
            this.itemView = itemView;
            this.position = position;
        }

        getItemView(): ItemView {
            return this.itemView;
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