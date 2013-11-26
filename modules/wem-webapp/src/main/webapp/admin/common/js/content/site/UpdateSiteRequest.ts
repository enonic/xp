module api_content_site {

    export class UpdateSiteRequest extends SiteResourceRequest<api_content_site_json.SiteJson> {

        private contentId:string;
        private siteTemplateId:string;
        private moduleConfigs:ModuleConfig[];

        constructor(contentId:string) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        setSiteTemplateId(siteTemplateId:string):UpdateSiteRequest {
            this.siteTemplateId = siteTemplateId;
            return this;
        }

        setModuleConfigs(moduleConfigs:ModuleConfig[]):UpdateSiteRequest {
            this.moduleConfigs = moduleConfigs;
            return this;
        }

        getParams():Object {

            return {
                contentId: this.contentId,
                siteTemplateId: this.siteTemplateId,
                moduleConfigs: this.moduleConfigs
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "update");
        }
    }
}