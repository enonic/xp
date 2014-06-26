module api.liveedit {

    import Event2 = api.event.Event2;

    export class DraggingPageComponentViewStartedEvent extends Event2 {

        static on(handler: (event: DraggingPageComponentViewStartedEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: DraggingPageComponentViewStartedEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}