module api.content.site {

    import Property = api.data.Property;
    import PropertySet = api.data.PropertySet;
    import PropertyTree = api.data.PropertyTree;
    import ModuleKey = api.module.ModuleKey;

    export class SiteConfig implements api.Equitable, api.Cloneable {

        private moduleKey: ModuleKey;

        private config: PropertySet;

        constructor(builder: SiteConfigBuilder) {
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

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, SiteConfig)) {
                return false;
            }

            var other = <SiteConfig>o;

            if (!api.ObjectHelper.equals(this.moduleKey, other.moduleKey)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.config, other.config)) {
                return false;
            }

            return true;
        }

        toPropertySet(parent: PropertySet): PropertySet {
            var siteConfigSet = parent.addPropertySet("siteConfig");
            siteConfigSet.addString("moduleKey", this.moduleKey.getName());
            siteConfigSet.addPropertySet("config", this.config.copy(parent.getTree()));
            return siteConfigSet;
        }

        clone(): SiteConfig {

            return new SiteConfigBuilder(this).build();
        }

        static create(): SiteConfigBuilder {
            return new SiteConfigBuilder();
        }
    }

    export class SiteConfigBuilder {

        moduleKey: ModuleKey;

        config: PropertySet;

        constructor(source?: SiteConfig) {
            if (source) {
                this.moduleKey = source.getModuleKey();
                if (source.getConfig()) {
                    var newTree = new PropertyTree(source.getConfig().getTree().getIdProvider(), source.getConfig());
                    this.config = newTree.getRoot();
                }
            }
        }

        fromData(propertySet: PropertySet): SiteConfigBuilder {
            api.util.assertNotNull(propertySet, "data cannot be null");
            var moduleKey = ModuleKey.fromString(propertySet.getString("moduleKey"));
            var siteConfig = propertySet.getPropertySet("config");
            this.setModuleKey(moduleKey);
            this.setConfig(siteConfig);
            return this;
        }

        setModuleKey(value: api.module.ModuleKey): SiteConfigBuilder {
            this.moduleKey = value;
            return this;
        }

        setConfig(value: PropertySet): SiteConfigBuilder {
            this.config = value;
            return this;
        }

        build(): SiteConfig {
            return new SiteConfig(this);
        }
    }

}