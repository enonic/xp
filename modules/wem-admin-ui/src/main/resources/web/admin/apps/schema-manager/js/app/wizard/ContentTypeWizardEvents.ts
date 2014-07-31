module app.wizard {

    import Event2 = api.event.Event2;

    export class ContentTypeCreatedEvent extends Event2 {

        static on(handler: (event: ContentTypeCreatedEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ContentTypeCreatedEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class ContentTypeUpdatedEvent extends Event2 {

        static on(handler: (event: ContentTypeUpdatedEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ContentTypeUpdatedEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

}
