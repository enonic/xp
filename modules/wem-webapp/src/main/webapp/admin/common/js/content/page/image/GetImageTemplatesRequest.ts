module api_content_page_image{

    export class GetImageTemplatesRequest extends ImageTemplateResource<api_content_page_image_json.ImageTemplateSummaryListJson> {

        private siteTemplateKey: api_content_site.SiteTemplateKey;

        constructor(siteTemplateKey: api_content_site.SiteTemplateKey) {
            super();
            super.setMethod("POST");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams():Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "image/template/list");
        }
    }
}
