module api.content.site.inputtype.moduleconfigurator {

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
    import Module = api.module.Module;
    import ModuleKey = api.module.ModuleKey;
    import ModuleConfig = api.content.site.ModuleConfig
    import GetModuleRequest = api.module.GetModuleRequest;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class ModuleConfigurator extends api.form.inputtype.support.BaseInputTypeManagingAdd<ModuleView> {

        private context: api.form.inputtype.InputTypeViewContext<any>;

        private input: api.form.Input;

        private propertyArray: PropertyArray;

        private comboBox: ModuleConfiguratorComboBox;

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        private _displayValidationErrors: boolean;

        private formContext: api.content.form.ContentFormContext;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext<any>) {
            super("module-configurator");
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

            this.input = input;
            this.propertyArray = propertyArray;

            var moduleConfigProvider = new ModuleConfigProvider(propertyArray);
            this.comboBox = this.createComboBox(input, moduleConfigProvider);

            this.appendChild(this.comboBox);

            return this.doLoadModules(propertyArray).then(() => {

                this.comboBox.onOptionDeselected((removed: SelectedOption<Module>) => {
                    this.propertyArray.remove(removed.getIndex());
                    this.validate(false);
                });

                this.comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<Module>) => {

                    var key = event.getOption().displayValue.getModuleKey();
                    if (!key) {
                        return;
                    }

                    var selectedOption = this.comboBox.getSelectedOption(event.getOption());
                    var selectedOptionView: ModuleConfiguratorSelectedOptionView = <ModuleConfiguratorSelectedOptionView>selectedOption.getOptionView();
                    var moduleConfig = selectedOptionView.getModuleConfig();


                    //var moduleConfigAsData = moduleConfig.toPropertySet(this.propertyArray.newSet());
                    //var newValue = new Value(moduleConfigAsData, ValueTypes.DATA);

                    //var newValue = new Value(moduleConfig.getConfig(), ValueTypes.DATA);

                    /*if (this.comboBox.countSelected() == 1) { // overwrite initial value
                        this.propertyArray.set(0, newValue);
                    }
                    else {
                        this.propertyArray.add(newValue);
                     }*/

                    this.validate(false);
                });
            });
        }

        private doLoadModules(propertyArray: PropertyArray): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            var moduleConfigFormsToDisplay: string[] = [];

            if (propertyArray.getSize() == 0) {
                deferred.resolve(null);
            } else {
                propertyArray.forEach((property: Property) => {

                    if (property.hasNonNullValue()) {
                        var moduleConfig = ModuleConfig.create().fromData(property.getSet()).build();
                        moduleConfigFormsToDisplay.push(moduleConfig.getModuleKey().toString());

                        new GetModuleRequest(moduleConfig.getModuleKey()).sendAndParse().
                            then((requestedModule: Module) => {

                                this.comboBox.onModuleConfigFormDisplayed((moduleKey: ModuleKey, formView: FormView) => {
                                    var indexToRemove = moduleConfigFormsToDisplay.indexOf(moduleKey.toString());
                                    if (indexToRemove != -1) {
                                        moduleConfigFormsToDisplay.splice(indexToRemove, 1);
                                    }
                                    if (moduleConfigFormsToDisplay.length == 0) {
                                        deferred.resolve(null);
                                    }

                                    formView.onValidityChanged((event: FormValidityChangedEvent) => {
                                        this.validate(false);
                                    });
                                });
                                this.comboBox.select(requestedModule);
                            });
                    }
                });
            }
            return deferred.promise;
        }

        private createComboBox(input: api.form.Input, moduleConfigProvider: ModuleConfigProvider): ModuleConfiguratorComboBox {

            return new ModuleConfiguratorComboBox(input.getOccurrences().getMaximum() || 0, moduleConfigProvider, this.formContext);
        }

        availableSizeChanged() {
        }

        displayValidationErrors(value: boolean) {
            this._displayValidationErrors = value;
            this.comboBox.getSelectedOptionViews().forEach((view: ModuleConfiguratorSelectedOptionView) => {
                view.getFormView().displayValidationErrors(value);
            });
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            var recording = new api.form.inputtype.InputValidationRecording();

            // check the number of occurrences
            var numberOfValids = this.comboBox.countSelected();
            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            this.comboBox.getSelectedOptionViews().forEach((view: ModuleConfiguratorSelectedOptionView) => {

                var validationRecording = view.getFormView().validate(silent);
                if (!validationRecording.isMinimumOccurrencesValid()) {
                    recording.setBreaksMinimumOccurrences(true);
                }
                if (!validationRecording.isMaximumOccurrencesValid()) {
                    recording.setBreaksMaximumOccurrences(true);
                }
            });

            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
                }
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ModuleConfigurator", ModuleConfigurator));
}