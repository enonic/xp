module app_wizard_form_input_type {

    export class TextArea extends BaseInputTypeView {

        constructor() {
            super("TextArea");
        }

        createInputOccurrenceElement(index:number, property?:api_data.Property):api_dom.Element {

            var inputEl = new api_ui.TextArea(this.getInput().getName() + "-" + index);
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            return inputEl;
        }

        getValue(occurrence:api_dom.Element):api_data.Value {
            var inputEl = <api_ui.TextArea>occurrence;
            return new api_data.Value(inputEl.getValue(), api_data.ValueTypes.TEXT);
        }
    }

    app_wizard_form_input.InputTypeManager.register("TextArea", TextArea);
}