module api.module.inputtype.moduleconfigurator {

    import Value = api.data.Value;
    import ValueType = api.data.type.ValueType;
    import ValueTypes = api.data.type.ValueTypes;
    import ValueChangedEvent = api.form.inputtype.support.ValueChangedEvent;

    export class ModuleConfigurator extends api.form.inputtype.support.BaseInputTypeNotManagingAdd<any, ModuleConfig> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config, "module-configurator");
        }

        getValueType(): ValueType {
            return ValueTypes.DATA;
        }

        newInitialValue(): ModuleConfig {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var moduleConfig = new ModuleConfig();

            return moduleConfig;
        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {

            var moduleConfig = <ModuleConfig>element;
            moduleConfig.onModuleSelected((event: ModuleSelectedEvent) => {
                var data = new api.data.RootDataSet();
                data.addProperty("moduleKey", new Value(event.getSelectedModule().getModuleKey().getName(), ValueTypes.STRING));
                data.addProperty("config", new Value(event.getFormView().getData(), ValueTypes.DATA));
                var newValue = new Value(data, ValueTypes.DATA);
                listener(new ValueChangedEvent(newValue));
            });

        }

        getValue(occurrence: api.dom.Element): Value {
            // Needs implementation
            return  new api.data.Value(null, ValueTypes.DATA);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.DATA);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ModuleConfigurator", ModuleConfigurator));
}