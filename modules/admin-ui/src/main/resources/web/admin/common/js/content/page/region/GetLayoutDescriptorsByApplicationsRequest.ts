module api.content.page.region {

    import ApplicationKey = api.application.ApplicationKey;

    export class GetLayoutDescriptorsByApplicationsRequest extends LayoutDescriptorsResourceRequest {

        private applicationKeys: api.application.ApplicationKey[];

        constructor(applicationKey: api.application.ApplicationKey[]) {
            super();
            this.applicationKeys = applicationKey;
        }

        setApplicationKeys(applicationKeys: api.application.ApplicationKey[]) {
            this.applicationKeys = applicationKeys;
        }

        getParams(): Object {
            throw new Error("Unexpected call");
        }

        getRequestPath(): api.rest.Path {
            throw new Error("Unexpected call");
        }


        sendAndParse(): wemQ.Promise<LayoutDescriptor[]> {

            var promises = this.applicationKeys.map((applicationKey: ApplicationKey) => new GetLayoutDescriptorsByApplicationRequest(applicationKey).sendAndParse());

            return wemQ.all(promises).
                then((results: LayoutDescriptor[][]) => {
                    var all: LayoutDescriptor[] = [];
                    results.forEach((result: LayoutDescriptor[]) => {
                        Array.prototype.push.apply(all, result);
                    });
                    return all;
                });
        }
    }
}