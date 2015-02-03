module api.app.wizard {

    export class WizardStepsValidityManager {

        private items: WizardStepValidityItem[];

        private validityChangedListeners: {(event: WizardValidityChangedEvent):void}[] = [];

        constructor() {
            this.items = [];
            this.validityChangedListeners = [];
        }

        clearItems() {
            this.items = [];
        }

        getItems(): WizardStepValidityItem[] {
            return this.items;
        }

        addItem(step: WizardStep) {
            this.items.push(new WizardStepValidityItem(step, this.notifyValidityChanged.bind(this)));
        }

        removeItem(step: WizardStep) {
            var index = this.items.map((item) => { return item.getStep(); }).indexOf(step);
            if (index >= 0) {
                this.items.splice(index, 1);
            }
        }

        isAllValid(): boolean {
            var result = true;
            for (var i = 0; i < this.items.length; i++) {
                if (!this.items[i].isValid()) {
                    result = false;
                }
            }

            return result;
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