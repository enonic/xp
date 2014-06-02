module api.form.inputtype.singleselector {

    import Option = api.ui.selector.Option;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;

    export interface SingleSelectorConfig {
        selectorType: string;
        options: {
            label: string;
            value: string;
        }[]
    }

    export class SingleSelector extends api.form.inputtype.support.BaseInputTypeView<SingleSelectorConfig> {

        public static TYPE_DROPDOWN: string = "DROPDOWN";
        public static TYPE_RADIO: string = "RADIO";
        public static TYPE_COMBOBOX: string = "COMBOBOX";

        private type: string;

        constructor(config: api.form.inputtype.InputTypeViewConfig<SingleSelectorConfig>) {
            super(config, "single-selector");
            this.type = config && config.inputConfig.selectorType && config.inputConfig.selectorType.toUpperCase();

            if (!(SingleSelector.TYPE_RADIO == this.type || SingleSelector.TYPE_COMBOBOX == this.type ||
                  SingleSelector.TYPE_DROPDOWN == this.type)) {
                throw new Error("Unsupported type of SingleSelector: " + this.type);
            }
        }

        newInitialValue(): api.data.Value {
            return new api.data.Value("", api.data.ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

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

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {

            if (SingleSelector.TYPE_RADIO == this.type) {
                var radioGroup = <api.ui.RadioGroup>element;
                radioGroup.onValueChanged((event: api.ui.ValueChangedEvent)=> {
                    listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(event.getNewValue())));
                });
            }
            else if (SingleSelector.TYPE_COMBOBOX == this.type) {
                var comboBox = <api.ui.selector.combobox.ComboBox<string>>element;
                comboBox.onOptionSelected(()=> {
                    // TODO: detect selected option changed
                    //listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(event.getOldValue()),
                    //    this.newValue(event.getNewValue())));
                });
            }
            else if (SingleSelector.TYPE_DROPDOWN == this.type) {
                var dropdown = <api.ui.Dropdown>element;
                dropdown.onValueChanged((event: api.ui.ValueChangedEvent) => {

                    // TODO: detect selected option changed

                    //listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(event.getOldValue()),
                    //    this.newValue(event.getNewValue())));

                });
            }
        }

        private createComboBoxElement(name: string, property: api.data.Property): api.dom.Element {

            var selectedOptionsView = new api.ui.selector.combobox.SelectedOptionsView<string>();
            var comboBox = new api.ui.selector.combobox.ComboBox<string>(name, {
                filter: this.comboboxFilter,
                selectedOptionsView: selectedOptionsView,
                maximumOccurrences: 1,
                hideComboBoxWhenMaxReached: true
            });
            var inputConfig: SingleSelectorConfig = this.getConfig().inputConfig;
            if (inputConfig) {
                var option;
                for (var i = 0; i < inputConfig.options.length; i++) {
                    option = inputConfig.options[i];
                    comboBox.addOption({ value: option.value, displayValue: option.label});
                }
            }

            if (property) {
                comboBox.setValue(property.getString());
            }

            comboBox.onOptionFilterInputValueChanged((event: api.ui.selector.OptionFilterInputValueChangedEvent<string>) => {
                event.getGrid().getDataView().setFilterArgs({searchString: event.getNewValue()});
                event.getGrid().getDataView().refresh();
            });

            return comboBox;
        }

        private createDropdownElement(name: string, property: api.data.Property): api.dom.Element {

            var dropdown = new Dropdown<string>(name, <DropdownConfig<string>>{});

            var inputEl = new api.ui.Dropdown(name);

            var inputConfig: SingleSelectorConfig = this.getConfig().inputConfig;
            if (inputConfig) {
                for (var i = 0; i < inputConfig.options.length; i++) {
                    var option = inputConfig.options[i];
                    inputEl.addOption(option.value, option.label);
                    var option2: Option<string> = <Option<string>>{ value: option.value, displayValue: option.label};
                    dropdown.addOption(option2);
                }
            }

            if (property) {
                inputEl.setValue(property.getString());
                dropdown.setValue(property.getString());
            }

            return dropdown;
        }


        private createRadioElement(name: string, property: api.data.Property): api.dom.Element {

            var inputEl = new api.ui.RadioGroup(name);

            var inputConfig: SingleSelectorConfig = this.getConfig().inputConfig;
            if (inputConfig) {
                for (var i = 0; i < inputConfig.options.length; i++) {
                    var option = inputConfig.options[i];
                    inputEl.addOption(option.value, option.label);
                }
            }

            if (property) {
                inputEl.setValue(property.getString());
            }

            return inputEl;
        }

        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, api.data.ValueTypes.STRING);
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var inputEl = <api.dom.FormInputEl>occurrence;
            if (!inputEl.getValue()) {
                return null;
            }
            return this.newValue(inputEl.getValue());
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {

            if (value == null) {
                return true;
            }

            if (api.util.isStringBlank(value.asString())) {
                return true;
            } else {
                return false;
            }
        }

        private comboboxFilter(item: Option<string>, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }
    }

    api.form.inputtype.InputTypeManager.register("SingleSelector", SingleSelector);
}