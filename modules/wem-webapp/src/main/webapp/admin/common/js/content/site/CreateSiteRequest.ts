module api_content_site {

    export class CreateSiteRequest extends SiteResourceRequest<api_content_json.ContentJson> {

        private contentId: string;
        private siteTemplateKey: api_content_site_template.SiteTemplateKey;
        private moduleConfigs: ModuleConfig[];

        constructor(contentId: string) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        setSiteTemplateKey(siteTemplateKey: api_content_site_template.SiteTemplateKey): CreateSiteRequest {
            this.siteTemplateKey = siteTemplateKey;
            return this;
        }

        setModuleConfigs(moduleConfigs: ModuleConfig[]): CreateSiteRequest {
            this.moduleConfigs = moduleConfigs;
            return this;
        }

        getParams(): Object {
            var moduleConfigsJson = [];
            for (var i = 0; i < this.moduleConfigs.length; i++) {
                moduleConfigsJson.push(this.moduleConfigs[i].toJson());
            }
            return {
                contentId: this.contentId,
                siteTemplateKey: this.siteTemplateKey.toString(),
                moduleConfigs: moduleConfigsJson
            };
        }

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "create");
        }
    }
}