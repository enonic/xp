module api {

    /**
     * An event representing that a property of an object have changed.
     */
    export class PropertyChangedEvent {

        private propertyName: string;

        private oldValue: any;

        private newValue: any;

        constructor(propertyName: string, oldValue: any, newValue: any) {

            this.propertyName = propertyName;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        getPropertyName(): string {
            return this.propertyName;
        }

        getOldValue(): any {
            return this.oldValue;
        }

        getNewValue(): any {
            return this.newValue;
        }
    }
}