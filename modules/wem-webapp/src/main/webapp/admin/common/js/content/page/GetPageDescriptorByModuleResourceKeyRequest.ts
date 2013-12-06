module api_content_page {

    export class GetPageDescriptorByModuleResourceKeyRequest extends PageDescriptorResourceRequest<api_content_page_json.PageDescriptorJson> {

        private key:api_module.ModuleResourceKey;

        constructor(key:api_module.ModuleResourceKey) {
            super();
            super.setMethod("GET");
            this.key = key;
        }

        getParams():Object {
            return {
                key: this.key.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}