module app_wizard {

    export class ContentTypeCreatedEvent extends api_event.Event {

        constructor() {
            super('contentTypeCreated');
        }

        static on(handler:(event:app_wizard.ContentTypeCreatedEvent) => void) {
            api_event.onEvent('contentTypeCreated', handler);
        }
    }

    export class ContentTypeUpdatedEvent extends api_event.Event {

        constructor() {
            super('contentTypeUpdated');
        }

        static on(handler:(event:app_wizard.ContentTypeUpdatedEvent) => void) {
            api_event.onEvent('contentTypeUpdated', handler);
        }
    }

}
