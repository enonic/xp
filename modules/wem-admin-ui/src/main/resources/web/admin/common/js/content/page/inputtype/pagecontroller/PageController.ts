module api.content.page.inputtype.pagecontroller {

    import support = api.form.inputtype.support;
    import ValueTypes = api.data.type.ValueTypes;
    import ValueType = api.data.type.ValueType;
    import PageDescriptorDropdown = api.content.page.PageDescriptorDropdown;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PageDescriptorsJson = api.content.page.PageDescriptorsJson;
    import GetPageDescriptorsByModulesRequest = api.content.page.GetPageDescriptorsByModulesRequest;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import Value = api.data.Value;
    import ValueChangedEvent = api.form.inputtype.support.ValueChangedEvent;
    import Element = api.dom.Element;
    import ContentInputTypeViewContext = api.content.form.inputtype.ContentInputTypeViewContext;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class PageController extends support.BaseInputTypeNotManagingAdd<any, string> {

        constructor(context: ContentInputTypeViewContext<any>) {
            super(context);
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): string {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): Element {
            var context = <ContentInputTypeViewContext<any>>this.getContext(),
                moduleKeys = context.site.getSite().getModuleKeys(),
                request = new GetPageDescriptorsByModulesRequest(moduleKeys),
                loader = new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(request);

            var dropdown = new PageDescriptorDropdown('page-controller[' + index + ']', {
                loader: loader
            });

            loader.onLoadedData((event: LoadedDataEvent<PageDescriptor>) => {
                dropdown.setValue(property.getString());
            });

            return dropdown;
        }

        onOccurrenceValueChanged(element: Element, listener: (event: ValueChangedEvent) => void) {
            var dropdown = <PageDescriptorDropdown>element;
            dropdown.onOptionSelected((event: OptionSelectedEvent<PageDescriptor>) => {
                var newValue = new Value(event.getOption().value, ValueTypes.STRING);
                listener(new ValueChangedEvent(newValue));
            });
        }

        getValue(occurrence: Element): Value {
            var dropdown = <PageDescriptorDropdown> occurrence,
                selectedOption = dropdown.getSelectedOption();
            return new Value(selectedOption ? selectedOption.value : null, ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value == null || api.util.StringHelper.isBlank(value.asString()) || !value.getType().equals(ValueTypes.STRING);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("PageController", PageController));

}