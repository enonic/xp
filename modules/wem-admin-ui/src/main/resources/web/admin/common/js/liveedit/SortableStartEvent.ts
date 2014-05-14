module api.liveedit {

    import Event2 = api.event.Event2;

    export class SortableStartEvent extends Event2 {

        static on(handler: (event: SortableStartEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: SortableStartEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}