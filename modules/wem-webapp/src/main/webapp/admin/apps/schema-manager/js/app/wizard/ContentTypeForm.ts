module app_wizard {

    export class ContentTypeForm extends api_ui_form.Form {

        constructor() {
            super("ContentTypeForm");

            var fieldset = new api_ui_form.Fieldset(this, "Config");

            var xmlTextArea:api_ui.CodeArea = new api_ui.CodeAreaBuilder().
                setName("xml").
                setMode("xml").
                setLineNumbers(true).
                setSize(api_ui.TextAreaSize.LARGE).
                build();

            var text = api_ui.TextInput.middle();
            text.setName("test");

            fieldset.add(new api_ui_form.FormItem("XML", xmlTextArea));

            this.fieldset(fieldset);

        }
    }
}