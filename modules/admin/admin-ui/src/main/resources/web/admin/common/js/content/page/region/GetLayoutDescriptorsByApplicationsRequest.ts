module api.content.page.region {

    import ApplicationKey = api.application.ApplicationKey;

    export class GetLayoutDescriptorsByApplicationsRequest extends LayoutDescriptorsResourceRequest {

        private applicationKeys: api.application.ApplicationKey[];

        setApplicationKeys(applicationKeys: api.application.ApplicationKey[]) {
            this.applicationKeys = applicationKeys;
        }

        getParams(): Object {
            throw new Error('Unexpected call');
        }

        getRequestPath(): api.rest.Path {
            throw new Error('Unexpected call');
        }

        sendAndParse(): wemQ.Promise<LayoutDescriptor[]> {

            const req = (applicationKey: ApplicationKey) => new GetLayoutDescriptorsByApplicationRequest(applicationKey).sendAndParse();

            let promises = this.applicationKeys.map(req);

            return wemQ.all(promises).
                then((results: LayoutDescriptor[][]) => {
                    let all: LayoutDescriptor[] = [];
                    results.forEach((result: LayoutDescriptor[]) => {
                        Array.prototype.push.apply(all, result);
                    });
                    return all;
                });
        }
    }
}
