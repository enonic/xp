module api_content_page_layout {

    export class GetLayoutTemplatesRequest extends LayoutTemplateResourceRequest {

        private siteTemplateKey:string;

        constructor(siteTemplateKey:string) {
            super();
            super.setMethod("GET");
            this.siteTemplateKey = siteTemplateKey;
        }

        getParams():Object {
            return {
                key: this.siteTemplateKey
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "list");
        }
    }

}