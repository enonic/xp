module api_content_site {

    export class CreateSiteRequest extends SiteResourceRequest<api_content_site_json.SiteJson> {

        private contentId:string;
        private siteTemplateKey:string;
        private moduleConfigs:ModuleConfig[];

        constructor(contentId:string) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        setSiteTemplateKey(siteTemplateKey:string):CreateSiteRequest {
            this.siteTemplateKey = siteTemplateKey;
            return this;
        }

        setModuleConfigs(moduleConfigs:ModuleConfig[]):CreateSiteRequest {
            this.moduleConfigs = moduleConfigs;
            return this;
        }

        getParams():Object {
            return {
                contentId: this.contentId,
                siteTemplateKey: this.siteTemplateKey,
                moduleConfigs: this.moduleConfigs
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "create");
        }
    }
}