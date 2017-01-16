module api.content.site {

    import Property = api.data.Property;
    import ApplicationKey = api.application.ApplicationKey;
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

        getSiteConfigs(): SiteConfig[] {

            let siteConfigs: SiteConfig[] = [];
            this.getContentData().forEachProperty("siteConfig", (applicationProperty: Property) => {
                let siteConfigData = applicationProperty.getPropertySet();
                if (siteConfigData) {
                    let siteConfig = SiteConfig.create().fromData(siteConfigData).build();
                    siteConfigs.push(siteConfig);
                }
            });

            return siteConfigs;
        }

        getApplicationKeys(): ApplicationKey[] {
            return this.getSiteConfigs().map((config: SiteConfig) => config.getApplicationKey());
        }

        equals(o: api.Equitable, ignoreEmptyValues: boolean = false, shallow: boolean = false): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Site)) {
                return false;
            }

            return super.equals(o, ignoreEmptyValues, shallow);
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
