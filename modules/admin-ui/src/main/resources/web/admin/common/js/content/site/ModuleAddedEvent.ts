module api.content.site {
    export class ModuleAddedEvent {

        private siteConfig: SiteConfig;

        constructor(siteConfig: SiteConfig) {
            this.siteConfig = siteConfig;
        }
        getModuleKey() : api.module.ModuleKey {
            return this.siteConfig.getModuleKey();
        }

        getSiteConfig(): SiteConfig {
            return this.siteConfig;
        }
    }
}