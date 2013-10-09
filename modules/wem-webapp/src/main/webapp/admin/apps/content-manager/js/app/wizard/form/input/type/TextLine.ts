module app_wizard_form_input_type {

    export class TextLine extends BaseInputTypeView {

        constructor() {
            super("TextLine");
        }

        createInputOccurrenceElement(index:number, property:api_data.Property):api_dom.Element {

            var inputEl = api_ui.TextInput.middle(this.getInput().getName() + "-" + index);
            inputEl.setName(this.getInput().getName());
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            return inputEl;
        }

        getValue(occurrence:api_dom.Element):api_data.Value {
            var inputEl = <api_ui.TextInput>occurrence;
            return new api_data.Value(inputEl.getValue(), api_data.ValueTypes.STRING);
        }
    }

    app_wizard_form_input.InputTypeManager.register("TextLine", TextLine);
}