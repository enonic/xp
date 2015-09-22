module api.app.wizard {

    export class WizardStepValidityChangedEvent {

        private valid: boolean;

        constructor(isValid: boolean) {
            this.valid = isValid;
        }

        isValid(): boolean {
            return this.valid;
        }
    }
}