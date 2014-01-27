module api.ui {
    export class Dropdown extends api.dom.SelectEl {

        private oldValue:string = "";

        private listeners:{[eventName:string]: {(event:InputEvent):void}[]} = {};

        constructor(name:string) {
            super();
            this.listeners[InputEvents.ValueChanged] = [];
            this.getEl().setAttribute("name", name);
            this.getEl().addEventListener("change", () => {
                this.notifyValueChanged(new ValueChangedEvent(this.oldValue, this.getValue()));
                this.oldValue = this.getValue();
            })
        }

        addOption(value:string, displayName:string) {
            var option = new DropdownOption(value, displayName);
            this.appendChild(option);
        }


        private addListener(eventName:InputEvents, listener:(event:InputEvent)=>void) {
            if (this.listeners[eventName] ) {
                this.listeners[eventName].push(listener);
            }
        }

        onValueChanged(listener:(event:ValueChangedEvent)=>void) {
            this.addListener(InputEvents.ValueChanged, listener);
        }

        private notifyListeners(eventName:InputEvents, event:InputEvent) {
            this.listeners[eventName].forEach((listener:(event:InputEvent)=>void)=> {
                listener(event);
            });
        }

        private notifyValueChanged(event:ValueChangedEvent) {
            this.notifyListeners(InputEvents.ValueChanged, event);
        }
    }


    export class DropdownOption extends api.dom.OptionEl {
        constructor(value:string, displayName:string) {
            super(value, displayName);
        }
    }
}