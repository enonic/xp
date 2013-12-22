module api_content_site {

    export class Site {

        private templateKey: api_content_site_template.SiteTemplateKey;

        private moduleConfigs: ModuleConfig[] = [];

        constructor(siteJson: api_content_site_json.SiteJson) {
            this.templateKey = api_content_site_template.SiteTemplateKey.fromString(siteJson.templateName);

            if (siteJson.moduleConfigs != null) {
                siteJson.moduleConfigs.forEach((moduleConfigJson: api_content_site_json.ModuleConfigJson) => {
                    this.moduleConfigs.push(new ModuleConfigBuilder().setFromJson(moduleConfigJson).build());
                });
            }
        }

        getTemplateKey(): api_content_site_template.SiteTemplateKey {
            return this.templateKey;
        }

        getModuleConfigs(): ModuleConfig[] {
            return this.moduleConfigs;
        }
    }
}