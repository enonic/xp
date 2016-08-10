module api.form.inputtype.customselector {

    import PropertyArray = api.data.PropertyArray;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;
    import ComboBoxOption = api.form.inputtype.combobox.ComboBoxOption;
    import ComboBoxDisplayValueViewer = api.form.inputtype.combobox.ComboBoxDisplayValueViewer;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import Viewer = api.ui.Viewer;
    import NamesAndIconViewer = api.ui.NamesAndIconViewer;

    export interface CustomSelectorItem {
        id: string;
        displayName: string;
        description: string;
        iconUrl: string;
        icon: Object;
    }

    export class CustomSelector extends api.form.inputtype.support.BaseInputTypeNotManagingAdd<string> {

        constructor(context: api.form.inputtype.InputTypeViewContext) {
            super(context);

            this.readConfig(context.inputConfig);
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): ComboBoxOption[] {
            var options: ComboBoxOption[] = [];

            var optionValues = inputConfig['option'] || [],
                optionValue;

            for (var i = 0; i < optionValues.length; i++) {
                optionValue = optionValues[i];
                options.push({label: optionValue['value'], value: optionValue['@value']});
            }

            return options;
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {
            if (!ValueTypes.STRING.equals(property.getType())) {
                property.convertValueType(ValueTypes.STRING);
            }

            let comboBox = this.createDropdown(property);

            property.onPropertyValueChanged((event: api.data.PropertyValueChangedEvent) => {
                this.updateInputOccurrenceElement(comboBox, property, true);
            });

            return comboBox;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly?: boolean): any {
            var customSelector = <api.ui.selector.combobox.ComboBox<string>> occurrence;

            if (!unchangedOnly || !customSelector.isDirty()) {
                customSelector.setValue(property.getString());
            }
        }

        createDropdown(property: Property): Dropdown<CustomSelectorItem> {

            var dropdown = new Dropdown<CustomSelectorItem>('custom-selector', {
                optionDisplayValueViewer: new CustomSelectorItemViewer(),
                dataIdProperty: "id",
                value: property.getString()
            });

            dropdown.onOptionSelected((event: OptionSelectedEvent<CustomSelectorItem>) => {
                this.ignorePropertyChange = true;

                let value = new Value(event.getOption().value, ValueTypes.STRING);
                property.setValue(value);

                this.ignorePropertyChange = false;
                this.validate(false);
            });

            return dropdown;
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element): boolean {
            var dropdown = <Dropdown<CustomSelectorItem>>inputElement;
            return dropdown.isValid();
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING);
        }

    }

    class CustomSelectorItemViewer extends NamesAndIconViewer<CustomSelectorItem> {
        constructor() {
            super("custom-selector-item-viewer");
        }


        resolveDisplayName(object: CustomSelectorItem): string {
            return object.displayName;
        }


        resolveSubTitle(object: CustomSelectorItem): string {
            return object.description;
        }


        resolveIconUrl(object: CustomSelectorItem): string {
            var iconUrl;
            if (object.icon) {
                // TODO
            } else {
                iconUrl = object.iconUrl;
            }
            return iconUrl;
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("CustomSelector", CustomSelector));
}