module api.content.site.inputtype.siteconfigurator {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import ApplicationKey = api.application.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig;

    export class SiteConfigProvider {

        private propertyArray: PropertyArray;

        private arrayChangedListeners: {() : void}[] = [];

        constructor(propertyArray: PropertyArray) {
            this.setPropertyArray(propertyArray);
        }

        setPropertyArray(propertyArray: PropertyArray) {
            this.propertyArray = propertyArray;
            this.notifyPropertyChanged();
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
            return match;
        }

        onPropertyChanged(listener: ()=>void) {
            this.arrayChangedListeners.push(listener);
        }

        unPropertyChanged(listener: ()=>void) {
            this.arrayChangedListeners = this.arrayChangedListeners.filter((currentListener: ()=>void) => {
                return currentListener != listener;
            });

        }

        private notifyPropertyChanged() {
            this.arrayChangedListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }
    }
}