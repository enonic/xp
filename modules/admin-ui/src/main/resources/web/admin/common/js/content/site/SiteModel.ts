module api.content.site {

    import ApplicationKey = api.application.ApplicationKey;

    export class SiteModel {

        public static PROPERTY_NAME_SITE_CONFIGS = "siteConfigs";

        private site: api.content.site.Site;

        private siteConfigs: SiteConfig[];

        private applicationAddedListeners: {(event: ApplicationAddedEvent):void}[] = [];

        private applicationRemovedListeners: {(event: ApplicationRemovedEvent):void}[] = [];

        private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

        constructor(site: Site) {
            this.site = site;
            this.siteConfigs = site.getSiteConfigs();

            this.site.getContentData().onPropertyAdded((event: api.data.PropertyAddedEvent) => {
                var property: api.data.Property = event.getProperty();
                // TODO:? property.getPath().startsWith(PropertyPath.fromString(".siteConfig")) &&  property.getName( )=="config")
                if (property.getPath().toString().indexOf(".siteConfig") == 0 && property.getName() == "config") {
                    var siteConfig: SiteConfig = api.content.site.SiteConfig.create().fromData(property.getParent()).build();
                    if (!this.siteConfigs) {
                        this.siteConfigs = [];
                    }
                    this.siteConfigs.push(siteConfig);
                    this.notifyApplicationAdded(siteConfig);
                }
            });

            this.site.getContentData().onPropertyRemoved((event: api.data.PropertyRemovedEvent) => {
                var property: api.data.Property = event.getProperty();
                if (property.getName() == "siteConfig") {
                    var applicationKey = ApplicationKey.fromString(property.getPropertySet().getString("applicationKey"));
                    this.siteConfigs = this.siteConfigs.filter((siteConfig: SiteConfig) =>
                            !siteConfig.getApplicationKey().equals(applicationKey)
                    );
                    this.notifyApplicationRemoved(applicationKey);
                }
            });


        }

        getSite(): api.content.site.Site {
            return this.site;
        }

        getSiteId(): api.content.ContentId {
            return this.site.getContentId();
        }

        getApplicationKeys(): ApplicationKey[] {
            return this.siteConfigs.map((sc: SiteConfig) => sc.getApplicationKey());
        }

        onPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListeners.push(listener);
        }

        unPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListeners =
                this.propertyChangedListeners.filter((curr: (event: api.PropertyChangedEvent)=>void) => {
                    return listener != curr;
                });
        }

        private notifyPropertyChanged(property: string, oldValue: any, newValue: any, source: any) {
            var event = new api.PropertyChangedEvent(property, oldValue, newValue, source);
            this.propertyChangedListeners.forEach((listener: (event: api.PropertyChangedEvent)=>void) => {
                listener(event);
            })
        }

        onApplicationAdded(listener: (event: ApplicationAddedEvent)=>void) {
            this.applicationAddedListeners.push(listener);
        }

        unApplicationAdded(listener: (event: ApplicationAddedEvent)=>void) {
            this.applicationAddedListeners =
            this.applicationAddedListeners.filter((curr: (event: ApplicationAddedEvent)=>void) => {
                    return listener != curr;
                });
        }

        private notifyApplicationAdded(siteConfig: SiteConfig) {
            var event = new ApplicationAddedEvent(siteConfig);
            this.applicationAddedListeners.forEach((listener: (event: ApplicationAddedEvent)=>void) => {
                listener(event);
            })
        }

        onApplicationRemoved(listener: (event: ApplicationRemovedEvent)=>void) {
            this.applicationRemovedListeners.push(listener);
        }

        unApplicationRemoved(listener: (event: ApplicationRemovedEvent)=>void) {
            this.applicationRemovedListeners =
            this.applicationRemovedListeners.filter((curr: (event: ApplicationRemovedEvent)=>void) => {
                    return listener != curr;
                });
        }

        private notifyApplicationRemoved(applicationKey: ApplicationKey) {
            var event = new ApplicationRemovedEvent(applicationKey);
            this.applicationRemovedListeners.forEach((listener: (event: ApplicationRemovedEvent)=>void) => {
                listener(event);
            })
        }
    }
}