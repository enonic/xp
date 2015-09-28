module api.ui.tags {

    export class TagAddedEvent {

        private value: string;

        constructor(value: string) {
            this.value = value;
        }

        getValue(): string {
            return this.value;
        }

    }
}