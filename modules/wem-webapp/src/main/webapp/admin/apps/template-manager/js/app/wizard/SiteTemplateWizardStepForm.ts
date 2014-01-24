module app.wizard {

    export class SiteTemplateWizardStepForm extends api.app.wizard.WizardStepForm {

        private descriptionField: api.ui.TextInput;
        private moduleComboBox: api.module.ModuleComboBox;
        private rootContentTypeComboBox: api.schema.content.ContentTypeComboBox;

        constructor() {
            super();
            this.descriptionField = api.ui.TextInput.large().setName("description");
            this.moduleComboBox = new api.module.ModuleComboBox();
            this.rootContentTypeComboBox = new api.schema.content.ContentTypeComboBox(false);
        }

        renderNew() {
            var fieldSet = new api.ui.form.Fieldset("Site Template");
            fieldSet.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(this.descriptionField).
                setLabel("Description")));
            fieldSet.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(this.moduleComboBox).
                setLabel("Modules").
                setValidator(api.ui.form.Validators.notEmpty)));
            fieldSet.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(this.rootContentTypeComboBox).
                setLabel("Root Content Type").
                setValidator(api.ui.form.Validators.notEmpty)));

            this.add(fieldSet);
        }



    }
}