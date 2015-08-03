module api.content.site {

    import ApplicationKey = api.application.ApplicationKey;

    export class SiteModel {

        public static PROPERTY_NAME_SITE_CONFIGS = "siteConfigs";

        private site: api.content.site.Site;

        private siteConfigs: SiteConfig[];

        private moduleAddedListeners: {(event: ModuleAddedEvent):void}[] = [];

        private moduleRemovedListeners: {(event: ModuleRemovedEvent):void}[] = [];

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
                    this.notifyModuleAdded(siteConfig);
                }
            });

            this.site.getContentData().onPropertyRemoved((event: api.data.PropertyRemovedEvent) => {
                var property: api.data.Property = event.getProperty();
                if (property.getName() == "siteConfig") {
                    var applicationKey = ApplicationKey.fromString(property.getPropertySet().getString("applicationKey"));
                    this.siteConfigs = this.siteConfigs.filter((siteConfig: SiteConfig) =>
                            !siteConfig.getApplicationKey().equals(applicationKey)
                    );
                    this.notifyModuleRemoved(applicationKey);
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

        onModuleAdded(listener: (event: ModuleAddedEvent)=>void) {
            this.moduleAddedListeners.push(listener);
        }

        unModuleAdded(listener: (event: ModuleAddedEvent)=>void) {
            this.moduleAddedListeners =
                this.moduleAddedListeners.filter((curr: (event: ModuleAddedEvent)=>void) => {
                    return listener != curr;
                });
        }

        private notifyModuleAdded(siteConfig: SiteConfig) {
            var event = new ModuleAddedEvent(siteConfig);
            this.moduleAddedListeners.forEach((listener: (event: ModuleAddedEvent)=>void) => {
                listener(event);
            })
        }

        onModuleRemoved(listener: (event: ModuleRemovedEvent)=>void) {
            this.moduleRemovedListeners.push(listener);
        }

        unModuleRemoved(listener: (event: ModuleRemovedEvent)=>void) {
            this.moduleRemovedListeners =
                this.moduleRemovedListeners.filter((curr: (event: ModuleRemovedEvent)=>void) => {
                    return listener != curr;
                });
        }

        private notifyModuleRemoved(applicationKey: ApplicationKey) {
            var event = new ModuleRemovedEvent(applicationKey);
            this.moduleRemovedListeners.forEach((listener: (event: ModuleRemovedEvent)=>void) => {
                listener(event);
            })
        }
    }
}