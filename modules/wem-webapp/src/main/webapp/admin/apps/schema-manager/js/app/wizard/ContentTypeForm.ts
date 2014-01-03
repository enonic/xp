module app.wizard {

    export class ContentTypeForm extends api.ui.form.Form {

        constructor() {
            super("ContentTypeForm");

            var fieldset = new api.ui.form.Fieldset(this, "Config");

            var xmlTextArea:api.ui.CodeArea = new api.ui.CodeAreaBuilder().
                setName("xml").
                setMode("xml").
                setLineNumbers(true).
                setSize(api.ui.TextAreaSize.LARGE).
                build();

            var text = api.ui.TextInput.middle();
            text.setName("test");

            fieldset.add(new api.ui.form.FormItem("XML", xmlTextArea));

            this.fieldset(fieldset);

        }
    }
}