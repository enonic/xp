module api.content.site {

    import Property = api.data.Property;
    import PropertyIdProvider = api.data.PropertyIdProvider;
    import ModuleKey = api.module.ModuleKey;
    import ValueTypes = api.data.ValueTypes;

    export class Site extends api.content.Content implements api.Equitable, api.Cloneable {

        constructor(builder: SiteBuilder) {
            super(builder);
        }

        isSite(): boolean {
            return true;
        }

        getDescription(): string {
            return this.getContentData().getString("description");
        }

        getModuleConfigs(): ModuleConfig[] {

            var moduleConfigs: ModuleConfig[] = [];
            this.getContentData().forEachProperty("moduleConfig", (moduleProperty: Property) => {
                var moduleConfigData = moduleProperty.getPropertySet();
                if (moduleConfigData) {
                    var moduleConfig = ModuleConfig.create().fromData(moduleConfigData).build();
                    moduleConfigs.push(moduleConfig);
                }
            });

            return moduleConfigs;
        }

        getModuleKeys(): ModuleKey[] {
            return this.getModuleConfigs().map((config: ModuleConfig) => config.getModuleKey());
        }

        equals(o: api.Equitable, ignoreEmptyValues: boolean = false): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Site)) {
                return false;
            }

            return super.equals(o, ignoreEmptyValues);
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

        fromContentJson(contentJson: api.content.json.ContentJson, propertyIdProvider: PropertyIdProvider): SiteBuilder {
            super.fromContentJson(contentJson, propertyIdProvider);
            return this;
        }

        build(): Site {
            return new Site(this);
        }
    }
}