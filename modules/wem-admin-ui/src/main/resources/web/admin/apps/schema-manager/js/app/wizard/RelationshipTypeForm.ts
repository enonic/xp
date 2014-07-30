module app.wizard {

    export class RelationshipTypeForm extends api.app.wizard.WizardStepForm {

        constructor() {
            super();

            var fieldset = new api.ui.form.Fieldset("Config");
            this.add(fieldset);

            var xmlTextArea: api.ui.text.CodeArea = new api.ui.text.CodeAreaBuilder().
                setName("xml").
                setMode("xml").
                setLineNumbers(true).
                build();

            fieldset.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(xmlTextArea).setLabel("XML")));
        }

    }

}