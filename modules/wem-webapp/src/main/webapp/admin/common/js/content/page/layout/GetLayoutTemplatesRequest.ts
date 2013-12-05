module api_content_page_layout {

    export class GetLayoutTemplatesRequest extends LayoutTemplateResourceRequest {

        private siteTemplateKey:api_content_site_template.SiteTemplateKey;

        constructor(siteTemplateKey:api_content_site_template.SiteTemplateKey) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams():Object {
            return {
                key: this.siteTemplateKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "list");
        }
    }

}