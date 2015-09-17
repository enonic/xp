module api.liveedit {

    export class ItemViewDeselectedEvent extends api.event.Event {

        private itemView: ItemView;

        constructor(itemView: ItemView) {
            super();
            this.itemView = itemView;
        }

        getItemView(): ItemView {
            return this.itemView;
        }

        static on(handler: (event: ItemViewDeselectedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ItemViewDeselectedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}