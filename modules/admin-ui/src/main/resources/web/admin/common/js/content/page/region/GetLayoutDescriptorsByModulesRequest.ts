module api.content.page.region {

    import ApplicationKey = api.module.ApplicationKey;

    export class GetLayoutDescriptorsByModulesRequest extends LayoutDescriptorsResourceRequest {

        private applicationKeys: api.module.ApplicationKey[];

        constructor(applicationKey: api.module.ApplicationKey[]) {
            super();
            this.applicationKeys = applicationKey;
        }

        setApplicationKeys(applicationKeys: api.module.ApplicationKey[]) {
            this.applicationKeys = applicationKeys;
        }

        getParams(): Object {
            throw new Error("Unexpected call");
        }

        getRequestPath(): api.rest.Path {
            throw new Error("Unexpected call");
        }


        sendAndParse(): wemQ.Promise<LayoutDescriptor[]> {

            var promises = this.applicationKeys.map((applicationKey: ApplicationKey) => new GetLayoutDescriptorsByModuleRequest(applicationKey).sendAndParse());

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