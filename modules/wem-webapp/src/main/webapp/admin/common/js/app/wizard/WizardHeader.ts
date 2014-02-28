module api.app.wizard {

    export class WizardHeader extends api.dom.DivEl {

        private propertyChangedListener: {(event: PropertyChangedEvent):void}[] = [];

        constructor() {
            super("wizard-header");
        }

        onPropertyChanged(listener: (event: PropertyChangedEvent)=>void) {
            this.propertyChangedListener.push(listener);
        }

        unPropertyChanged(listener: (event: PropertyChangedEvent)=>void) {
            this.propertyChangedListener = this.propertyChangedListener.filter((currentListener: (event: PropertyChangedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        notifyPropertyChanged(property: string, oldValue: string, newValue: string) {
            this.propertyChangedListener.forEach((listener: (event: PropertyChangedEvent)=>void) => {
                listener.call(this, new PropertyChangedEvent(property, oldValue, newValue));
            })
        }

        giveFocus(): boolean {
            return false;
        }
    }
}