module api.content.page.image {

    export class GetImageDescriptorsByModuleRequest extends ImageDescriptorResourceRequest<ImageDescriptorJson> {

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

            this.send().then((response: api.rest.JsonResponse<ImageDescriptorJson>) => {
                deferred.resolve(this.fromJsonToImageDescriptor(response.getResult()));
            }).catch((response: api.rest.RequestError) => {
                deferred.reject(null);
            }).done();

            return deferred.promise;
        }
    }
}