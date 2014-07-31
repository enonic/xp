module app.wizard {

    import Event2 = api.event.Event2;

    export class RelationshipTypeCreatedEvent extends Event2 {

        static on(handler: (event: RelationshipTypeCreatedEvent) => void) {
            Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: RelationshipTypeCreatedEvent) => void) {
            Event2.unbind(api.util.getFullName(this), handler);
        }
    }

    export class RelationshipTypeUpdatedEvent extends Event2 {

        static on(handler: (event: RelationshipTypeUpdatedEvent) => void) {
            Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: RelationshipTypeUpdatedEvent) => void) {
            Event2.unbind(api.util.getFullName(this), handler);
        }
    }

}
