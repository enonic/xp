module api_content_page {

    export class GetPageTemplateByKeyRequest extends PageTemplateResourceRequest<api_content_page_json.PageTemplateJson> {

        private key:PageTemplateKey;

        constructor(key:PageTemplateKey) {
            super();
            super.setMethod("GET");
            this.key = key;
        }

        getParams():Object {
            return {
                key: this.key.toString(),
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}