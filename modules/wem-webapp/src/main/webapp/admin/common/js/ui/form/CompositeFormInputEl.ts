module api_ui_form {

    export class CompositeFormInputEl extends api_dom.FormInputEl {

        private wrappedInput: api_dom.FormInputEl;

        private additionalElements: api_dom.Element[] = [];

        constructor(wrappedInput: api_dom.FormInputEl, ...additionalElements: api_dom.Element[]) {
            super("div");
            this.wrappedInput = wrappedInput;
            this.additionalElements = additionalElements;

            this.appendChild(this.wrappedInput);

            if (this.additionalElements != null) {
                this.additionalElements.forEach((element: api_dom.Element) => {
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
