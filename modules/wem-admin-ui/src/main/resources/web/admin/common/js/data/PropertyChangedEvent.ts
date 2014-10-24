module api.data {

    export class PropertyChangedEvent {

        private type: PropertyChangedEventType;

        private path: DataPath;

        private value: Value;

        constructor(type: PropertyChangedEventType, path: DataPath, value: Value) {
            this.type = type;
            this.path = path;
            this.value = value;
        }

        getType(): PropertyChangedEventType {
            return this.type;
        }

        getPath(): DataPath {
            return this.path;
        }

        getValue(): Value {
            return this.value;
        }
    }

    export enum PropertyChangedEventType {

        ADDED,
        CHANGED,
        REMOVED
    }
}