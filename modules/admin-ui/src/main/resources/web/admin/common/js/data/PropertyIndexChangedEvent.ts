module api.data {

    export class PropertyIndexChangedEvent extends PropertyEvent {

        private previousIndex: number;

        private newIndex: number;

        constructor(property: Property, previousIndex: number, newIndex: number) {
            super(PropertyEventType.INDEX_CHANGED, property);
            this.previousIndex = previousIndex;
            this.newIndex = newIndex;
        }

        getPreviousIndex(): number {
            return this.previousIndex;
        }

        getNewIndex(): number {
            return this.newIndex;
        }

        toString(): string {
            return "[" + this.previousIndex + "] -> [" + this.newIndex + "]";
        }
    }
}