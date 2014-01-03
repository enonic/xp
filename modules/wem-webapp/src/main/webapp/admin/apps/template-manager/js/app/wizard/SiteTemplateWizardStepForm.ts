module app.wizard
{

    export class SiteTemplateWizardStepForm extends api.app.wizard.WizardStepForm
    {

        private descriptionField:api.ui.TextInput;
        private moduleComboBox:api.module.ModuleComboBox;
        private rootContentComboBox:api.content.ContentComboBox;

        constructor()
        {
            super( "site-template-wizard-step" );
            this.descriptionField = api.ui.TextInput.large("description" ).setName("description");
            this.moduleComboBox = new api.module.ModuleComboBox();
            this.rootContentComboBox = new api.content.ContentComboBox(false);
            this.registerInput(this.descriptionField);
            this.registerInput(this.moduleComboBox);
            this.registerInput(this.rootContentComboBox);
        }

        renderNew()
        {
            var fieldSet = new api.ui.form.Fieldset(this, "Site Template");
            fieldSet.add(new api.ui.form.FormItem("Description", this.descriptionField));
            fieldSet.add(new api.ui.form.FormItem("Modules", this.moduleComboBox));
            fieldSet.add(new api.ui.form.FormItem("Root Content", this.rootContentComboBox));


            this.fieldset(fieldSet);

        }

    }
}