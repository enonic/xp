module api.content.site.inputtype.siteconfigurator {

    import PropertyTree = api.data.PropertyTree;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
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
    import Application = api.module.Application;
    import ApplicationKey = api.module.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig
    import GetModuleRequest = api.module.GetModuleRequest;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class SiteConfigurator extends api.form.inputtype.support.BaseInputTypeManagingAdd<SiteView> {

        private context: api.form.inputtype.InputTypeViewContext<any>;

        private comboBox: SiteConfiguratorComboBox;

        private _displayValidationErrors: boolean;

        private formContext: api.content.form.ContentFormContext;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext<any>) {
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

            return this.doLoadModules(propertyArray).then(() => {

                this.setLayoutInProgress(false);

                this.comboBox.onOptionDeselected((removed: SelectedOption<Application>) => {
                    this.getPropertyArray().remove(removed.getIndex());
                    this.validate(false);
                });

                this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<Application>) => {

                    var key = event.getOption().displayValue.getApplicationKey();
                    if (!key) {
                        return;
                    }

                    var selectedOption = this.comboBox.getSelectedOption(event.getOption());
                    var selectedOptionView: SiteConfiguratorSelectedOptionView = <SiteConfiguratorSelectedOptionView>selectedOption.getOptionView();
                    var siteConfig = selectedOptionView.getSiteConfig();

                    this.validate(false);
                });
            });
        }

        private doLoadModules(propertyArray: PropertyArray): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            var siteConfigFormsToDisplay: string[] = [];

            if (propertyArray.getSize() == 0) {
                deferred.resolve(null);
            } else {
                propertyArray.forEach((property: Property) => {

                    if (property.hasNonNullValue()) {
                        var siteConfig = SiteConfig.create().fromData(property.getPropertySet()).build();
                        siteConfigFormsToDisplay.push(siteConfig.getApplicationKey().toString());

                        new GetModuleRequest(siteConfig.getApplicationKey()).sendAndParse().
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