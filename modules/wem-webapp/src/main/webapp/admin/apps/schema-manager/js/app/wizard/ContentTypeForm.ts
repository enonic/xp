module app_wizard {

    export class ContentTypeForm extends api_ui_form.Form {

        constructor() {
            super("ContentTypeForm");

            var fieldset = new api_ui_form.Fieldset(this, "Config");


            var textArea = new api_ui.CodeArea("xml", "xml");
            textArea.setSize(api_ui.TextAreaSize.LARGE);
            textArea.setLineNumbers(true);

            var text = api_ui.TextInput.middle();
            text.setName("test");

            fieldset.add(new api_ui_form.FormItem("Test", text));
            fieldset.add(new api_ui_form.FormItem("XML", textArea));

            this.fieldset(fieldset);

        }
    }
}