module app_wizard {

    export class RelationshipTypeCreatedEvent extends api_event.Event {

        constructor() {
            super('relationshipTypeCreated');
        }

        static on(handler:(event:app_wizard.RelationshipTypeCreatedEvent) => void) {
            api_event.onEvent('relationshipTypeCreated', handler);
        }
    }

    export class RelationshipTypeUpdatedEvent extends api_event.Event {

        constructor() {
            super('relationshipTypeUpdated');
        }

        static on(handler:(event:app_wizard.RelationshipTypeUpdatedEvent) => void) {
            api_event.onEvent('relationshipTypeUpdated', handler);
        }
    }

}
