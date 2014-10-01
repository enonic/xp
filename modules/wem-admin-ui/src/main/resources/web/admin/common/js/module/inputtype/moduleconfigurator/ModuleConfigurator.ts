module api.module.inputtype.moduleconfigurator {

    import Value = api.data.Value;
    import Property = api.data.Property;
    import ValueType = api.data.type.ValueType;
    import ValueTypes = api.data.type.ValueTypes;
    import ValueChangedEvent = api.form.inputtype.support.ValueChangedEvent;
    import RootDataSet = api.data.RootDataSet;

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

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            var data = property.getData(),
                moduleConfigBuilder = new ModuleConfigBuilder();

            if (!!data) {
                var moduleKeyProperty = (<Property>data.getDataByName("moduleKey")[0]);
                var configProperty = (<Property>data.getDataByName("config")[0]);
                moduleConfigBuilder.
                    setModuleKey(!!moduleKeyProperty ? moduleKeyProperty.getString() : "").
                    setConfig(!!configProperty ? configProperty.getData() : new RootDataSet());
            }

            return moduleConfigBuilder.build();
        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {

            var moduleConfig = <ModuleConfig>element;
            moduleConfig.onModuleSelected((event: ModuleSelectedEvent) => {
                var data = new RootDataSet();
                data.addProperty("moduleKey", new Value(event.getSelectedModule().getModuleKey().getName(), ValueTypes.STRING));
                data.addProperty("config", new Value(event.getFormView().getData(), ValueTypes.DATA));
                var newValue = new Value(data, ValueTypes.DATA);
                listener(new ValueChangedEvent(newValue));
            });
        }

        getValue(occurrence: api.dom.Element): Value {
            var moduleConfig = <ModuleConfig> occurrence,
                data = new RootDataSet();
            if (moduleConfig.getSelectedModule()) {
                data.addProperty("moduleKey", new Value(moduleConfig.getSelectedModule().getModuleKey().getName(), ValueTypes.STRING));
                data.addProperty("config", new Value(moduleConfig.getFormView().getData(), ValueTypes.DATA));
            }
            return new Value(data, ValueTypes.DATA);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.DATA);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ModuleConfigurator", ModuleConfigurator));
}