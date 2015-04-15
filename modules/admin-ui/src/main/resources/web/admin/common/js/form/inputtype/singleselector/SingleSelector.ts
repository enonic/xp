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

    export class SingleSelector extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<SingleSelectorConfig,string> {

        public static TYPE_DROPDOWN: string = "DROPDOWN";
        public static TYPE_RADIO: string = "RADIO";
        public static TYPE_COMBOBOX: string = "COMBOBOX";

        private type: string;

        private selector: api.dom.Element;
        private property: Property;
        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

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

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {

            this.input = input;
            this.property = property;

            if (SingleSelector.TYPE_RADIO == this.type) {
                this.selector = this.createRadioElement(input.getName(), property);
            }
            else if (SingleSelector.TYPE_COMBOBOX == this.type) {
                this.selector = this.createComboBoxElement(input.getName(), property);
            }
            else if (SingleSelector.TYPE_DROPDOWN == this.type) {
                this.selector = this.createDropdownElement(input.getName(), property);
            }
            this.appendChild(this.selector);

            return wemQ<void>(null);
        }

        giveFocus(): boolean {
            return this.selector.giveFocus();
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            var recording = new api.form.inputtype.InputValidationRecording();
            var propertyValue = this.property.getValue();
            if (propertyValue.isNull() && this.input.getOccurrences().getMinimum() > 0) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
                }
            }
            this.previousValidationRecording = recording;
            return recording;
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.selector.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.selector.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.selector.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.selector.unBlur(listener);
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
                this.validate(false);
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
                this.validate(false);
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
                this.validate(false);
            });

            return radioGroup;
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.STRING);
        }

        private comboboxFilter(item: Option<string>, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("SingleSelector", SingleSelector));
}