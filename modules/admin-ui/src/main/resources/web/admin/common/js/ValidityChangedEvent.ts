module api {

    export class ValidityChangedEvent {

        private valid: boolean;

        constructor(valid: boolean) {
            this.valid = valid;
        }

        isValid(): boolean {
            return this.valid;
        }

    }
}