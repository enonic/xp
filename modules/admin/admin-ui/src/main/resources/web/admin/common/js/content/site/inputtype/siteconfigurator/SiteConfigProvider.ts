module api.content.site.inputtype.siteconfigurator {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import ApplicationKey = api.application.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig;

    export class SiteConfigProvider {

        private propertyArray: PropertyArray;

        private arrayChangedListeners: {() : void}[] = [];

        private beforeArrayChangedListeners: {():void}[] = [];

        private afterArrayChangedListeners: {():void}[] = [];

        constructor(propertyArray: PropertyArray) {
            this.setPropertyArray(propertyArray);
        }

        setPropertyArray(propertyArray: PropertyArray) {
            this.propertyArray = propertyArray;
            this.notifyPropertyChanged();
        }

        getConfig(applicationKey: ApplicationKey, addMissing: boolean = true): SiteConfig {
            var match: SiteConfig = null;

            if (!applicationKey) {
                return match;
            }

            this.propertyArray.forEach((property: Property) => {
                if (property.hasNonNullValue()) {
                    var siteConfigAsSet = property.getPropertySet();
                    var siteConfig = SiteConfig.create().fromData(siteConfigAsSet).build();
                    if (siteConfig.getApplicationKey().equals(applicationKey)) {
                        match = siteConfig;
                    }
                }
            });

            if (!match && addMissing) {
                this.notifyBeforePropertyChanged();

                var siteConfigAsSet = this.propertyArray.addSet();
                siteConfigAsSet.addString("applicationKey", applicationKey.toString());
                siteConfigAsSet.addPropertySet("config");
                var newSiteConfig = SiteConfig.create().
                    fromData(siteConfigAsSet).
                    build();

                this.notifyAfterPropertyChanged();
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

        onBeforePropertyChanged(listener: ()=>void) {
            this.beforeArrayChangedListeners.push(listener);
        }

        unBeforePropertyChanged(listener: ()=>void) {
            this.beforeArrayChangedListeners = this.beforeArrayChangedListeners.filter((currentListener: ()=>void) => {
                return currentListener != listener;
            });
        }

        private notifyBeforePropertyChanged() {
            this.beforeArrayChangedListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        onAfterPropertyChanged(listener: ()=>void) {
            this.afterArrayChangedListeners.push(listener);
        }

        unAfterPropertyChanged(listener: ()=>void) {
            this.afterArrayChangedListeners = this.afterArrayChangedListeners.filter((currentListener: ()=>void) => {
                return currentListener != listener;
            });
        }

        private notifyAfterPropertyChanged() {
            this.afterArrayChangedListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }
    }
}