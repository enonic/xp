module app.wizard {

    export class ContentTypeForm extends api.app.wizard.WizardStepForm {

        constructor() {
            super();

            var fieldset = new api.ui.form.Fieldset("Config");

            var xmlTextArea: api.ui.CodeArea = new api.ui.CodeAreaBuilder().
                setName("xml").
                setMode("xml").
                setLineNumbers(true).
                setSize(api.ui.TextAreaSize.LARGE).
                build();

            var text = api.ui.TextInput.middle();
            text.setName("test");

            fieldset.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(xmlTextArea).setLabel("XML")));

            this.add(fieldset);

        }
    }
}