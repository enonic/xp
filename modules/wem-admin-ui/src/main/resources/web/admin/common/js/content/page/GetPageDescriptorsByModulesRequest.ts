module api.content.page {

    import ModuleKey = api.module.ModuleKey;

    export class GetPageDescriptorsByModulesRequest extends PageDescriptorResourceRequest<PageDescriptorsJson, PageDescriptor[]> {

        private moduleKeys: ModuleKey[];

        constructor(moduleKeys: ModuleKey[]) {
            super();
            this.moduleKeys = moduleKeys;
        }

        getParams(): Object {
            throw new Error("Unexpected call");
        }

        getRequestPath(): api.rest.Path {
            throw new Error("Unexpected call");
        }

        sendAndParse(): wemQ.Promise<PageDescriptor[]> {

            var promises: wemQ.Promise<PageDescriptor[]>[] = [];
            this.moduleKeys.forEach((moduleKey: ModuleKey) => {
                promises.push(new GetPageDescriptorsByModuleRequest(moduleKey).sendAndParse());
            });

            return wemQ.allSettled(promises).then((results: wemQ.PromiseState<PageDescriptor[]>[]) => {
                var all: PageDescriptor[] = [];
                results.forEach((result: wemQ.PromiseState<PageDescriptor[]>) => {
                    if (result.state == "fulfilled") {
                        var descriptors = result.value;
                        all = all.concat(descriptors);
                    }
                    else {
                        throw new Error("Unexpected Promise state [" + result.state + "]: " + result.reason.message);
                    }
                });
                return all;
            });
        }
    }
}