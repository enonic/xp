module api_content_page{

    export class GetPageTemplatesRequest extends PageTemplateResourceRequest<api_content_page_json.PageTemplateListJson> {

        private siteTemplateKey:api_content_site_template.SiteTemplateKey;

        constructor(siteTemplateKey:api_content_site_template.SiteTemplateKey) {
            super();
            super.setMethod("GET");

            this.siteTemplateKey = siteTemplateKey;
        }

        getParams():Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "list");
        }
    }
}
