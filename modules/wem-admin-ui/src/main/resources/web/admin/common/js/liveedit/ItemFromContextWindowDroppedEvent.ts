module api.liveedit {

    import Event = api.event.Event;

    export class ItemFromContextWindowDroppedEvent extends Event {

        static on(handler: (event: ItemFromContextWindowDroppedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ItemFromContextWindowDroppedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}