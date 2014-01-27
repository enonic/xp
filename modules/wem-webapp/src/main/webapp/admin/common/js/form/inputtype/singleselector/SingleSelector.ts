module api.form.inputtype.singleselector {

    import ValueChangedEvent = api.ui.ValueChangedEvent;

    export interface SingleSelectorConfig {
        selectorType: string;
        options: {
            label: string;
            value: string;
        }[]
    }

    export class SingleSelector extends api.form.inputtype.support.BaseInputTypeView {

        public static TYPE_DROPDOWN = "DROPDOWN";
        public static TYPE_RADIO = "RADIO";
        public static TYPE_COMBOBOX = "COMBOBOX";

        private config:SingleSelectorConfig;

        constructor(config:api.form.inputtype.InputTypeViewConfig<SingleSelectorConfig>) {
            super("single-selector");
            this.config = config.inputConfig;
        }

        createInputOccurrenceElement(index:number, property:api.data.Property):api.dom.Element {

            var type = this.config && this.config.selectorType && this.config.selectorType.toUpperCase();
            var name = this.getInput().getName() + "-" + index;
            if (SingleSelector.TYPE_RADIO == type) {
                return this.createRadioElement(name, property);
            }
            else if (SingleSelector.TYPE_COMBOBOX == type) {
                return this.createComboBoxElement(name, property);
            }
            else if (SingleSelector.TYPE_DROPDOWN == type) {
                return this.createDropdownElement(name, property);
            }
            else {
                throw new Error("Unsupported type of SingleSelector: " + type);
            }
        }

        private createComboBoxElement(name:string, property:api.data.Property):api.dom.Element {

            var selectedOptionsView = new api.ui.combobox.SelectedOptionsView<string>();
            var comboBox = new api.ui.combobox.ComboBox<string>(name, {
                rowHeight: 24,
                filter: this.comboboxFilter,
                selectedOptionsView: selectedOptionsView,
                maximumOccurrences: 1,
                hideComboBoxWhenMaxReached: true
            });
            comboBox.addListener({
                onInputValueChanged: function (oldValue, newValue, grid) {
                    grid.getDataView().setFilterArgs({searchString: newValue});
                    grid.getDataView().refresh();
                },
                onOptionSelected: () => {
                    var validationRecorder:api.form.ValidationRecorder = new api.form.ValidationRecorder();
                    this.validate(validationRecorder);
                    if (this.validityChanged(validationRecorder)) {
                        this.notifyValidityChanged(new support.ValidityChangedEvent(validationRecorder.valid()));
                    }
                }
            });

            if (this.config) {
                var option;
                for (var i = 0; i < this.config.options.length; i++) {
                    option = this.config.options[i];
                    comboBox.addOption({ value: option.value, displayValue: option.label});
                }
            }

            if (property) {
                comboBox.setValue(property.getString());
            }

            return comboBox;
        }

        private createDropdownElement(name:string, property:api.data.Property):api.dom.Element {

            var inputEl = new api.ui.Dropdown(name);

            if (this.config) {
                for (var i = 0; i < this.config.options.length; i++) {
                    var option = this.config.options[i];
                    inputEl.addOption(option.value, option.label);
                }
            }

            if (property) {
                inputEl.setValue(property.getString());
            }

            inputEl.onValueChanged((event:ValueChangedEvent) => {
                var validationRecorder:api.form.ValidationRecorder = new api.form.ValidationRecorder();
                this.validate(validationRecorder);
                if (this.validityChanged(validationRecorder)) {
                    this.notifyValidityChanged(new support.ValidityChangedEvent(validationRecorder.valid()));
                }});
            return inputEl;
        }


        private createRadioElement(name:string, property:api.data.Property):api.dom.Element {

            var inputEl = new api.ui.RadioGroup(name);

            if (this.config) {
                for (var i = 0; i < this.config.options.length; i++) {
                    var option = this.config.options[i];
                    inputEl.addOption(option.value, option.label);
                }
            }

            if (property) {
                inputEl.setValue(property.getString());
            }

            inputEl.onValueChanged((event:ValueChangedEvent) => {
                var validationRecorder:api.form.ValidationRecorder = new api.form.ValidationRecorder();
                this.validate(validationRecorder);
                if (this.validityChanged(validationRecorder)) {
                    this.notifyValidityChanged(new support.ValidityChangedEvent(validationRecorder.valid()));
                }
            })

            return inputEl;
        }

        getValue(occurrence:api.dom.Element):api.data.Value {
            var inputEl = <api.dom.FormInputEl>occurrence;
            return new api.data.Value(inputEl.getValue(), api.data.ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value:api.data.Value):boolean {
            if (api.util.isStringBlank(value.asString())) {
                return true;
            } else {
                return false;
            }
        }

        private comboboxFilter(item:api.ui.combobox.Option<string>, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }
    }

    api.form.input.InputTypeManager.register("SingleSelector", SingleSelector);
}