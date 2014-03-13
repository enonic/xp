module api.content.site {

    export class Site {

        private templateKey: api.content.site.template.SiteTemplateKey;

        private moduleConfigs: ModuleConfig[] = [];

        constructor(siteJson: api.content.site.SiteJson) {
            this.templateKey = api.content.site.template.SiteTemplateKey.fromString(siteJson.templateName);

            if (siteJson.moduleConfigs != null) {
                siteJson.moduleConfigs.forEach((moduleConfigJson: api.content.site.ModuleConfigJson) => {
                    this.moduleConfigs.push(new ModuleConfigBuilder().setFromJson(moduleConfigJson).build());
                });
            }
        }

        getTemplateKey(): api.content.site.template.SiteTemplateKey {
            return this.templateKey;
        }

        getModuleConfigs(): ModuleConfig[] {
            return this.moduleConfigs;
        }
    }
}