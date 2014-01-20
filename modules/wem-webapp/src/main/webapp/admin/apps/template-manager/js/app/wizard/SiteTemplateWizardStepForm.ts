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
            fieldSet.add(new api.ui.form.FormItem("Description", this.descriptionField));
            fieldSet.add(new api.ui.form.FormItem("Modules", this.moduleComboBox).setValidator(this.notEmptyValidator));
            fieldSet.add(new api.ui.form.FormItem("Root Content Type", this.rootContentTypeComboBox).setValidator(this.notEmptyValidator));

            this.add(fieldSet);
        }


        notEmptyValidator(input: api.dom.FormInputEl): string {
            var value = input.getValue();
            return !value || value.length == 0 ? "Required field" : undefined;
        }

    }
}