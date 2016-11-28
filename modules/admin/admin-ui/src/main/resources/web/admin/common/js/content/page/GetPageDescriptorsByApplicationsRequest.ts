module api.content.page {

    import ApplicationKey = api.application.ApplicationKey;

    export class GetPageDescriptorsByApplicationsRequest extends PageDescriptorResourceRequest<PageDescriptorsJson, PageDescriptor[]> {

        private applicationKeys: ApplicationKey[];

        setApplicationKeys(applicationKeys: ApplicationKey[]) {
            this.applicationKeys = applicationKeys;
        }

        getParams(): Object {
            throw new Error("Unexpected call");
        }

        getRequestPath(): api.rest.Path {
            throw new Error("Unexpected call");
        }

        sendAndParse(): wemQ.Promise<PageDescriptor[]> {

            if (this.applicationKeys.length > 0) {
                var promises = this.applicationKeys.map(
                    (applicationKey: ApplicationKey) => new GetPageDescriptorsByApplicationRequest(applicationKey).sendAndParse());

                return wemQ.all(promises).then((results: PageDescriptor[][]) => {
                    var all: PageDescriptor[] = [];
                    results.forEach((descriptors: PageDescriptor[]) => {
                        Array.prototype.push.apply(all, descriptors);
                    });
                    return all;
                });
            } else {
                return wemQ.resolve([]);
            }
        }
    }
}