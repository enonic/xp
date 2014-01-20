module api.app.wizard {

    export class WizardHeader extends api.dom.DivEl implements api.event.Observable {

        private listeners:WizardHeaderListener[] = [];

        constructor() {
            super("wizard-header");

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

        giveFocus(): boolean {
            return false;
        }
    }
}