module api.app.wizard {

    export class WizardStepValidityItem {

        private valid: boolean;

        private step: WizardStep;

        constructor(step: WizardStep, callback: (event: boolean)=>void) {
            this.step = step;
            this.valid = true;
            this.step.getStepForm().onValidityChanged((event: WizardStepValidityChangedEvent) => {
                this.valid = event.isValid();
                callback(event.isValid());
            });
        }

        getStep(): WizardStep {
            return this.step;
        }

        isValid(): boolean {
            return this.valid;
        }
    }
}