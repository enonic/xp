module api.ui {

    export class ActivatedEvent {

        private index: number;

        constructor(index: number) {

            this.index = index;
        }

        getIndex(): number {
            return this.index;
        }
    }
}