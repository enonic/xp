module api.ui {

    export class Checkbox extends api.dom.FormInputEl {
        //TODO: USE HTML CHECKED PROPERTY INSTEAD OF ATTRIBUTE CHECKED! from ljl

        private checkbox: api.dom.InputEl;

        private label: api.dom.LabelEl;

        public static debug = false;

        constructor(text?: string, checked?: boolean) {
            super("div", "checkbox", undefined, String(checked != undefined ? checked : false));
            // we need an id for the label to interact nicely
            this.checkbox = <api.dom.InputEl> new api.dom.Element(new api.dom.NewElementBuilder().
                setTagName('input').
                setGenerateId(true));
            this.checkbox.getEl().setAttribute('type', 'checkbox');
            this.appendChild(this.checkbox);

            this.label = new api.dom.LabelEl(text, this.checkbox);
            this.appendChild(this.label);

            wemjq(this.checkbox.getHTMLElement()).change((e) => {
                if (Checkbox.debug) {
                    console.debug('Checkbox on change', e);
                }
                this.refreshValueChanged();
                this.refreshDirtyState();
            });
        }

        setChecked(newValue: boolean, silent?: boolean): Checkbox {
            super.setValue(String(newValue), silent);
            return this;
        }

        isChecked(): boolean {
            return super.getValue() == "true";
        }

        toggleChecked() {
            this.setChecked(!this.isChecked());
        }

        protected doSetValue(value: string, silent?: boolean) {
            if (Checkbox.debug) {
                console.debug('Checkbox.doSetValue: ', value);
            }
            this.checkbox.getHTMLElement()['checked'] = value == 'true';
        }

        protected doGetValue(): string {
            return String(this.checkbox.getHTMLElement()['checked']);
        }

        setValue(value: string, silent?: boolean): Checkbox {
            if (Checkbox.debug) {
                console.warn('Checkbox.setValue sets the value attribute, you may have wanted to use setChecked instead');
            }
            this.getEl().setValue(value);
            return this;
        }

        getValue(): string {
            if (Checkbox.debug) {
                console.warn('Checkbox.getValue gets the value attribute, you may have wanted to use getChecked instead');
            }
            return this.getEl().getValue();
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

        setDisabled(value: boolean): Checkbox {
            this.checkbox.getEl().setDisabled(value);
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