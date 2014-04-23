module api.ui {

    export class CheckboxInput extends api.dom.InputEl {
        //TODO: USE HTML CHECKED PROPERTY INSTEAD OF ATTRIBUTE CHECKED! from ljl
        /**
         * Input value before it was changed by last input event.
         */
        private oldValue: boolean = false;

        private valueChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        constructor(className?: string) {
            super(className);

            this.getEl().setAttribute('type', 'checkbox');

            jQuery(this.getHTMLElement()).change(() => {
                var newValue = this.isChecked();
                this.notifyValueChanged(this.oldValue, newValue);
                this.oldValue = newValue;
            });
        }

        setChecked(newValue: boolean, supressEvent?: boolean): CheckboxInput {
            var oldValue = this.isChecked();

            this.getHTMLElement()["checked"] = newValue

            if (newValue) {
                this.getEl().setAttribute("checked", "checked");
            }
            else {
                this.getEl().removeAttribute("checked");
            }

            if (!supressEvent) {
                this.notifyValueChanged(oldValue, newValue);
            }
            // save new value to know which value was before input event.
            this.oldValue = newValue;

            return this;
        }

        toggleChecked() {
            this.setChecked(!this.isChecked());
        }

        isChecked(): boolean {
            return this.getHTMLElement()["checked"];
        }

        setValue(value: string): CheckboxInput {
            throw new Error("CheckboxInput does not support method setValue, use setChecked instead");
        }

        getValue(): string {
            throw new Error("CheckboxInput does not support method setValue, use isChecked instead");
        }

        setName(value: string): CheckboxInput {
            super.setName(value);
            return this;
        }

        setPlaceholder(value: string): CheckboxInput {
            this.getEl().setAttribute('placeholder', value);
            return this;
        }

        getPlaceholder(): string {
            return this.getEl().getAttribute('placeholder');
        }


        onValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((currentListener: (event: ValueChangedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyValueChanged(oldValue: boolean, newValue: boolean) {
            this.valueChangedListeners.forEach((listener: (event: ValueChangedEvent)=>void) => {
                listener.call(this, new ValueChangedEvent(oldValue.toString(), newValue.toString()));
            });
        }
    }
}