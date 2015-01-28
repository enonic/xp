module api.data {

    export class PropertyAddedEvent extends PropertyEvent {

        constructor(property: Property) {
            super(PropertyEventType.ADDED, property);
        }

        toString(): string {
            var value = this.getProperty().getValue();
            return "" + this.getPath().toString() + " = " + (value.getObject() ? value.getObject().toString() : null);
        }
    }
}