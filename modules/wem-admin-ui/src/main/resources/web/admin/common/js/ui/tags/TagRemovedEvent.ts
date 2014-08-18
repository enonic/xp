module api.ui.tags {

    export class TagRemovedEvent {

        private value: string;

        private index: number;

        constructor(value: string, index: number) {
            this.value = value;
            this.index = index;
        }

        getValue(): string {
            return this.value;
        }

        getIndex(): number {
            return this.index;
        }

    }
}