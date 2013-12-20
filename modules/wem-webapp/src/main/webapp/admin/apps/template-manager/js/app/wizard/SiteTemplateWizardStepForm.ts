module app_wizard
{

    export class SiteTemplateWizardStepForm extends api_app_wizard.WizardStepForm
    {

        private descriptionField:api_ui.TextInput;
        private moduleComboBox:api_module.ModuleComboBox;
        private rootContentComboBox:api_content.ContentComboBox;

        constructor()
        {
            super( "site-template-wizard-step" );
            this.descriptionField = api_ui.TextInput.large("description" ).setName("description");
            this.moduleComboBox = new api_module.ModuleComboBox();
            this.rootContentComboBox = new api_content.ContentComboBox(false);
            this.registerInput(this.descriptionField);
            this.registerInput(this.moduleComboBox);
            this.registerInput(this.rootContentComboBox);
        }

        renderNew()
        {
            var fieldSet = new api_ui_form.Fieldset(this, "Site Template");
            fieldSet.add(new api_ui_form.FormItem("Description", this.descriptionField));
            fieldSet.add(new api_ui_form.FormItem("Modules", this.moduleComboBox));
            fieldSet.add(new api_ui_form.FormItem("Root Content", this.rootContentComboBox));


            this.fieldset(fieldSet);

        }

    }
}