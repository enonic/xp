module api.data {

    export class PropertyEvent {

        private type: PropertyEventType;

        private property: Property;

        constructor(type: PropertyEventType, property: Property) {
            this.type = type;
            this.property = property;
        }

        getType(): PropertyEventType {
            return this.type;
        }

        getProperty(): Property {
            return this.property;
        }

        getPath(): PropertyPath {
            return this.property.getPath();
        }

        toString(): string {
            return this.getPath().toString();
        }
    }
}