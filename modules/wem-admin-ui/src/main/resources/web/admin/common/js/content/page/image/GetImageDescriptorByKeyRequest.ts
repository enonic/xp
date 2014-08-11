module api.content.page.image {

    export class GetImageDescriptorsByModuleRequest extends ImageDescriptorResourceRequest<ImageDescriptorJson, ImageDescriptor> {

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

            return this.send().then((response: api.rest.JsonResponse<ImageDescriptorJson>) => {
                return this.fromJsonToImageDescriptor(response.getResult());
            });
        }
    }
}