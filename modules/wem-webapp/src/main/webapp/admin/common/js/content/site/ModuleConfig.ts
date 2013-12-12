module api_content_site {

    export class ModuleConfig {

        private moduleKey: api_module.ModuleKey;

        private config: api_data.RootDataSet;

        constructor(moduleConfigJson: api_content_site_json.ModuleConfigJson) {
            this.moduleKey = api_module.ModuleKey.fromString(moduleConfigJson.moduleKey);
            this.config = api_data.DataFactory.createRootDataSet(moduleConfigJson.config);
        }

        getModuleKey(): api_module.ModuleKey {
            return this.moduleKey;
        }

        getConfig(): api_data.RootDataSet {
            return this.config;
        }

        setConfig(config: api_data.RootDataSet) {
            this.config = config;
        }

        toJson(): Object {
            return {
                moduleKey: this.moduleKey.toString(),
                config: this.config.toJson()
            }
        }
    }
}