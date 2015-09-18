module api.content.page.region {

    import ApplicationKey = api.application.ApplicationKey;

    export class GetPartDescriptorByKeyRequest extends PartDescriptorResourceRequest<PartDescriptorJson,PartDescriptor> {

        private key: api.content.page.DescriptorKey;

        constructor(key: api.content.page.DescriptorKey) {
            super();
            this.key = key;
        }

        setKey(key: api.content.page.DescriptorKey) {
            this.key = key;
        }

        getParams(): Object {
            throw new Error("Unexpected call");
        }

        getRequestPath(): api.rest.Path {
            throw new Error("Unexpected call");
        }

        sendAndParse(): wemQ.Promise<PartDescriptor> {
            var deferred = wemQ.defer<PartDescriptor>();

            new GetPartDescriptorsByApplicationRequest(this.key.getApplicationKey()).sendAndParse().then((descriptors: PartDescriptor[]) => {
                descriptors.forEach((descriptor: PartDescriptor) => {
                    if (this.key.equals(descriptor.getKey())) {
                        deferred.resolve(descriptor);
                    }
                });
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return deferred.promise;
        }
    }
}