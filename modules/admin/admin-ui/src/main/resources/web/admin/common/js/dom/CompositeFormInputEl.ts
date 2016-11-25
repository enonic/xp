module api.dom {

    export class CompositeFormInputEl extends api.dom.FormInputEl {

        private wrappedInput: api.dom.FormInputEl;

        private additionalElements: api.dom.Element[];

        constructor(input?: api.dom.FormInputEl) {
            super("div", "composite-input");

            if (input) {
                this.setWrappedInput(input);
            }
            this.additionalElements = [];
        }
        
        setAdditionalElements(...additionalElements: api.dom.Element[]) {
            additionalElements.forEach((element: api.dom.Element) => {
                this.addAdditionalElement(element);
            });
        }
        
        setWrappedInput(wrappedInput: api.dom.FormInputEl) {
            this.wrappedInput = wrappedInput;
            this.appendChild(this.wrappedInput);
        }

        doGetValue(): string {
            return this.wrappedInput.getValue();
        }

        doSetValue(value: string, silent?: boolean): CompositeFormInputEl {
            this.wrappedInput.setValue(value, silent);
            return this;
        }


        setValue(value: string, silent?: boolean, userInput?: boolean): CompositeFormInputEl {
            this.wrappedInput.setValue(value, silent, userInput);
            return this;
        }

        getValue(): string {
            return this.wrappedInput.getValue();
        }

        getName(): string {
            return this.wrappedInput.getName();
        }

        setName(name: string): CompositeFormInputEl {
            this.wrappedInput.setName(name);
            return this;
        }

        isDirty(): boolean {
            return this.wrappedInput.isDirty();
        }

        resetBaseValues() {
            super.resetBaseValues();
            this.wrappedInput.resetBaseValues();
        }

        onDirtyChanged(listener: (dirty: boolean) => void) {
            this.wrappedInput.onDirtyChanged(listener);
        }

        unDirtyChanged(listener: (dirty: boolean) => void) {
            this.wrappedInput.unDirtyChanged(listener);
        }

        onValueChanged(listener: (p1: api.ValueChangedEvent) => void) {
            this.wrappedInput.onValueChanged(listener);
        }

        unValueChanged(listener: (p1: api.ValueChangedEvent) => void) {
            this.wrappedInput.unValueChanged(listener);
        }

        onChange(listener: (event: Event) => void) {
            this.wrappedInput.onChange(listener);
        }

        unChange(listener: (event: Event) => void) {
            this.wrappedInput.unChange(listener);
        }

        onInput(listener: (event: Event) => void) {
            this.wrappedInput.onInput(listener);
        }

        unInput(listener: (event: Event) => void) {
            this.wrappedInput.unInput(listener);
        }

        giveFocus(): boolean {
            return this.wrappedInput.giveFocus();
        }

        giveBlur(): boolean {
            return this.wrappedInput.giveBlur();
        }

        addAdditionalElement(element: api.dom.Element) {
            this.appendChild(element);
            this.additionalElements.push(element);
        }
    }
}
