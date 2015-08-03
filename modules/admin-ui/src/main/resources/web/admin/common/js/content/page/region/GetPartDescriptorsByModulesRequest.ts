module api.content.page.region {

    import ApplicationKey = api.application.ApplicationKey;

    export class GetPartDescriptorsByModulesRequest extends PartDescriptorsResourceRequest {

        private applicationKeys: ApplicationKey[];

        constructor(applicationKeys: ApplicationKey[]) {
            super();
            this.applicationKeys = applicationKeys;
        }

        setApplicationKeys(applicationKeys: ApplicationKey[]) {
            this.applicationKeys = applicationKeys;
        }

        getParams(): Object {
            throw new Error("Unexpected call");
        }

        getRequestPath(): api.rest.Path {
            throw new Error("Unexpected call");
        }

        sendAndParse(): wemQ.Promise<PartDescriptor[]> {

            var promises = this.applicationKeys.map((applicationKey: ApplicationKey) => new GetPartDescriptorsByModuleRequest(applicationKey).sendAndParse());

            return wemQ.all(promises).then((results: PartDescriptor[][]) => {
                var all: PartDescriptor[] = [];
                results.forEach((result: PartDescriptor[]) => {
                    Array.prototype.push.apply(all, result);
                });
                return all;
            });
        }
    }
}