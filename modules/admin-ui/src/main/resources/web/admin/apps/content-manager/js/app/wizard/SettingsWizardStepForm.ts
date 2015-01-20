module app.wizard {

    import Content = api.content.Content;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalLoader = api.security.PrincipalLoader;
    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import PrincipalComboBox = api.ui.security.PrincipalComboBox;
    import LocaleComboBox = api.ui.locale.LocaleComboBox;

    export class SettingsWizardStepForm extends api.app.wizard.WizardStepForm {

        private content: Content;
        private model: ContentSettingsModel;

        private localeCombo: LocaleComboBox;
        private ownerCombo: PrincipalComboBox;

        constructor() {
            super("settings-wizard-step-form");

            this.localeCombo = new LocaleComboBox(1);
            var localeFormItem = new FormItemBuilder(this.localeCombo).
                setLabel('Language').
                setValidator(Validators.required).
                build();

            var loader = new PrincipalLoader().setAllowedTypes([PrincipalType.USER]);
            this.ownerCombo = new PrincipalComboBox(loader, 1);
            var ownerFormItem = new FormItemBuilder(this.ownerCombo).
                setLabel('Owner').
                setValidator(Validators.required).
                build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(localeFormItem);
            fieldSet.add(ownerFormItem);

            var form = new api.ui.form.Form().add(fieldSet);

            this.appendChild(form);
        }

        layout(content: api.content.Content) {

            this.content = content;
        }

        setModel(model: ContentSettingsModel) {
            api.util.assertNotNull(model, "Model can't be null");

            if (model.getOwner()) {
                this.ownerCombo.setValue(model.getOwner().toString());
            }
            if (model.getLanguage()) {
                this.localeCombo.setValue(model.getLanguage())
            }

            // 2-way data binding
            var ownerListener = () => {
                var principals: api.security.Principal[] = this.ownerCombo.getSelectedDisplayValues();
                model.setOwner(principals.length > 0 ? principals[0].getKey() : null, true);
            };
            this.ownerCombo.onOptionSelected((event) => ownerListener());
            this.ownerCombo.onOptionDeselected((option) => ownerListener());

            var localeListener = () => {
                model.setLanguage(this.localeCombo.getValue(), true);
            };
            this.localeCombo.onOptionSelected((event) => localeListener());
            this.localeCombo.onOptionDeselected((option) => localeListener());

            model.onPropertyChanged((event: api.PropertyChangedEvent) => {
                switch (event.getPropertyName()) {
                case ContentSettingsModel.PROPERTY_LANG:
                    this.localeCombo.setValue(event.getNewValue());
                    break;
                case ContentSettingsModel.PROPERTY_OWNER:
                    this.ownerCombo.setValue(event.getNewValue());
                    break;
                }
            });

            this.model = model;
        }

        getModel(): ContentSettingsModel {
            return this.model;
        }

        giveFocus(): boolean {
            return this.ownerCombo.giveFocus();
        }

    }
}
