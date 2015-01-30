module api.form.inputtype.singleselector {

    import Option = api.ui.selector.Option;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;

    export interface SingleSelectorConfig {
        selectorType: string;
        options: {
            label: string;
            value: string;
        }[]
    }

    export class SingleSelector extends api.form.inputtype.support.BaseInputTypeNotManagingAdd<SingleSelectorConfig,string> {

        public static TYPE_DROPDOWN: string = "DROPDOWN";
        public static TYPE_RADIO: string = "RADIO";
        public static TYPE_COMBOBOX: string = "COMBOBOX";

        private type: string;

        constructor(config: api.form.inputtype.InputTypeViewContext<SingleSelectorConfig>) {
            super(config, "single-selector");
            this.type = config && config.inputConfig.selectorType && config.inputConfig.selectorType.toUpperCase();

            if (!(SingleSelector.TYPE_RADIO == this.type || SingleSelector.TYPE_COMBOBOX == this.type ||
                  SingleSelector.TYPE_DROPDOWN == this.type)) {
                throw new Error("Unsupported type of SingleSelector: " + this.type);
            }
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            var name = this.getInput().getName() + "-" + index;
            if (SingleSelector.TYPE_RADIO == this.type) {
                return this.createRadioElement(name, property);
            }
            else if (SingleSelector.TYPE_COMBOBOX == this.type) {
                return this.createComboBoxElement(name, property);
            }
            else if (SingleSelector.TYPE_DROPDOWN == this.type) {
                return this.createDropdownElement(name, property);
            }
            return null;
        }

        private createComboBoxElement(name: string, property: Property): api.dom.Element {

            var comboAndSelectedOptionsWrapper = new api.dom.DivEl();
            var selectedOptionsView = new BaseSelectedOptionsView<string>();
            var comboBox = new ComboBox<string>(name, {
                filter: this.comboboxFilter,
                selectedOptionsView: selectedOptionsView,
                maximumOccurrences: 1,
                hideComboBoxWhenMaxReached: true
            });
            var inputConfig: SingleSelectorConfig = this.getContext().inputConfig;
            if (inputConfig) {
                var option;
                for (var i = 0; i < inputConfig.options.length; i++) {
                    option = inputConfig.options[i];
                    comboBox.addOption({ value: option.value, displayValue: option.label});
                }
            }

            if (property.hasNonNullValue()) {
                comboBox.setValue(property.getString());
            }

            comboBox.onOptionFilterInputValueChanged((event: api.ui.selector.OptionFilterInputValueChangedEvent<string>) => {
                event.getGrid().getDataView().setFilterArgs({searchString: event.getNewValue()});
                event.getGrid().getDataView().refresh();
            });

            comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<string>)=> {
                property.setValue(this.newValue(event.getOption().value));
            });

            comboAndSelectedOptionsWrapper.appendChild(comboBox);
            comboAndSelectedOptionsWrapper.appendChild(selectedOptionsView);
            return comboAndSelectedOptionsWrapper;
        }

        private createDropdownElement(name: string, property: Property): api.dom.Element {

            var dropdown = new Dropdown<string>(name, <DropdownConfig<string>>{});

            var inputEl = new api.ui.Dropdown(name);

            var inputConfig: SingleSelectorConfig = this.getContext().inputConfig;
            if (inputConfig) {
                for (var i = 0; i < inputConfig.options.length; i++) {
                    var option = inputConfig.options[i];
                    inputEl.addOption(option.value, option.label);
                    var option2: Option<string> = <Option<string>>{ value: option.value, displayValue: option.label};
                    dropdown.addOption(option2);
                }
            }

            if (property.hasNonNullValue()) {
                inputEl.setValue(property.getString());
                dropdown.setValue(property.getString());
            }

            dropdown.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<string>) => {
                property.setValue(this.newValue(event.getOption().value));
            });

            return dropdown;
        }


        private createRadioElement(name: string, property: Property): api.dom.Element {

            var radioGroup = new api.ui.RadioGroup(name);

            var inputConfig: SingleSelectorConfig = this.getContext().inputConfig;
            if (inputConfig) {
                for (var i = 0; i < inputConfig.options.length; i++) {
                    var option = inputConfig.options[i];
                    radioGroup.addOption(option.value, option.label);
                }
            }

            if (property.hasNonNullValue()) {
                radioGroup.setValue(property.getString());
            }

            radioGroup.onValueChanged((event: api.ui.ValueChangedEvent)=> {
                property.setValue(this.newValue(event.getNewValue()));
            });

            return radioGroup;
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.STRING);
        }

        private comboboxFilter(item: Option<string>, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING) || !this.isExistingValue(value.getString());
        }

        private isExistingValue(value: string): boolean {
            var options = this.getContext().inputConfig.options || [];

            return options.some((option: any) => {
                return option.value == value;
            });
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {

            // TODO
            return true;
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("SingleSelector", SingleSelector));
}