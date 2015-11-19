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

        private localeCombo: LocaleComboBox;
        private ownerCombo: PrincipalComboBox;

        constructor() {
            super("settings-wizard-step-form");

            this.localeCombo = new LocaleComboBox(1);
            var localeFormItem = new FormItemBuilder(this.localeCombo).
                setLabel('Language').
                build();

            var loader = new PrincipalLoader().setAllowedTypes([PrincipalType.USER]);
            this.ownerCombo = new PrincipalComboBox(loader, 1);
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

            this.modelChangeListener = (event: api.PropertyChangedEvent) => {
                switch (event.getPropertyName()) {
                case ContentSettingsModel.PROPERTY_LANG:
                    if (!this.updateUnchangedOnly || !this.localeCombo.isDirty()) {
                        if (this.localeCombo.maximumOccurrencesReached()) {
                            this.localeCombo.clearSelection(true);
                        }
                        this.localeCombo.setValue(event.getNewValue());
                    }
                    break;
                case ContentSettingsModel.PROPERTY_OWNER:
                    if (!this.updateUnchangedOnly || !this.localeCombo.isDirty()) {
                        if (this.ownerCombo.maximumOccurrencesReached()) {
                            this.ownerCombo.clearSelection(true);
                        }
                        this.ownerCombo.setValue(event.getNewValue());
                    }
                    break;
                }
            }
        }

        layout(content: api.content.Content) {
            this.content = content;
            this.setModel(new ContentSettingsModel(content));
        }

        update(content: api.content.Content, unchangedOnly?: boolean) {
            this.updateUnchangedOnly = unchangedOnly;
            this.getModel().setOwner(content.getOwner()).setLanguage(content.getLanguage());
        }

        private setModel(model: ContentSettingsModel) {
            api.util.assertNotNull(model, "Model can't be null");

            if (this.model) {
                model.unPropertyChanged(this.modelChangeListener);
            }

            if (model.getOwner()) {
                this.ownerCombo.setIgnoreNextFocus().setValue(model.getOwner().toString());
            }
            if (model.getLanguage()) {
                this.localeCombo.setIgnoreNextFocus().setValue(model.getLanguage());
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
