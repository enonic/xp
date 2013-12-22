module api_content_site {

    export class ModuleConfig {

        private moduleKey: api_module.ModuleKey;

        private config: api_data.RootDataSet;

        constructor(builder: ModuleConfigBuilder) {
            this.moduleKey = builder.moduleKey;
            this.config = builder.config;
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

    export class ModuleConfigBuilder{

        moduleKey: api_module.ModuleKey;

        config: api_data.RootDataSet;

        setFromJson(moduleConfigJson: api_content_site_json.ModuleConfigJson):ModuleConfigBuilder {
            this.moduleKey = api_module.ModuleKey.fromString(moduleConfigJson.moduleKey);
            this.config = api_data.DataFactory.createRootDataSet(moduleConfigJson.config);
            return this;
        }

        setModuleKey(value:api_module.ModuleKey):ModuleConfigBuilder {
            this.moduleKey = value;
            return this;
        }

        setConfig(value:api_data.RootDataSet):ModuleConfigBuilder {
            this.config = value;
            return this;
        }

        build():ModuleConfig {
            return new ModuleConfig(this);
        }
    }

}