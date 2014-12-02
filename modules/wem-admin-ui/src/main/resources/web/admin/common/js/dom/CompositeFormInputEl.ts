module api.dom {

    export class CompositeFormInputEl extends api.dom.FormInputEl {

        private wrappedInput: api.dom.FormInputEl;

        private additionalElements: api.dom.Element[] = [];

        constructor(wrappedInput: api.dom.FormInputEl, ...additionalElements: api.dom.Element[]) {
            super("div");
            this.addClass("composite-input");

            this.wrappedInput = wrappedInput;
            this.appendChild(this.wrappedInput);

            if (additionalElements) {
                additionalElements.forEach((element: api.dom.Element) => {
                    this.addAdditionalElement(element);
                });
            }
        }

        getValue(): string {
            return this.wrappedInput.getValue();
        }

        getName(): string {
            return this.wrappedInput.getName();
        }

        setValue(value: string): CompositeFormInputEl {
            this.wrappedInput.setValue(value);
            return this;
        }

        addAdditionalElement(element: api.dom.Element) {
            this.appendChild(element);
            this.additionalElements.push(element);
        }
    }
}
