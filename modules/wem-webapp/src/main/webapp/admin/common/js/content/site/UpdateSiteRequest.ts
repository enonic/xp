module api_content_site {

    export class UpdateSiteRequest extends SiteResourceRequest<api_content_json.ContentJson> {

        private contentId: string;
        private siteTemplateKey: string;
        private moduleConfigs: ModuleConfig[];

        constructor(contentId: string) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        setSiteTemplateKey(siteTemplateKey: string): UpdateSiteRequest {
            this.siteTemplateKey = siteTemplateKey;
            return this;
        }

        setModuleConfigs(moduleConfigs: ModuleConfig[]): UpdateSiteRequest {
            this.moduleConfigs = moduleConfigs;
            return this;
        }

        getParams(): Object {

            return {
                contentId: this.contentId,
                siteTemplateKey: this.siteTemplateKey,
                moduleConfigs: this.moduleConfigs
            };
        }

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "update");
        }
    }
}