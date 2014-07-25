module app.wizard {

    export class ContentTypeForm extends api.app.wizard.WizardStepForm {

        constructor() {
            super();

            var fieldset = new api.ui.form.Fieldset("Config");

            var xmlTextArea: api.ui.text.CodeArea = new api.ui.text.CodeAreaBuilder().
                setName("xml").
                setMode("xml").
                setLineNumbers(true).
                build();

            var text = api.ui.text.TextInput.middle();
            text.setName("test");

            fieldset.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(xmlTextArea).setLabel("XML")));

            this.add(fieldset);

        }
    }
}