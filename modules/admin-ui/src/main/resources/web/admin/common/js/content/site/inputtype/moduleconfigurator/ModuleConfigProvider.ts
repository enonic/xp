module api.content.site.inputtype.moduleconfigurator {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import ModuleKey = api.module.ModuleKey;
    import ModuleConfig = api.content.site.ModuleConfig;

    export class ModuleConfigProvider {

        private propertyArray: PropertyArray;

        constructor(propertyArray: PropertyArray) {
            this.propertyArray = propertyArray;
        }

        getConfig(moduleKey: ModuleKey): ModuleConfig {
            var match: ModuleConfig = null;

            this.propertyArray.forEach((property: Property) => {
                if (property.hasNonNullValue()) {
                    var moduleConfigAsSet = property.getSet();
                    var moduleConfig = ModuleConfig.create().fromData(moduleConfigAsSet).build();
                    if (moduleConfig.getModuleKey().equals(moduleKey)) {
                        match = moduleConfig;
                    }
                }
            });

            if (!match) {
                var moduleConfigAsSet = this.propertyArray.addSet();
                moduleConfigAsSet.addString("moduleKey", moduleKey.toString());
                moduleConfigAsSet.addSet("config");
                var newModuleConfig = ModuleConfig.create().
                    fromData(moduleConfigAsSet).
                    build();
                return newModuleConfig;

            }
            else {
                return match;
            }
        }
    }
}