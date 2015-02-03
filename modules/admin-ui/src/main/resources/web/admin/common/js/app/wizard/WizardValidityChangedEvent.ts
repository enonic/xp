module api.app.wizard {

    export class WizardValidityChangedEvent {

        private valid: boolean;

        constructor(valid: boolean) {
            this.valid = valid;
        }

        isValid(): boolean {
            return this.valid;
        }

    }
}