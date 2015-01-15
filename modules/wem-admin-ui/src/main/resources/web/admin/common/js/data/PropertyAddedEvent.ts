module api.data {

    export class PropertyAddedEvent extends PropertyEvent {

        constructor(property: Property) {
            super(PropertyEventType.ADDED, property);
        }
    }
}