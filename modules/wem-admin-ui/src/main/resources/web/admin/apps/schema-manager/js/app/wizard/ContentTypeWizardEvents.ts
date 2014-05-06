module app.wizard {

    export class ContentTypeCreatedEvent extends api.event.Event {

        constructor() {
            super('contentTypeCreated');
        }

        static on(handler:(event:app.wizard.ContentTypeCreatedEvent) => void) {
            api.event.onEvent('contentTypeCreated', handler);
        }
    }

    export class ContentTypeUpdatedEvent extends api.event.Event {

        constructor() {
            super('contentTypeUpdated');
        }

        static on(handler:(event:app.wizard.ContentTypeUpdatedEvent) => void) {
            api.event.onEvent('contentTypeUpdated', handler);
        }
    }

}
