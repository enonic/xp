module api.liveedit {

    import Event = api.event.Event;

    export class DraggingPageComponentViewStartedEvent extends Event {

        static on(handler: (event: DraggingPageComponentViewStartedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: DraggingPageComponentViewStartedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}