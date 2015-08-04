module api.content.site.inputtype.siteconfigurator {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import ApplicationKey = api.application.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig;

    export class SiteConfigProvider {

        private propertyArray: PropertyArray;

        constructor(propertyArray: PropertyArray) {
            this.propertyArray = propertyArray;
        }

        getConfig(applicationKey: ApplicationKey): SiteConfig {
            var match: SiteConfig = null;

            this.propertyArray.forEach((property: Property) => {
                if (property.hasNonNullValue()) {
                    var siteConfigAsSet = property.getPropertySet();
                    var siteConfig = SiteConfig.create().fromData(siteConfigAsSet).build();
                    if (siteConfig.getApplicationKey().equals(applicationKey)) {
                        match = siteConfig;
                    }
                }
            });

            if (!match) {
                var siteConfigAsSet = this.propertyArray.addSet();
                siteConfigAsSet.addString("applicationKey", applicationKey.toString());
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