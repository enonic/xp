module api.app.wizard {

    export class WizardHeader extends api.dom.DivEl {

        private propertyChangedListener: {(event: api.PropertyChangedEvent):void}[] = [];

        constructor() {
            super("wizard-header");
        }

        onPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListener.push(listener);
        }

        unPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListener =
            this.propertyChangedListener.filter((currentListener: (event: api.PropertyChangedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        notifyPropertyChanged(property: string, oldValue: string, newValue: string) {
            this.propertyChangedListener.forEach((listener: (event: api.PropertyChangedEvent)=>void) => {
                listener.call(this, new api.PropertyChangedEvent(property, oldValue, newValue));
            })
        }

        giveFocus(): boolean {
            return false;
        }
    }
}