module app_wizard_form_input {

    export class NoInputTypeFoundView extends BaseInputTypeView {

        constructor() {
            super("NoInputTypeFound");
        }

        layout(input:api_schema_content_form.Input, properties?:api_data.Property[]) {

            var divEl = new api_dom.DivEl();
            divEl.getEl().setInnerHtml("Warning: no input type found: " + input.getInputType().toString());

            super.layout(input, properties);
        }

        createInputOccurrenceElement(index:number, property?:api_data.Property):api_dom.Element {

            var inputEl = api_ui.TextInput.middle(this.getInput().getName() + "-" + index);
            inputEl.setName(this.getInput().getName());
            if (property != null) {
                inputEl.setValue(property.getValue());
            }
            return inputEl;
        }

        getValue(occurrence:api_dom.Element):string {
            var inputEl = <api_ui.TextInput>occurrence;
            return inputEl.getValue();
        }
    }
}