module app.wizard {

    export class RelationshipTypeCreatedEvent extends api.event.Event {

        constructor() {
            super('relationshipTypeCreated');
        }

        static on(handler:(event:app.wizard.RelationshipTypeCreatedEvent) => void) {
            api.event.onEvent('relationshipTypeCreated', handler);
        }
    }

    export class RelationshipTypeUpdatedEvent extends api.event.Event {

        constructor() {
            super('relationshipTypeUpdated');
        }

        static on(handler:(event:app.wizard.RelationshipTypeUpdatedEvent) => void) {
            api.event.onEvent('relationshipTypeUpdated', handler);
        }
    }

}
