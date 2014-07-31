module api.liveedit {

    import Event = api.event.Event;

    export class PageComponentLoadedEvent extends Event {

        private itemView: ItemView;

        constructor(itemView: ItemView) {
            super();
            this.itemView = itemView;
        }

        getItemView(): ItemView {
            return this.itemView;
        }

        static on(handler: (event: PageComponentLoadedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentLoadedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}