module api.data {

    export class PropertyRemovedEvent extends PropertyEvent {

        constructor(property: Property) {
            super(PropertyEventType.REMOVED, property);
        }
    }
}