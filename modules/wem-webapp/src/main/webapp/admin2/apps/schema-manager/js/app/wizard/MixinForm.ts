module app_wizard {

    export class MixinForm extends api_ui_form.Form {

        constructor() {
            super("MixinForm");

            var fieldset = new api_ui_form.Fieldset(this,"Config");
            this.fieldset(fieldset);

            var xmlTextArea = new api_ui.CodeArea("xml");
            xmlTextArea.setSize(api_ui.TextAreaSize.LARGE);
            xmlTextArea.setLineNumbers(true);

            fieldset.add(new api_ui_form.FormItem("XML", xmlTextArea));

        }
    }

}