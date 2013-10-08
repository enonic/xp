module app_wizard_form_input_type {

    export class BaseInputTypeView extends api_dom.DivEl implements InputTypeView {

        private input:api_schema_content_form.Input;

        private inputOccurrences:InputOccurrences;

        constructor(idPrefix:string) {
            super(idPrefix, "input-type-view");
        }

        getHTMLElement():HTMLElement {
            return super.getHTMLElement();
        }

        addFormItemOccurrencesListener(listener:app_wizard_form.FormItemOccurrencesListener) {
            this.inputOccurrences.addListener(listener);
        }

        removeFormItemOccurrencesListener(listener:app_wizard_form.FormItemOccurrencesListener) {
            this.inputOccurrences.removeListener(listener);
        }

        public maximumOccurrencesReached():boolean {
            return this.inputOccurrences.maximumOccurrencesReached();
        }

        createAndAddOccurrence() {
            this.inputOccurrences.createAndAddOccurrence();
        }

        layout(input:api_schema_content_form.Input, properties?:api_data.Property[]) {

            this.input = input;
            this.inputOccurrences = new InputOccurrences(this, this.input, properties);
            this.inputOccurrences.layout();
        }

        getValues():api_data.Value[] {

            return this.inputOccurrences.getValues();
        }

        getInput():api_schema_content_form.Input {
            return this.input;
        }

        createInputOccurrenceElement(index:number, property?:api_data.Property):api_dom.Element {
            throw new Error("Must be implemented by inheritor");
        }

        getValue(occurrence:api_dom.Element):api_data.Value {
            throw new Error("Must be implemented by inheritor");
        }
    }
}