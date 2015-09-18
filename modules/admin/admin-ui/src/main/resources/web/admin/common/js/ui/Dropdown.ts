module api.ui {
    export class Dropdown extends api.dom.SelectEl {

        private oldValue: string = "";

        private valueChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        constructor(name: string) {
            super();
            this.getEl().setAttribute("name", name);
            this.onChange((event: Event) => {
                this.notifyValueChanged(this.oldValue, this.getValue());
                this.oldValue = this.getValue();
            })
        }

        addOption(value: string, displayName: string) {
            var option = new DropdownOption(value, displayName);
            this.appendChild(option);
        }

        setValue(value: string): Dropdown {
            super.setValue(value);
            this.notifyValueChanged(this.oldValue, value);
            this.oldValue = value;
            return this;
        }


        onValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((currentListener: (event: ValueChangedEvent)=>void)=> {
                return currentListener != listener;
            });
        }

        private notifyValueChanged(oldValue: string, newValue: string) {
            this.valueChangedListeners.forEach((listener: (event: ValueChangedEvent)=>void)=> {
                listener.call(this, new ValueChangedEvent(oldValue, newValue));
            })
        }
    }


    export class DropdownOption extends api.dom.OptionEl {
        constructor(value: string, displayName: string) {
            super(value, displayName);
        }
    }
}