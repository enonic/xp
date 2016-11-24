module api.ui {

    export class Checkbox extends api.dom.FormInputEl {
        //TODO: USE HTML CHECKED PROPERTY INSTEAD OF ATTRIBUTE CHECKED! from ljl

        private checkbox: api.dom.InputEl;

        private label: api.dom.LabelEl;

        public static debug = false;

        constructor(builder: CheckboxBuilder) {
            super("div", "checkbox", undefined, String(builder.checked || false));

            this.initCheckbox(builder.inputAlignment);
            this.initLabel(builder.text);

            this.appendChild(this.checkbox);
            this.appendChild(this.label);
        }

        isDisabled(): boolean {
            return this.checkbox.getEl().isDisabled();
        }

        private initCheckbox(inputAlignment: InputAlignment) {            // we need an id for the label to interact nicely
            // we need an id for the label to interact nicely
            this.checkbox = <api.dom.InputEl> new api.dom.Element(new api.dom.NewElementBuilder().setTagName('input').setGenerateId(true));
            this.checkbox.getEl().setAttribute('type', 'checkbox');
            this.addClass(this.getInputAlignmentAsString(inputAlignment));

            wemjq(this.checkbox.getHTMLElement()).change((e) => {
                if (Checkbox.debug) {
                    console.debug('Checkbox on change', e);
                }
                this.refreshValueChanged();
                this.refreshDirtyState();
            });

        }

        private initLabel(text: string) {
            this.label = new api.dom.LabelEl(text, this.checkbox);
        }

        private getInputAlignmentAsString(inputAlignment: InputAlignment = InputAlignment.LEFT): string {
            
            return InputAlignment[inputAlignment].toLowerCase();
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

        setDisabled(value: boolean, cls?: string): Checkbox {
            this.checkbox.getEl().setDisabled(value);
            if (cls) {
                this.toggleClass(cls, value);
            }
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

        static create(): CheckboxBuilder {
            return new CheckboxBuilder();
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

    export enum InputAlignment {
        TOP,
        RIGHT,
        LEFT,
        BOTTOM
    }

    export class CheckboxBuilder {
        text: string;

        checked: boolean;

        inputAlignment: InputAlignment;

        constructor() {
        }

        setLabelText(value: string): CheckboxBuilder {
            this.text = value;
            return this;
        }

        setChecked(value: boolean): CheckboxBuilder {
            this.checked = value;
            return this;
        }

        setInputAlignment(value: InputAlignment): CheckboxBuilder {
            this.inputAlignment = value;
            return this;
        }

        build(): Checkbox {
            return new Checkbox(this);
        }
    }



}