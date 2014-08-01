module api.liveedit {

    import Event = api.event.Event;
    import PageComponent = api.content.page.PageComponent;

    export class ItemFromContextWindowDroppedEvent extends Event {

        static on(handler: (event: ItemFromContextWindowDroppedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ItemFromContextWindowDroppedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}