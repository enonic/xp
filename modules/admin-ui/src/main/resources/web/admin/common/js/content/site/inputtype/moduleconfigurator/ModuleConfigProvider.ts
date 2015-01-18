module api.content.site.inputtype.moduleconfigurator {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;

    export class ModuleConfigProvider {

        private propertyArray: PropertyArray;

        constructor(propertyArray: PropertyArray) {
            this.propertyArray = propertyArray;
        }

        getConfig(moduleKey: api.module.ModuleKey): api.content.site.ModuleConfig {
            var match: api.content.site.ModuleConfig = null;

            this.propertyArray.forEach((property: Property) => {
                if (property.hasNonNullValue()) {
                    var rootDataSet = property.getSet();
                    var moduleConfig = new api.content.site.ModuleConfigBuilder().fromData(rootDataSet).build();
                    if (moduleConfig.getModuleKey().equals(moduleKey)) {
                        match = moduleConfig;
                    }
                }
            });

            return match;
        }
    }
}