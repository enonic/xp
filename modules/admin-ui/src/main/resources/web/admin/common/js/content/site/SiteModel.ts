module api.content.site {

    import ModuleKey = api.module.ModuleKey;

    export class SiteModel {

        public static PROPERTY_NAME_MODULE_CONFIGS = "moduleConfigs";

        private site: api.content.site.Site;

        private moduleConfigs: ModuleConfig[];

        private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

        constructor(site: Site) {
            this.site = site;
            this.moduleConfigs = site.getModuleConfigs();
        }

        setModules(moduleConfigs: ModuleConfig[], source?: any) {
            var oldValue = this.moduleConfigs;
            this.moduleConfigs = moduleConfigs;
            this.notifyPropertyChanged(SiteModel.PROPERTY_NAME_MODULE_CONFIGS, oldValue, this.moduleConfigs, source);
        }

        getSite(): api.content.site.Site {
            return this.site;
        }

        getSiteId(): api.content.ContentId {
            return this.site.getContentId();
        }

        getModuleKeys(): ModuleKey[] {
            return this.moduleConfigs.map((mc: ModuleConfig) => mc.getModuleKey());
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
    }
}