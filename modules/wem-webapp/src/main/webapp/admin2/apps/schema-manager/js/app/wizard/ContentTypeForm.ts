module app_wizard {

    export class ContentTypeForm extends api_ui.Form {

        constructor() {
            super("ContentTypeForm");

            var fieldset = new api_ui.Fieldset(this, "Config");


            var textArea = new api_ui.CodeArea("xml");
            textArea.setSize(api_ui.TextAreaSize.LARGE);
            textArea.setLineNumbers(true);

            var text = new api_ui.TextInput();
            text.setName("test");

            fieldset.add(new api_ui.FormItem("Test", text));
            fieldset.add(new api_ui.FormItem("XML", textArea));

            this.fieldset(fieldset);

        }
    }
}