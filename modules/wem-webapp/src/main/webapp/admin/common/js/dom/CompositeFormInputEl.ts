module api.ui.form {

    export class CompositeFormInputEl extends api.dom.FormInputEl {

        private wrappedInput: api.dom.FormInputEl;

        private additionalElements: api.dom.Element[] = [];

        constructor(wrappedInput: api.dom.FormInputEl, ...additionalElements: api.dom.Element[]) {
            super("div");
            this.wrappedInput = wrappedInput;
            this.additionalElements = additionalElements;

            this.appendChild(this.wrappedInput);

            if (this.additionalElements != null) {
                this.additionalElements.forEach((element: api.dom.Element) => {
                    this.appendChild(element);
                });
            }
        }

        getValue(): string {
            return this.wrappedInput.getValue();
        }

        getName(): string {
            return this.wrappedInput.getName();
        }

        setValue(value: string) {
            this.wrappedInput.setValue(value);
        }
    }
}
