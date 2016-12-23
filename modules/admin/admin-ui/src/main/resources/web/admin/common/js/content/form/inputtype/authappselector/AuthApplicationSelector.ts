module api.content.site.inputtype.authappselector {

    import PropertyTree = api.data.PropertyTree;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import PropertySet = api.data.PropertySet;
    import FormView = api.form.FormView;
    import FormValidityChangedEvent = api.form.FormValidityChangedEvent;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import InputOccurrences = api.form.inputtype.support.InputOccurrences;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig;
    import GetApplicationRequest = api.application.GetApplicationRequest;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;
    import SiteConfigProvider = api.content.site.inputtype.siteconfigurator.SiteConfigProvider;

    export class AuthApplicationSelector extends api.form.inputtype.support.BaseInputTypeManagingAdd<Application> {

        private context: api.form.inputtype.InputTypeViewContext;

        private comboBox: AuthApplicationComboBox;

        private siteConfigProvider: SiteConfigProvider;

        private formContext: api.content.form.ContentFormContext;

        private readOnly: boolean;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super("site-configurator");
            this.context = config;
            this.readConfig(config.inputConfig);
            this.formContext = config.formContext;
        }

        getValueType(): ValueType {
            return ValueTypes.DATA;
        }

        newInitialValue(): Value {
            return null;
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            var readOnlyConfig = inputConfig['readOnly'] && inputConfig['readOnly'][0];
            var readOnlyValue = readOnlyConfig && readOnlyConfig['value'];
            this.readOnly = readOnlyValue === "true";
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            super.layout(input, propertyArray);

            this.siteConfigProvider = new SiteConfigProvider(propertyArray);
            this.comboBox = this.createComboBox(input, this.siteConfigProvider);

            this.appendChild(this.comboBox);

            this.setLayoutInProgress(false);

            return wemQ<void>(null);
        }


        update(propertyArray: api.data.PropertyArray, unchangedOnly?: boolean): Q.Promise<void> {
            var superPromise = super.update(propertyArray, unchangedOnly);
            this.siteConfigProvider.setPropertyArray(propertyArray);

            this.siteConfigProvider.setPropertyArray(propertyArray);

            if (!unchangedOnly || !this.comboBox.isDirty()) {
                return superPromise.then(() => {
                    this.comboBox.setValue(this.getValueFromPropertyArray(propertyArray));
                });
            } else {
                return superPromise;
            }
        }

        reset() {
            this.comboBox.resetBaseValues();
        }


        private saveToSet(siteConfig: SiteConfig, index: number) {

            var propertySet = this.getPropertyArray().get(index).getPropertySet();
            if (!propertySet) {
                propertySet = this.getPropertyArray().addSet();
            }

            var config = siteConfig.getConfig();
            var appKey = siteConfig.getApplicationKey();

            propertySet.setStringByPath('applicationKey', appKey.toString());
            propertySet.setPropertySetByPath('config', config);
        }

        protected getValueFromPropertyArray(propertyArray: api.data.PropertyArray): string {
            return propertyArray.getProperties().map((property) => {
                if (property.hasNonNullValue()) {
                    var siteConfig = SiteConfig.create().fromData(property.getPropertySet()).build();
                    return siteConfig.getApplicationKey().toString();
                }
            }).join(';');
        }

        private createComboBox(input: api.form.Input, siteConfigProvider: SiteConfigProvider): AuthApplicationComboBox {

            var value = this.getValueFromPropertyArray(this.getPropertyArray());
            var siteConfigFormsToDisplay = value.split(';');
            var comboBox = new AuthApplicationComboBox(input.getOccurrences().getMaximum() || 0, siteConfigProvider,
                this.formContext, value, this.readOnly);

            // creating selected option might involve property changes
            comboBox.onBeforeOptionCreated(() => this.ignorePropertyChange = true);
            comboBox.onAfterOptionCreated(() => this.ignorePropertyChange = false);

            const forcedValidate = () => {
                this.ignorePropertyChange = false;
                this.validate(false);
            };
            const saveAndForceValidate = (selectedOption: SelectedOption<Application>) => {
                const view: AuthApplicationSelectedOptionView = <AuthApplicationSelectedOptionView> selectedOption.getOptionView();
                this.saveToSet(view.getSiteConfig(), selectedOption.getIndex());
                forcedValidate();
            };

            comboBox.onOptionDeselected((event: SelectedOptionEvent<Application>) => {
                this.ignorePropertyChange = true;

                this.getPropertyArray().remove(event.getSelectedOption().getIndex());

                forcedValidate();
            });

            comboBox.onOptionSelected((event: SelectedOptionEvent<Application>) => {
                this.fireFocusSwitchEvent(event);

                this.ignorePropertyChange = true;

                const selectedOption = event.getSelectedOption();
                const key = selectedOption.getOption().displayValue.getApplicationKey();
                if (key) {
                    saveAndForceValidate(selectedOption);
                }
            });

            comboBox.onOptionMoved((selectedOption: SelectedOption<Application>) => {
                this.ignorePropertyChange = true;
                saveAndForceValidate(selectedOption);
            });

            comboBox.onSiteConfigFormDisplayed((applicationKey: ApplicationKey, formView: FormView) => {
                var indexToRemove = siteConfigFormsToDisplay.indexOf(applicationKey.toString());
                if (indexToRemove != -1) {
                    siteConfigFormsToDisplay.splice(indexToRemove, 1);
                }

                formView.onValidityChanged((event: FormValidityChangedEvent) => {
                    this.validate(false);
                });

                this.validate(false);
            });

            return comboBox;
        }

        displayValidationErrors(value: boolean) {
            this.comboBox.getSelectedOptionViews().forEach((view: AuthApplicationSelectedOptionView) => {
                view.getFormView().displayValidationErrors(value);
            });
        }

        protected getNumberOfValids(): number {
            return this.comboBox.countSelected();
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            var recording = new api.form.inputtype.InputValidationRecording();

            this.comboBox.getSelectedOptionViews().forEach((view: AuthApplicationSelectedOptionView) => {

                var validationRecording = view.getFormView().validate(true);
                if (!validationRecording.isMinimumOccurrencesValid()) {
                    recording.setBreaksMinimumOccurrences(true);
                }
                if (!validationRecording.isMaximumOccurrencesValid()) {
                    recording.setBreaksMaximumOccurrences(true);
                }
            });

            return super.validate(silent, recording);
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("AuthApplicationSelector", AuthApplicationSelector));
}