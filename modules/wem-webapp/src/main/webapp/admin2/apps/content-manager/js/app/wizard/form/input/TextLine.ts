module app_wizard_form_input {

    export class TextLine extends BaseInput implements Input {

        private input:api_schema_content_form.Input;

        constructor() {
            super("TextLine");
        }

        createInputEl(index:number, property?:api_content_data.Property):api_dom.FormInputEl {
            var inputEl = new api_ui.TextInput(this.input.getName() + "-" + index);
            inputEl.setName(this.input.getName());
            if (property != null) {
                inputEl.setValue(property.getValue());
            }
            return inputEl;
        }
    }
}