module api.module.inputtype.moduleconfigurator {

    export class ModuleConfigProvider {

        private properties: api.data.Property[];

        constructor(properties: api.data.Property[]) {
            this.properties = properties;
        }

        getConfig(moduleKey: api.module.ModuleKey): api.data.RootDataSet {
            var match: api.data.RootDataSet = null;

            this.properties.forEach((property: api.data.Property) => {
                var propertyData = property.getData();
                var moduleKeyProperty = propertyData.getPropertyById(new api.data.DataId("moduleKey", 0)).getString();
                var currModuleKey = api.module.ModuleKey.fromString(moduleKeyProperty);
                if (moduleKey.equals(currModuleKey)) {
                    match = propertyData.getPropertyById(new api.data.DataId("config", 0)).getData();
                }
            });

            return match;
        }
    }
}