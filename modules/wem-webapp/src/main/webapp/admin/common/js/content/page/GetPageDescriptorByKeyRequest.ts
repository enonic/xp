module api.content.page {

    export class GetPageDescriptorByKeyRequest extends PageDescriptorResourceRequest<api.content.page.json.PageDescriptorJson> {

        private key: api.module.ModuleResourceKey;

        constructor(key: api.module.ModuleResourceKey) {
            super();
            super.setMethod("GET");
            this.key = key;
        }

        getParams(): Object {
            return {
                key: this.key.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): Q.Promise<api.content.page.PageDescriptor> {

            var deferred = Q.defer<api.content.page.PageDescriptor>();

            this.send().done((response: api.rest.JsonResponse<api.content.page.json.PageDescriptorJson>) => {
                deferred.resolve(this.fromJsonToPageDescriptor(response.getResult()));
            }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred.promise;
        }
    }
}