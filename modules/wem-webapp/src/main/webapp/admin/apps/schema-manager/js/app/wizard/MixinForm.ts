module app_wizard {

    export class MixinForm extends api_ui_form.Form {

        constructor() {
            super("MixinForm");

            var fieldset = new api_ui_form.Fieldset(this,"Config");
            this.fieldset(fieldset);

            var xmlTextArea:api_ui.CodeArea = new api_ui.CodeAreaBuilder().
                setName("xml").
                setMode("xml").
                setLineNumbers(true).
                setSize(api_ui.TextAreaSize.LARGE).
                build();

            fieldset.add(new api_ui_form.FormItem("XML", xmlTextArea));

        }
    }

}