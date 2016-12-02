import "../../api.ts";
import {ContentSettingsModel} from "./ContentSettingsModel";

import Content = api.content.Content;
import PrincipalType = api.security.PrincipalType;
import PrincipalLoader = api.security.PrincipalLoader;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import FormItem = api.ui.form.FormItem;
import Validators = api.ui.form.Validators;
import PrincipalComboBox = api.ui.security.PrincipalComboBox;
import LocaleComboBox = api.ui.locale.LocaleComboBox;
import LocalDateTimeFormInput = api.form.LocalDateTimeFormInput;

export class SettingsWizardStepForm extends api.app.wizard.WizardStepForm {

    private content: Content;
    private model: ContentSettingsModel;
    private modelChangeListener: (event: api.PropertyChangedEvent) => void;
    private updateUnchangedOnly: boolean = false;
    private ignorePropertyChange: boolean = false;

    private localeCombo: LocaleComboBox;
    private ownerCombo: PrincipalComboBox;

    private publishFromDate: LocalDateTimeFormInput;
    private publishToDate: LocalDateTimeFormInput;

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
                case ContentSettingsModel.PROPERTY_PUBLISH_FROM:
                    if (!this.updateUnchangedOnly || !this.publishFromDate.isDirty()) {
                        this.publishFromDate.setValue(value ? value.toString() : "");
                    }
                    break;
                case ContentSettingsModel.PROPERTY_PUBLISH_TO:
                    if (!this.updateUnchangedOnly || !this.publishToDate.isDirty()) {
                        this.publishToDate.setValue(value ? value.toString() : "");
                    }
                    break;
                }
            }
        }
    }

    layout(content: api.content.Content) {
        this.content = content;

        this.localeCombo = new LocaleComboBox(1, content.getLanguage());
        var localeFormItem = new FormItemBuilder(this.localeCombo).setLabel('Language').build();

        var loader = new PrincipalLoader().setAllowedTypes([PrincipalType.USER]);

        this.ownerCombo = PrincipalComboBox.create().setLoader(loader).setMaxOccurences(1).setValue(
            content.getOwner() ? content.getOwner().toString() : undefined).setDisplayMissing(true).build();

        var ownerFormItem = new FormItemBuilder(this.ownerCombo).setLabel('Owner').build();

        var fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(localeFormItem);
        fieldSet.add(ownerFormItem);
        this.initPublishDateInputs(fieldSet);

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

    private initPublishDateInputs(fieldSet: api.ui.form.Fieldset) {
        this.publishFromDate = new LocalDateTimeFormInput(this.content.getPublishFromDate());
        var publishFromDateFormItem = new FormItemBuilder(this.publishFromDate).setLabel('Publish From').build();

        this.publishToDate = new LocalDateTimeFormInput(this.content.getPublishToDate());
        var publishToDateFormItem = new FormItemBuilder(this.publishToDate).setLabel('Publish From').build();

        fieldSet.add(publishFromDateFormItem);
        fieldSet.add(publishToDateFormItem);
    }

    update(content: api.content.Content, unchangedOnly: boolean = true) {
        this.updateUnchangedOnly = unchangedOnly;

        this.model.setOwner(content.getOwner(), true).setLanguage(content.getLanguage(), true);
    }

    reset() {
        return this.localeCombo.resetBaseValues();
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

        this.publishFromDate.getPicker().onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
            this.ignorePropertyChange = true;
            model.setPublishFrom(event.getDate());
            this.ignorePropertyChange = false;
            ;
        });

        this.publishToDate.getPicker().onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
            this.ignorePropertyChange = true;
            model.setPublishTo(event.getDate());
            this.ignorePropertyChange = false;
            ;
        });

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
