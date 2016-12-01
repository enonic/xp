module api.content.site.inputtype.siteconfigurator {

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
    import SiteConfig = api.content.site.SiteConfig
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;

    export class SiteConfigurator extends api.form.inputtype.support.BaseInputTypeManagingAdd<Application> {

        private context: api.form.inputtype.InputTypeViewContext;

        private readOnly: boolean;

        private comboBox: SiteConfiguratorComboBox;

        private siteConfigProvider: SiteConfigProvider;

        private formContext: api.content.form.ContentFormContext;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super("site-configurator");
            this.context = config;
            this.formContext = config.formContext;

            new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
                this.readOnly = !loginResult.getPrincipals().some(function (principal) {
                    return principal.equals(api.security.RoleKeys.ADMIN) || principal.equals(api.security.RoleKeys.CMS_ADMIN);
                });
                // request may finish after rendered has been done
                if (this.comboBox.isRendered()) {
                    this.comboBox.setReadOnly(this.readOnly);
                }
            });
        }

        getValueType(): ValueType {
            return ValueTypes.DATA;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            super.layout(input, propertyArray);

            var deferred = wemQ.defer<void>();

            this.siteConfigProvider = new SiteConfigProvider(propertyArray);
            // ignore changes made to property by siteConfigProvider
            this.siteConfigProvider.onBeforePropertyChanged(() => this.ignorePropertyChange = true);
            this.siteConfigProvider.onAfterPropertyChanged(() => this.ignorePropertyChange = false);

            this.comboBox = this.createComboBox(input, this.siteConfigProvider);
            this.comboBox.setReadOnly(this.readOnly);

            this.appendChild(this.comboBox);

            this.comboBox.render().then(() => {
                this.setLayoutInProgress(false);
                deferred.resolve(null);
            });
            return deferred.promise;
        }


        update(propertyArray: api.data.PropertyArray, unchangedOnly?: boolean): Q.Promise<void> {
            return super.update(propertyArray, unchangedOnly).then(() => {
                this.siteConfigProvider.setPropertyArray(propertyArray);

                if (!unchangedOnly || !this.comboBox.isDirty()) {
                    this.comboBox.setValue(this.getValueFromPropertyArray(propertyArray));
                }
                return null;
            });
        }

        reset() {
            this.comboBox.resetBaseValues();
        }


        private saveToSet(siteConfig: SiteConfig, index) {

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

        private createComboBox(input: api.form.Input, siteConfigProvider: SiteConfigProvider): SiteConfiguratorComboBox {

            var value = this.getValueFromPropertyArray(this.getPropertyArray());
            var siteConfigFormsToDisplay = value.split(';');
            var comboBox = new SiteConfiguratorComboBox(input.getOccurrences().getMaximum() || 0, siteConfigProvider, this.formContext,
                value);

            comboBox.onOptionDeselected((event: SelectedOptionEvent<Application>) => {
                this.ignorePropertyChange = true;

                this.getPropertyArray().remove(event.getSelectedOption().getIndex());

                this.ignorePropertyChange = false;
                this.validate(false);
            });

            comboBox.onOptionSelected((event: SelectedOptionEvent<Application>) => {
                this.fireFocusSwitchEvent(event);

                this.ignorePropertyChange = true;

                const selectedOption = event.getSelectedOption();
                var key = selectedOption.getOption().displayValue.getApplicationKey();
                if (!key) {
                    return;
                }
                var selectedOptionView: SiteConfiguratorSelectedOptionView = <SiteConfiguratorSelectedOptionView>selectedOption.getOptionView();
                this.saveToSet(selectedOptionView.getSiteConfig(), selectedOption.getIndex());

                this.ignorePropertyChange = false;
                this.validate(false);
            });

            comboBox.onOptionMoved((selectedOption: SelectedOption<Application>) => {
                this.ignorePropertyChange = true;

                var selectedOptionView: SiteConfiguratorSelectedOptionView = <SiteConfiguratorSelectedOptionView> selectedOption.getOptionView();
                this.saveToSet(selectedOptionView.getSiteConfig(), selectedOption.getIndex());

                this.ignorePropertyChange = false;
                this.validate(false);
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
            this.comboBox.getSelectedOptionViews().forEach((view: SiteConfiguratorSelectedOptionView) => {
                view.getFormView().displayValidationErrors(value);
            });
        }

        protected getNumberOfValids(): number {
            return this.comboBox.countSelected();
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            var recording = new api.form.inputtype.InputValidationRecording();

            this.comboBox.getSelectedOptionViews().forEach((view: SiteConfiguratorSelectedOptionView) => {

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

    api.form.inputtype.InputTypeManager.register(new api.Class("SiteConfigurator", SiteConfigurator));
}