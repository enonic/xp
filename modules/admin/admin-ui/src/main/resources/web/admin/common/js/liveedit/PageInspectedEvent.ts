module api.liveedit {

    import Event = api.event.Event;

    export class PageInspectedEvent extends Event {

        constructor() {
            super();
        }

        static on(handler: (event: PageInspectedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: PageInspectedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}