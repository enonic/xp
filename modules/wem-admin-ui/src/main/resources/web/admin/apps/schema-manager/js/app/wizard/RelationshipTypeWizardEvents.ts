module app.wizard {

    import Event = api.event.Event;

    export class RelationshipTypeCreatedEvent extends Event {

        static on(handler: (event: RelationshipTypeCreatedEvent) => void) {
            Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: RelationshipTypeCreatedEvent) => void) {
            Event.unbind(api.util.getFullName(this), handler);
        }
    }

    export class RelationshipTypeUpdatedEvent extends Event {

        static on(handler: (event: RelationshipTypeUpdatedEvent) => void) {
            Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: RelationshipTypeUpdatedEvent) => void) {
            Event.unbind(api.util.getFullName(this), handler);
        }
    }

}
