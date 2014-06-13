module api.liveedit {

    export class ItemViewDeselectEvent extends api.event.Event2 {

        private itemView: ItemView;

        constructor(itemView: ItemView) {
            super();
            this.itemView = itemView;
        }

        getItemView(): ItemView {
            return this.itemView;
        }

        static on(handler: (event: ItemViewDeselectEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ItemViewDeselectEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}