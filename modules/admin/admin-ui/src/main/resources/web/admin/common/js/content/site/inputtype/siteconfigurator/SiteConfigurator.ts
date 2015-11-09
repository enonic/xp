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
    import ValueChangedEvent = api.form.inputtype.support.ValueChangedEvent;
    import InputOccurrences = api.form.inputtype.support.InputOccurrences;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig
    import GetApplicationRequest = api.application.GetApplicationRequest;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class SiteConfigurator extends api.form.inputtype.support.BaseInputTypeManagingAdd<Application> {

        private context: api.form.inputtype.InputTypeViewContext;

        private comboBox: SiteConfiguratorComboBox;

        private _displayValidationErrors: boolean;

        private formContext: api.content.form.ContentFormContext;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super("site-configurator");
            this._displayValidationErrors = false;
            this.context = config;
            this.formContext = config.formContext;
        }

        getValueType(): ValueType {
            return ValueTypes.DATA;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            super.layout(input, propertyArray);

            var siteConfigProvider = new SiteConfigProvider(propertyArray);
            this.comboBox = this.createComboBox(input, siteConfigProvider);

            this.appendChild(this.comboBox);

            return this.doLoadApplications(propertyArray).then(() => {

                this.setLayoutInProgress(false);

                this.comboBox.onOptionDeselected((removed: SelectedOption<Application>) => {
                    this.getPropertyArray().remove(removed.getIndex());
                    this.validate(false);
                });

                this.comboBox.onOptionSelected((selectedOption: SelectedOption<Application>) => {

                    var key = selectedOption.getOption().displayValue.getApplicationKey();
                    if (!key) {
                        return;
                    }

                    var selectedOptionView: SiteConfiguratorSelectedOptionView = <SiteConfiguratorSelectedOptionView>selectedOption.getOptionView();

                    this.saveToSet(selectedOptionView.getSiteConfig(), this.getPropertyArray().addSet());

                    this.validate(false);
                });

                this.comboBox.onOptionMoved((selectedOption: SelectedOption<Application>) => {

                    var selectedOptionView: SiteConfiguratorSelectedOptionView = <SiteConfiguratorSelectedOptionView> selectedOption.getOptionView();
                    var propertySet = this.getPropertyArray().get(selectedOption.getIndex()).getPropertySet();

                    this.saveToSet(selectedOptionView.getSiteConfig(), propertySet);

                    this.validate(false);
                })
            });
        }

        private saveToSet(siteConfig: SiteConfig, propertySet: PropertySet) {
            var config = siteConfig.getConfig();
            var appKey = siteConfig.getApplicationKey();

            propertySet.setStringByPath('applicationKey', appKey.toString());
            propertySet.setPropertySetByPath('config', config);
        }

        private doLoadApplications(propertyArray: PropertyArray): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            var siteConfigFormsToDisplay: string[] = [];

            if (propertyArray.getSize() == 0) {
                deferred.resolve(null);
            } else {
                propertyArray.forEach((property: Property) => {

                    if (property.hasNonNullValue()) {
                        var siteConfig = SiteConfig.create().fromData(property.getPropertySet()).build();
                        siteConfigFormsToDisplay.push(siteConfig.getApplicationKey().toString());

                        new GetApplicationRequest(siteConfig.getApplicationKey()).sendAndParse().
                            then((requestedApplication: Application) => {

                                this.comboBox.onSiteConfigFormDisplayed((applicationKey: ApplicationKey, formView: FormView) => {
                                    var indexToRemove = siteConfigFormsToDisplay.indexOf(applicationKey.toString());
                                    if (indexToRemove != -1) {
                                        siteConfigFormsToDisplay.splice(indexToRemove, 1);
                                    }
                                    if (siteConfigFormsToDisplay.length == 0) {
                                        deferred.resolve(null);
                                    }

                                    formView.onValidityChanged((event: FormValidityChangedEvent) => {
                                        this.validate(false);
                                    });

                                    this.validate(false);
                                });
                                this.comboBox.select(requestedApplication);
                            });
                    }
                });
            }
            return deferred.promise;
        }

        private createComboBox(input: api.form.Input, siteConfigProvider: SiteConfigProvider): SiteConfiguratorComboBox {

            return new SiteConfiguratorComboBox(input.getOccurrences().getMaximum() || 0, siteConfigProvider, this.formContext);
        }

        availableSizeChanged() {
        }

        displayValidationErrors(value: boolean) {
            this._displayValidationErrors = value;
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