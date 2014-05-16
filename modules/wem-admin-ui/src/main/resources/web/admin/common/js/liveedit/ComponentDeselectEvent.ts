module api.liveedit {

    import Event2 = api.event.Event2;

    export class ComponentDeselectEvent extends Event2 {

        static on(handler: (event: ComponentDeselectEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentDeselectEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}