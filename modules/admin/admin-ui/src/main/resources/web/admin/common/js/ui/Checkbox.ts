module api.ui {

    export class Checkbox extends api.dom.FormInputEl {
        //TODO: USE HTML CHECKED PROPERTY INSTEAD OF ATTRIBUTE CHECKED! from ljl
        /**
         * Input value before it was changed by last input event.
         */
        private oldValue: boolean = false;

        private valueChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        private checkbox: api.dom.InputEl;

        private label: api.dom.LabelEl;

        constructor(text?: string) {
            super("div", "checkbox");
            // we need an id for the label to interact nicely
            this.checkbox = <api.dom.InputEl> new api.dom.Element(new api.dom.NewElementBuilder().
                setTagName('input').
                setGenerateId(true));
            this.checkbox.getEl().setAttribute('type', 'checkbox');
            this.appendChild(this.checkbox);

            this.label = new api.dom.LabelEl(text, this.checkbox);
            this.appendChild(this.label);

            wemjq(this.checkbox.getHTMLElement()).change(() => {
                var newValue = this.isChecked();
                this.notifyValueChanged(this.oldValue, newValue);
                this.oldValue = newValue;
            });
        }

        setChecked(newValue: boolean, suppressEvent?: boolean): Checkbox {
            this.checkbox.getHTMLElement()["checked"] = newValue;

            if (!suppressEvent) {
                this.notifyValueChanged(this.oldValue, newValue);
            }
            // save new value to know which value was before input event.
            this.oldValue = newValue;

            return this;
        }

        setDisabled(value: boolean): Checkbox {
            this.checkbox.getEl().setDisabled(value);
            return this;
        }

        toggleChecked() {
            this.setChecked(!this.isChecked());
        }

        isChecked(): boolean {
            return this.checkbox.getHTMLElement()["checked"];
        }

        setValue(value: string): Checkbox {
            throw new Error("CheckboxInput does not support method setValue, use setChecked instead");
        }

        getValue(): string {
            throw new Error("CheckboxInput does not support method setValue, use isChecked instead");
        }

        giveFocus(): boolean {
            return this.checkbox.giveFocus();
        }

        giveBlur(): boolean {
            return this.checkbox.giveBlur();
        }

        setName(value: string): Checkbox {
            this.checkbox.setName(value);
            return this;
        }

        setLabel(text: string): Checkbox {
            this.label.setValue(text);
            return this;
        }

        getLabel(): string {
            return this.label.getValue();
        }

        setPlaceholder(value: string): Checkbox {
            this.checkbox.getEl().setAttribute('placeholder', value);
            return this;
        }

        getPlaceholder(): string {
            return this.checkbox.getEl().getAttribute('placeholder');
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

        onFocus(listener: (event: FocusEvent) => void) {
            this.checkbox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.checkbox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.checkbox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.checkbox.unBlur(listener);
        }
    }
}