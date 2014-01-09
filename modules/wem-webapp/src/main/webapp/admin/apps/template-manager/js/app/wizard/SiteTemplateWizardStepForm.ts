module app.wizard
{

    export class SiteTemplateWizardStepForm extends api.app.wizard.WizardStepForm
    {

        private descriptionField:api.ui.TextInput;
        private moduleComboBox:api.module.ModuleComboBox;
        private rootContentTypeComboBox:api.schema.content.ContentTypeComboBox;

        constructor()
        {
            super( "site-template-wizard-step" );
            this.descriptionField = api.ui.TextInput.large("description" ).setName("description");
            this.moduleComboBox = new api.module.ModuleComboBox();
            this.rootContentTypeComboBox = new api.schema.content.ContentTypeComboBox(false);

            this.registerInput(this.descriptionField);
            this.registerInput(this.moduleComboBox);
            this.registerInput(this.rootContentTypeComboBox);
        }

        renderNew()
        {
            var fieldSet = new api.ui.form.Fieldset(this, "Site Template");
            fieldSet.add(new api.ui.form.FormItem("Description", this.descriptionField));
            fieldSet.add(new api.ui.form.FormItem("Modules", this.moduleComboBox));
            fieldSet.add(new api.ui.form.FormItem("Root Content Type", this.rootContentTypeComboBox));


            this.fieldset(fieldSet);

        }

    }
}