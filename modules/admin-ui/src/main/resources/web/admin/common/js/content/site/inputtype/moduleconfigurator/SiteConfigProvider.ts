module api.content.site.inputtype.moduleconfigurator {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import ModuleKey = api.module.ModuleKey;
    import SiteConfig = api.content.site.SiteConfig;

    export class SiteConfigProvider {

        private propertyArray: PropertyArray;

        constructor(propertyArray: PropertyArray) {
            this.propertyArray = propertyArray;
        }

        getConfig(moduleKey: ModuleKey): SiteConfig {
            var match: SiteConfig = null;

            this.propertyArray.forEach((property: Property) => {
                if (property.hasNonNullValue()) {
                    var siteConfigAsSet = property.getPropertySet();
                    var siteConfig = SiteConfig.create().fromData(siteConfigAsSet).build();
                    if (siteConfig.getModuleKey().equals(moduleKey)) {
                        match = siteConfig;
                    }
                }
            });

            if (!match) {
                var siteConfigAsSet = this.propertyArray.addSet();
                siteConfigAsSet.addString("moduleKey", moduleKey.toString());
                siteConfigAsSet.addPropertySet("config");
                var newSiteConfig = SiteConfig.create().
                    fromData(siteConfigAsSet).
                    build();
                return newSiteConfig;

            }
            else {
                return match;
            }
        }
    }
}