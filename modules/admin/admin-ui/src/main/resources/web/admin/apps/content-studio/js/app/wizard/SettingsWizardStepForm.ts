import "../../api.ts";
import {ContentSettingsModel} from "./ContentSettingsModel";

import Content = api.content.Content;
import PrincipalType = api.security.PrincipalType;
import PrincipalLoader = api.security.PrincipalLoader;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import FormItem = api.ui.form.FormItem;
import PrincipalComboBox = api.ui.security.PrincipalComboBox;
import LocaleComboBox = api.ui.locale.LocaleComboBox;
import LocalDateTimeFormInput = api.form.LocalDateTimeFormInput;
import ValidationRecording = api.form.ValidationRecording;

export class SettingsWizardStepForm extends api.app.wizard.WizardStepForm {

    private content: Content;
    private model: ContentSettingsModel;
    private modelChangeListener: (event: api.PropertyChangedEvent) => void;
    private updateUnchangedOnly: boolean = false;
    private ignorePropertyChange: boolean = false;

    private localeCombo: LocaleComboBox;
    private ownerCombo: PrincipalComboBox;

    private publishFromInput: LocalDateTimeFormInput;
    private publishToInput: LocalDateTimeFormInput;

    private publishFromInputFormItem: FormItem;
    private publishToInputFormItem: FormItem;

    private formValid: boolean = true;

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
                    if (!this.updateUnchangedOnly || !this.publishFromInput.isDirty()) {
                        this.publishFromInput.getPicker().setSelectedDateTime(value);
                    }
                    break;
                case ContentSettingsModel.PROPERTY_PUBLISH_TO:
                    if (!this.updateUnchangedOnly || !this.publishToInput.isDirty()) {
                        this.publishToInput.getPicker().setSelectedDateTime(value);
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

        this.publishFromInput = new LocalDateTimeFormInput(this.content.getPublishFromTime());
        this.publishFromInputFormItem = new FormItemBuilder(this.publishFromInput).setLabel('Publish From').build();
        this.publishFromInputFormItem.addClass("publishFrom");

        this.publishFromInput.getPicker().onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
            this.checkValidityAndNotify();
        });

        this.publishToInput = new LocalDateTimeFormInput(this.content.getPublishToTime());
        this.publishToInputFormItem = new FormItemBuilder(this.publishToInput).setLabel('Publish To').build();
        this.publishToInputFormItem.addClass("publishTo");

        this.publishToInput.getPicker().onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
            this.checkValidityAndNotify();
        });


        var fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(localeFormItem);
        fieldSet.add(ownerFormItem);
        fieldSet.add(this.publishFromInputFormItem);
        fieldSet.add(this.publishToInputFormItem);

        var form = new api.ui.form.Form(api.form.FormView.VALIDATION_CLASS).add(fieldSet);

        this.appendChild(form);

        form.onFocus((event) => {
            this.notifyFocused(event);
        });
        form.onBlur((event) => {
            this.notifyBlurred(event);
        });
        form.onValidityChanged((event: api.ValidityChangedEvent) => {
            this.checkValidityAndNotify();
        });

        this.setModel(new ContentSettingsModel(content));
    }

    isValid(): boolean {
        return this.checkValidity();
    }

    private checkValidityAndNotify() {
        var prevValidityState = this.formValid,
            currentValidityState = this.checkValidity();

        if (prevValidityState != currentValidityState) {
            this.notifyValidityChanged(new api.app.wizard.WizardStepValidityChangedEvent(currentValidityState));
        }
    }

    private checkValidity(): boolean {
        var fromDate = this.publishFromInput.getPicker().getSelectedDateTime(),
            toDate = this.publishToInput.getPicker().getSelectedDateTime(),
            isValid = this.publishFromInput.getPicker().isValid() && this.publishToInput.getPicker().isValid();

        // check toDate is before fromDate
        if (fromDate && this.publishFromInput.getPicker().isValid() && toDate && this.publishToInput.getPicker().isValid() &&
            toDate < fromDate) {
            isValid = false;
            this.publishToInputFormItem.addClass("before-start");
        } else {
            this.publishToInputFormItem.removeClass("before-start");
        }

        if (!fromDate && toDate) { // only toDate is set
            isValid = false;
            this.publishFromInputFormItem.addClass("missing");
        } else {
            this.publishFromInputFormItem.removeClass("missing");
        }

        return this.formValid = isValid;
    }

    update(content: api.content.Content, unchangedOnly: boolean = true) {
        this.updateUnchangedOnly = unchangedOnly;

        this.model.setOwner(content.getOwner()).
            setLanguage(content.getLanguage()).
            setPublishFrom(content.getPublishFromTime()).
            setPublishTo(content.getPublishToTime());
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

        this.publishFromInput.getPicker().
            onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
                this.ignorePropertyChange = true;
                model.setPublishFrom(event.getDate());
                this.ignorePropertyChange = false;
                ;
            });

        this.publishToInput.getPicker().
            onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
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
