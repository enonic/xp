module api_content_site_template {

    export class GetSiteTemplateRequest extends SiteTemplateResourceRequest<api_content_site_template_json.SiteTemplateSummaryJson> {

        private siteTemplateId:string;

        constructor(siteTemplateId:string) {
            super();
            super.setMethod("GET");
            this.siteTemplateId = siteTemplateId;
        }

        getParams():Object {
            return {
                siteTemplateId: this.siteTemplateId
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "get");
        }
    }
}