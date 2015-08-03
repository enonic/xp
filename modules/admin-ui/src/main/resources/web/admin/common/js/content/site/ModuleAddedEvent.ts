module api.content.site {
    export class ModuleAddedEvent {

        private siteConfig: SiteConfig;

        constructor(siteConfig: SiteConfig) {
            this.siteConfig = siteConfig;
        }

        getApplicationKey(): api.application.ApplicationKey {
            return this.siteConfig.getApplicationKey();
        }

        getSiteConfig(): SiteConfig {
            return this.siteConfig;
        }
    }
}