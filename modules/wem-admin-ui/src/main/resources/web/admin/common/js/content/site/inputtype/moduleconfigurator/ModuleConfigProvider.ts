module api.content.site.inputtype.moduleconfigurator {

    export class ModuleConfigProvider {

        private properties: api.data.Property[];

        constructor(properties: api.data.Property[]) {
            this.properties = properties;
        }

        getConfig(moduleKey: api.module.ModuleKey): api.content.site.ModuleConfig {
            var match: api.content.site.ModuleConfig = null;

            this.properties.forEach((property: api.data.Property) => {
                if (property.hasNonNullValue()) {
                    var rootDataSet = property.getData();
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