module app_wizard_form_input {

    export class HtmlArea extends BaseInputTypeView {

        constructor() {
            super("HtmlArea");
        }

        createInputEl(index:number, property?:api_data.Property):api_dom.FormInputEl {
            var inputEl = new api_ui.TextArea(this.getInput().getName() + "-" + index);
            //inputEl.setName(this.input.getName());
            if (property != null) {
                inputEl.setValue(property.getValue());
            }
            return inputEl;
        }
    }
}