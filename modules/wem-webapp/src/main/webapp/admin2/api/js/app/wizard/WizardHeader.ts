module api_app_wizard {

    export class WizardHeader extends api_dom.DivEl implements api_event.Observable {

        private listeners:WizardHeaderListener[] = [];

        constructor() {
            super("WizardHeader", "wizard-header");

        }

        addListener(listener:WizardHeaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:WizardHeaderListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyPropertyChanged(property:string, oldValue:string, newValue:string) {
            this.listeners.forEach((listener:WizardHeaderListener) => {
                if (listener.onPropertyChanged) {
                    listener.onPropertyChanged({
                        property: property,
                        oldValue: oldValue,
                        newValue: newValue});
                }
            });
        }
    }
}