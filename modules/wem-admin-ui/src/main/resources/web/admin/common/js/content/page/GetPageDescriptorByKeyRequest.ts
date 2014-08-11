module api.content.page {

    export class GetPageDescriptorByKeyRequest extends PageDescriptorResourceRequest<PageDescriptorJson, PageDescriptor> {

        private key: DescriptorKey;

        constructor(key: DescriptorKey) {
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

        sendAndParse(): Q.Promise<PageDescriptor> {

            return this.send().then((response: api.rest.JsonResponse<PageDescriptorJson>) => {
                return this.fromJsonToPageDescriptor(response.getResult());
            });
        }
    }
}