module app.wizard {

    export class BaseContentWizardStepForm extends api.app.wizard.WizardStepForm {

        private validityChangedListeners: {(event: WizardStepValidityChangedEvent): void}[] = [];

        constructor(className?: string) {
            super(className);
        }

        onValidityChanged(listener: (event: WizardStepValidityChangedEvent) => void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: WizardStepValidityChangedEvent) => void) {
            this.validityChangedListeners = this.validityChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        /*
         *   public to be used by inheritors
         */
        notifyValidityChanged(event: WizardStepValidityChangedEvent) {
            this.validityChangedListeners.forEach((listener) => {
                listener(event);
            })
        }
    }
}