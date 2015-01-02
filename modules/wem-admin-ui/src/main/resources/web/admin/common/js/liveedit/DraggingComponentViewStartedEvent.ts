module api.liveedit {

    import Event = api.event.Event;

    export class DraggingComponentViewStartedEvent extends Event {

        static on(handler: (event: DraggingComponentViewStartedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: DraggingComponentViewStartedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}