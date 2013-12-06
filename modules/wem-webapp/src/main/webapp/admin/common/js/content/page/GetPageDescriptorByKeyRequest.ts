module api_content_page {

    export class GetPageDescriptorByKeyRequest extends PageDescriptorResourceRequest<api_content_page_json.PageDescriptorJson> {

        private key: api_module.ModuleResourceKey;

        constructor(key: api_module.ModuleResourceKey) {
            super();
            super.setMethod("GET");
            this.key = key;
        }

        getParams(): Object {
            return {
                key: this.key.toString()
            };
        }

        getRequestPath(): api_rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): JQueryPromise<api_content_page.PageDescriptor> {

            var deferred: JQueryDeferred<api_content_page.PageDescriptor> = jQuery.Deferred<api_content_page.PageDescriptor>();

            this.send().done((response: api_rest.JsonResponse<api_content_page_json.PageDescriptorJson>) => {
                deferred.resolve(this.fromJsonToPageDescriptor(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}