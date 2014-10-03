module api.content.site {

    import Property = api.data.Property;
    import DataId = api.data.DataId;
    import ModuleKey = api.module.ModuleKey;
    import ValueTypes = api.data.type.ValueTypes;

    export class Site extends api.content.Content implements api.Equitable, api.Cloneable {

        constructor(builder: SiteBuilder) {
            super(builder);
        }

        isSite(): boolean {
            return true;
        }

        getDescription(): string {
            return this.getContentData().getProperty("description").getString();
        }

        getModuleConfigs(): ModuleConfig[] {

            var moduleConfigs: ModuleConfig[] = [];
            var modulesProperites = this.getContentData().getPropertiesByName("modules");
            modulesProperites.forEach((moduleProperty: Property) => {
                var moduleConfigData = moduleProperty.getData();
                if (moduleConfigData) {
                    var moduleKey = ModuleKey.fromString(moduleConfigData.getProperty("moduleKey").getString());
                    var moduleConfigData = moduleConfigData.getProperty("config").getData();
                    var moduleConfig = new ModuleConfigBuilder().
                        setModuleKey(moduleKey).
                        setConfig(moduleConfigData).build();
                    moduleConfigs.push(moduleConfig);
                }
            });

            return moduleConfigs;
        }

        getModuleKeys(): ModuleKey[] {
            return this.getModuleConfigs().map((config: ModuleConfig) => config.getModuleKey());
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Site)) {
                return false;
            }

            return super.equals(o);
        }

        clone(): Site {

            return this.newBuilder().build();
        }

        newBuilder(): SiteBuilder {
            return new SiteBuilder(this);
        }
    }

    export class SiteBuilder extends api.content.ContentBuilder {

        constructor(source?: Site) {
            super(source);
        }

        fromContentJson(contentJson: api.content.json.ContentJson): SiteBuilder {
            super.fromContentJson(contentJson);
            return this;
        }

        build(): Site {
            return new Site(this);
        }
    }
}