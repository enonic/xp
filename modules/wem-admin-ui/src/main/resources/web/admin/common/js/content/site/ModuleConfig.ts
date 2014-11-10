module api.content.site {

    export class ModuleConfig implements api.Equitable, api.Cloneable {

        private moduleKey: api.module.ModuleKey;

        private config: api.data.RootDataSet;

        constructor(builder: ModuleConfigBuilder) {
            this.moduleKey = builder.moduleKey;
            this.config = builder.config;
        }

        getModuleKey(): api.module.ModuleKey {
            return this.moduleKey;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }

        setConfig(config: api.data.RootDataSet) {
            this.config = config;
        }

        toJson(): Object {
            return {
                moduleKey: this.moduleKey.toString(),
                config: this.config.toJson()
            }
        }

        toData(): api.data.RootDataSet {
            var data = new api.data.RootDataSet();
            data.addProperty("moduleKey", new api.data.Value(this.moduleKey.getName(), api.data.type.ValueTypes.STRING));
            data.addProperty("config", new api.data.Value(this.config, api.data.type.ValueTypes.DATA));
            return data;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ModuleConfig)) {
                return false;
            }

            var other = <ModuleConfig>o;

            if (!api.ObjectHelper.equals(this.moduleKey, other.moduleKey)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.config, other.config)) {
                return false;
            }

            return true;
        }

        clone(): ModuleConfig {

            return new ModuleConfigBuilder(this).build();
        }
    }

    export class ModuleConfigBuilder {

        moduleKey: api.module.ModuleKey;

        config: api.data.RootDataSet;

        constructor(source?: ModuleConfig) {
            if (source) {
                this.moduleKey = source.getModuleKey();
                this.config = source.getConfig() ? source.getConfig().clone() : null;
            }
        }

        fromData(data: api.data.RootDataSet): ModuleConfigBuilder {
            api.util.assertNotNull(data, "data cannot be null");
            var moduleKey = api.module.ModuleKey.fromString(data.getProperty("moduleKey").getString());
            var moduleConfig = data.getProperty("config").getData();
            this.setModuleKey(moduleKey);
            this.setConfig(moduleConfig);
            return this;
        }

        setModuleKey(value: api.module.ModuleKey): ModuleConfigBuilder {
            this.moduleKey = value;
            return this;
        }

        setConfig(value: api.data.RootDataSet): ModuleConfigBuilder {
            this.config = value;
            return this;
        }

        build(): ModuleConfig {
            return new ModuleConfig(this);
        }
    }

}