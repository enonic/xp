module api.module.inputtype.moduleconfigurator {

    import Value = api.data.Value;
    import Property = api.data.Property;
    import ValueType = api.data.type.ValueType;
    import ValueTypes = api.data.type.ValueTypes;
    import ValueChangedEvent = api.form.inputtype.support.ValueChangedEvent;
    import RootDataSet = api.data.RootDataSet;
    import InputOccurrences = api.form.inputtype.support.InputOccurrences;
    import InputOccurrencesConfig = api.form.inputtype.support.InputOccurrencesConfig;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import Option = api.ui.selector.Option;
    import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Module = api.module.Module;
    import ModuleKey = api.module.ModuleKey;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class ModuleConfigurator extends api.form.inputtype.support.BaseInputTypeManagingAdd<ModuleView> {

        private context: api.form.inputtype.InputTypeViewContext<any>;

        private input: api.form.Input;

        private comboBox: ModuleConfiguratorComboBox;

        private layoutInProgress: boolean;

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super("module-configurator");
            this.context = config;
        }

        getValueType(): ValueType {
            return ValueTypes.DATA;
        }

        newInitialValue(): ModuleView {
            return null;
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.layoutInProgress = true;
            this.input = input;

            var moduleConfigProvider = new ModuleConfigProvider(properties);
            this.comboBox = this.createComboBox(input, moduleConfigProvider);

            this.appendChild(this.comboBox);

            this.doLoadModules(properties).
                catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(()=> {
                    this.layoutInProgress = false;
                }).done();
        }

        private doLoadModules(properties: api.data.Property[]): wemQ.Promise<Module[]> {
            var promises: wemQ.Promise<Module>[] = [];
            properties.forEach((property: api.data.Property) => {
                var moduleKeyProperty = <Property> property.getData().getDataByName("moduleKey")[0];
                if (property.hasNonNullValue()) {
                    var promise = new GetModuleRequest(ModuleKey.fromString(moduleKeyProperty.getString())).sendAndParse();
                    promises.push(promise);
                    promise.then((requestedModule: Module) => {
                        this.comboBox.select(requestedModule);
                    }).done();
                }
            });
            return wemQ.all<Module>(promises);
        }

        private createComboBox(input: api.form.Input, moduleConfigProvider: ModuleConfigProvider): ModuleConfiguratorComboBox {

            var comboBox = new ModuleConfiguratorComboBox(input.getOccurrences().getMaximum() || 0, moduleConfigProvider);

            comboBox.onSelectedOptionRemoved((removed: SelectedOption<Module>) => {
                this.notifyValueRemoved(removed.getIndex());
                this.validate(false);
            });

            comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<Module>) => {
                if (!this.layoutInProgress) {
                    var key = event.getOption().displayValue.getModuleKey();
                    if (!key) {
                        return;
                    }
                    var newValue = this.createValue(key);

                    if (comboBox.countSelected() == 1) { // overwrite initial value
                        this.notifyValueChanged(new api.form.inputtype.ValueChangedEvent(newValue, 0));
                    }
                    else {
                        this.notifyValueAdded(newValue);
                    }
                }
                this.validate(false);
            });

            return comboBox;
        }

        availableSizeChanged() {
        }

        private createValue(moduleKey: ModuleKey, config?: RootDataSet): Value {
            var data = new RootDataSet();
            data.addProperty("moduleKey", new Value(moduleKey.getName(), ValueTypes.STRING));
            data.addProperty("config", new Value(config || new RootDataSet(), ValueTypes.DATA));
            return new Value(data, ValueTypes.DATA);
        }

        getValues(): api.data.Value[] {
            var options: SelectedOption<Module>[] = this.comboBox.getSelectedOptions();
            return options.map((selectedOption) => {
                var optionView = <any> selectedOption.getOptionView().getHTMLElement();
                return this.createValue(optionView.getModule().getModuleKey(), optionView.getFormView().getData());
            });
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            var recording = new api.form.inputtype.InputValidationRecording();

            // check the number of occurrencees
            var numberOfValids = this.comboBox.countSelected();
            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            // check


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