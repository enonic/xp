module api.liveedit {

    export class ItemViewSelectedEvent extends api.event.Event {

        private pageItemView: ItemView;

        private position: Position;

        private newlyCreated: boolean;

        private rightClicked: boolean;

        constructor(itemView: ItemView, position: Position, isNew: boolean = false, rightClicked: boolean = false) {
            super();
            this.pageItemView = itemView;
            this.position = position;
            this.newlyCreated = isNew;
            this.rightClicked = rightClicked;
        }

        getItemView(): ItemView {
            return this.pageItemView;
        }

        getPosition(): Position {
            return this.position;
        }

        isNew(): boolean {
            return this.newlyCreated;
        }

        isRightClicked(): boolean {
            return this.rightClicked;
        }

        static on(handler: (event: ItemViewSelectedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ItemViewSelectedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}
