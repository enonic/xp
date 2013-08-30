module app_wizard_form_input {

    export class TextLine extends BaseInput implements Input {

        constructor() {
            super("TextLine");
        }

        createInputEl(index:number, property?:api_data.Property):api_dom.FormInputEl {
            var inputEl = api_ui.TextInput.middle(this.getInput().getName() + "-" + index);
            inputEl.setName(this.getInput().getName());
            if (property != null) {
                inputEl.setValue(property.getValue());
            }
            return inputEl;
        }
    }
}