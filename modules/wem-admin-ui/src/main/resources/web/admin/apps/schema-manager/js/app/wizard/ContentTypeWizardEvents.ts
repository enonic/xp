module app.wizard {

    import Event = api.event.Event;

    export class ContentTypeCreatedEvent extends Event {

        static on(handler: (event: ContentTypeCreatedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ContentTypeCreatedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class ContentTypeUpdatedEvent extends Event {

        static on(handler: (event: ContentTypeUpdatedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ContentTypeUpdatedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

}
