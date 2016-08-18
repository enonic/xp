module api.content.form.inputtype.customselector {

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
    import JsonRequest = api.rest.JsonRequest;
    import StringHelper = api.util.StringHelper;
    import Path = api.rest.Path;
    import JsonResponse = api.rest.JsonResponse;
    import ContentInputTypeViewContext = api.content.form.inputtype.ContentInputTypeViewContext;
    import ElementBuilder = api.dom.ElementBuilder;
    import NewElementBuilder = api.dom.NewElementBuilder;

    export interface CustomSelectorResponse {
        total: number,
        count: number,
        hits: CustomSelectorItem[]
    }

    export interface CustomSelectorItem {
        id: string;
        displayName: string;
        description: string;
        iconUrl?: string;
        icon?: {
            data: string;
            type: string;
        };
    }

    export class CustomSelector extends api.form.inputtype.support.BaseInputTypeNotManagingAdd<string> {

        public static debug: boolean = true;

        private static portalUrl: string = '/admin/portal/edit/draft{0}/_/service/{1}';

        private serviceUrl: string;

        private contentPath: string;

        constructor(context: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(context);

            if (CustomSelector.debug) {
                console.debug("CustomSelector: config", context.inputConfig);
            }

            this.serviceUrl = context.inputConfig['service'][0]['value'];
            this.contentPath = context.contentPath.toString();
        }

        private loadOptionsFor(dropdown: Dropdown<CustomSelectorItem>): wemQ.Promise<void> {

            let path = StringHelper.format(CustomSelector.portalUrl, this.contentPath, this.serviceUrl);

            return new JsonRequest<CustomSelectorResponse>()
                .setPath(Path.fromString(path))
                .send().then((response: JsonResponse<CustomSelectorResponse>) => {
                    return response.getResult().hits;
                }).then((items) => {
                    var options = items.map((item) => {
                        return {
                            value: String(item.id),
                            displayValue: item
                        }
                    });
                    dropdown.setOptions(options);
                }).catch((reason) => {
                    dropdown.setEmptyDropdownText("Could not load options");
                    api.DefaultErrorHandler.handle(reason);
                });
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

            let dropdown = this.createDropdown(property);

            property.onPropertyValueChanged((event: api.data.PropertyValueChangedEvent) => {
                this.updateInputOccurrenceElement(dropdown, property, true);
            });

            return dropdown;
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
                dataIdProperty: "value",
                value: property.getString()
            });

            dropdown.onOptionSelected((event: OptionSelectedEvent<CustomSelectorItem>) => {
                this.ignorePropertyChange = true;

                let value = new Value(event.getOption().value, ValueTypes.STRING);
                property.setValue(value);

                this.ignorePropertyChange = false;
                this.validate(false);
            });

            this.loadOptionsFor(dropdown);

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


        resolveSubName(object: CustomSelectorItem): string {
            return object.description;
        }

        resolveIconEl(object: CustomSelectorItem): api.dom.Element {
            if (object.icon && object.icon.data) {
                return api.dom.Element.fromString(object.icon.data);
            }
            return null;
        }

        resolveIconUrl(object: CustomSelectorItem): string {
            return object.iconUrl;
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("CustomSelector", CustomSelector));
}