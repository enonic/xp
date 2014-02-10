module api.content.page.image {

    export class GetImageDescriptorsByModuleRequest extends ImageDescriptorResourceRequest<json.ImageDescriptorJson> {

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

        sendAndParse(): Q.Promise<ImageDescriptor> {

            var deferred = Q.defer<ImageDescriptor>();

            this.send().done((response: api.rest.JsonResponse<json.ImageDescriptorJson>) => {
                deferred.resolve(this.fromJsonToImageDescriptor(response.getResult()));
            }).fail((response: api.rest.RequestError) => {
                deferred.reject(null);
            });

            return deferred.promise;
        }
    }
}