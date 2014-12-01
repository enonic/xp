module api.data {

    export class PropertyChangedEvent {

        private type: PropertyChangedEventType;

        private path: PropertyPath;

        private value: Value;

        constructor(type: PropertyChangedEventType, path: PropertyPath, value: Value) {
            this.type = type;
            this.path = path;
            this.value = value;
        }

        getType(): PropertyChangedEventType {
            return this.type;
        }

        getPath(): PropertyPath {
            return this.path;
        }

        getValue(): Value {
            return this.value;
        }
    }
}