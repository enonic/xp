module api.content.page.part {

    import ModuleKey = api.module.ModuleKey;

    export class GetPartDescriptorsByModulesRequest extends PartDescriptorsResourceRequest {

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

        sendAndParse(): wemQ.Promise<PartDescriptor[]> {

            var promises: wemQ.Promise<PartDescriptor[]>[] = [];
            this.moduleKeys.forEach((moduleKey: ModuleKey) => {
                promises.push(new GetPartDescriptorsByModuleRequest(moduleKey).sendAndParse());
            });

            return wemQ.allSettled(promises).then((results: wemQ.PromiseState<PartDescriptor[]>[]) => {
                var all: PartDescriptor[] = [];
                results.forEach((result: wemQ.PromiseState<PartDescriptor[]>) => {
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