module api.content.site {

    import Property = api.data.Property;
    import PropertySet = api.data.PropertySet;
    import PropertyTree = api.data.PropertyTree;
    import ModuleKey = api.module.ModuleKey;

    export class ModuleConfig implements api.Equitable, api.Cloneable {

        private moduleKey: ModuleKey;

        private config: PropertySet;

        constructor(builder: ModuleConfigBuilder) {
            this.moduleKey = builder.moduleKey;
            this.config = builder.config;
        }

        getModuleKey(): api.module.ModuleKey {
            return this.moduleKey;
        }

        getConfig(): PropertySet {
            return this.config;
        }

        toJson(): Object {
            return {
                moduleKey: this.moduleKey.toString(),
                config: this.config.toJson()
            }
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

        static create(): ModuleConfigBuilder {
            return new ModuleConfigBuilder();
        }
    }

    class ModuleConfigBuilder {

        moduleKey: ModuleKey;

        config: PropertySet;

        constructor(source?: ModuleConfig) {
            if (source) {
                this.moduleKey = source.getModuleKey();
                if (source.getConfig()) {
                    var newTree = new PropertyTree(source.getConfig().getTree().getIdProvider(), source.getConfig());
                    this.config = newTree.getRoot();
                }
            }
        }

        fromData(propertySet: PropertySet): ModuleConfigBuilder {
            api.util.assertNotNull(propertySet, "data cannot be null");
            var moduleKey = ModuleKey.fromString(propertySet.getString("moduleKey"));
            var moduleConfig = propertySet.getSet("config");
            this.setModuleKey(moduleKey);
            this.setConfig(moduleConfig);
            return this;
        }

        setModuleKey(value: api.module.ModuleKey): ModuleConfigBuilder {
            this.moduleKey = value;
            return this;
        }

        setConfig(value: PropertySet): ModuleConfigBuilder {
            this.config = value;
            return this;
        }

        build(): ModuleConfig {
            return new ModuleConfig(this);
        }
    }

}