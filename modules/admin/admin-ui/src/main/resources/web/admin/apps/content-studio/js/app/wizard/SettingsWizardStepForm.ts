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
        private modelChangeListener: (event: api.PropertyChangedEvent) => void;
        private updateUnchangedOnly: boolean = false;
        private ignorePropertyChange: boolean = false;

        private localeCombo: LocaleComboBox;
        private ownerCombo: PrincipalComboBox;

        constructor() {
            super("settings-wizard-step-form");

            this.modelChangeListener = (event: api.PropertyChangedEvent) => {
                if (!this.ignorePropertyChange) {
                    var value = event.getNewValue();
                    switch (event.getPropertyName()) {
                    case ContentSettingsModel.PROPERTY_LANG:
                        if (!this.updateUnchangedOnly || !this.localeCombo.isDirty()) {
                            this.localeCombo.setValue(value ? value.toString() : "");
                        }
                        break;
                    case ContentSettingsModel.PROPERTY_OWNER:
                        if (!this.updateUnchangedOnly || !this.ownerCombo.isDirty()) {
                            this.ownerCombo.setValue(value ? value.toString() : "");
                        }
                        break;
                    }
                }
            }
        }

        layout(content: api.content.Content) {
            this.content = content;

            this.localeCombo = new LocaleComboBox(1, content.getLanguage());
            var localeFormItem = new FormItemBuilder(this.localeCombo).
                setLabel('Language').
                build();

            var loader = new PrincipalLoader().setAllowedTypes([PrincipalType.USER]);
            this.ownerCombo = new PrincipalComboBox(loader, 1, content.getOwner() ? content.getOwner().toString() : undefined);
            var ownerFormItem = new FormItemBuilder(this.ownerCombo).
                setLabel('Owner').
                build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(localeFormItem);
            fieldSet.add(ownerFormItem);

            var form = new api.ui.form.Form().add(fieldSet);

            this.appendChild(form);

            form.onFocus((event) => {
                this.notifyFocused(event);
            });
            form.onBlur((event) => {
                this.notifyBlurred(event);
            });

            this.setModel(new ContentSettingsModel(content));
        }

        update(content: api.content.Content, unchangedOnly: boolean = true) {
            this.updateUnchangedOnly = unchangedOnly;

            this.model.setOwner(content.getOwner()).setLanguage(content.getLanguage());
        }

        private setModel(model: ContentSettingsModel) {
            api.util.assertNotNull(model, "Model can't be null");

            if (this.model) {
                model.unPropertyChanged(this.modelChangeListener);
            }

            // 2-way data binding
            var ownerListener = () => {
                var principals: api.security.Principal[] = this.ownerCombo.getSelectedDisplayValues();
                this.ignorePropertyChange = true;
                model.setOwner(principals.length > 0 ? principals[0].getKey() : null);
                this.ignorePropertyChange = false;
            };
            this.ownerCombo.onOptionSelected((event) => ownerListener());
            this.ownerCombo.onOptionDeselected((option) => ownerListener());

            var localeListener = () => {
                this.ignorePropertyChange = true;
                model.setLanguage(this.localeCombo.getValue());
                this.ignorePropertyChange = false;
            };
            this.localeCombo.onOptionSelected((event) => localeListener());
            this.localeCombo.onOptionDeselected((option) => localeListener());

            model.onPropertyChanged(this.modelChangeListener);

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
