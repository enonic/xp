module api.app.wizard {

    export class WizardValidityManager {

        private steps: WizardStep[];

        private header: WizardHeader;

        private validityChangedListeners: {(event: WizardValidityChangedEvent):void}[] = [];

        constructor() {
            this.steps = [];
            this.validityChangedListeners = [];
        }

        clearItems() {
            this.steps = [];
        }

        getSteps(): WizardStep[] {
            return this.steps;
        }

        addItem(step: WizardStep) {
            this.steps.push(step);
            step.getStepForm().onValidityChanged((event: WizardStepValidityChangedEvent) => {
                this.notifyValidityChanged(event.isValid());
            });
        }

        removeItem(step: WizardStep) {
            var index = this.steps.indexOf(step);
            if (index >= 0) {
                this.steps.splice(index, 1);
            }
        }

        setHeader(header: WizardHeader) {
            this.header = header;
            this.header.onPropertyChanged((event) => {
                this.notifyValidityChanged(this.header.isValid());
            });
        }

        isAllValid(): boolean {
            if (this.header && !this.header.isValid()) {
                return false;
            }

            for (var i = 0; i < this.steps.length; i++) {
                if (!this.steps[i].getStepForm().isValid()) {
                    return false;
                }
            }

            return true;
        }

        onValidityChanged(listener: (event: WizardValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: WizardValidityChangedEvent)=>void) {
            this.validityChangedListeners = this.validityChangedListeners.filter((curr) => {
                return curr != listener;
            });
        }

        notifyValidityChanged(valid: boolean) {
            this.validityChangedListeners.forEach((listener: (event: WizardValidityChangedEvent)=>void)=> {
                listener.call(this, new WizardValidityChangedEvent(valid));
            });
        }

    }
}