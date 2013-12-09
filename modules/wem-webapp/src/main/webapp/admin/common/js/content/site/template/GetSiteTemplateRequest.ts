module api_content_site_template {

    export class GetSiteTemplateRequest extends SiteTemplateResourceRequest<api_content_site_template_json.SiteTemplateSummaryJson> {

        private siteTemplateKey:SiteTemplateKey;

        constructor(siteTemplateId:SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateId;
        }

        getParams():Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "get");
        }
    }
}