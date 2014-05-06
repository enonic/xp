module api.app.wizard {

    export class PropertyChangedEvent {

        private property: string;

        private oldValue: string;

        private newValue: string;

        constructor(property: string, oldValue: string, newValue: string) {
            this.property = property;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        getProperty(): string {
            return this.property;
        }

        getOldValue(): string {
            return this.oldValue;
        }

        getNewValue(): string {
            return this.newValue;
        }
    }
}