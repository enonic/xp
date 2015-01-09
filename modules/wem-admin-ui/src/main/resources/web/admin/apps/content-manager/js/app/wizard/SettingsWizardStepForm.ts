module app.wizard {

    import Content = api.content.Content;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import FormItem = api.ui.form.FormItem;
    import PrincipalComboBox = api.ui.security.PrincipalComboBox;

    export class SettingsWizardStepForm extends api.app.wizard.WizardStepForm {

        private content: Content;
        private languageFormItem: FormItem;
        private ownerFormItem: FormItem;

        constructor() {
            super("settings-wizard-step-form");

            // this.languageFormItem = new FormItemBuilder(new LanguageComboBox()).setLabel('Language').build();

            this.ownerFormItem = new FormItemBuilder(new PrincipalComboBox()).setLabel('Owner').build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(this.ownerFormItem);

            var form = new api.ui.form.Form().add(fieldSet);

            this.appendChild(form);
        }

        layout(content: api.content.Content) {
            var owner = content.getOwner();
            if (owner) {
                this.ownerFormItem.getInput().setValue(owner);
            }

            this.content = content;
        }

        giveFocus(): boolean {
            return false;
        }

    }
}
