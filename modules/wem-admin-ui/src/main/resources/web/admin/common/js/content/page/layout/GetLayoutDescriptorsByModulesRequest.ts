module api.content.page.layout {

    import ModuleKey = api.module.ModuleKey;

    export class GetLayoutDescriptorsByModulesRequest extends LayoutDescriptorsResourceRequest {

        private moduleKeys: api.module.ModuleKey[];

        constructor(moduleKeys: api.module.ModuleKey[]) {
            super();
            this.moduleKeys = moduleKeys;
        }

        getParams(): Object {
            throw new Error("Unexpected call");
        }

        getRequestPath(): api.rest.Path {
            throw new Error("Unexpected call");
        }


        sendAndParse(): wemQ.Promise<LayoutDescriptor[]> {

            var promises: wemQ.Promise<LayoutDescriptor[]>[] = [];
            this.moduleKeys.forEach((moduleKey: ModuleKey) => {
                promises.push(new GetLayoutDescriptorsByModuleRequest(moduleKey).sendAndParse());
            });

            return wemQ.allSettled(promises).then((results: wemQ.PromiseState<LayoutDescriptor[]>[]) => {
                var all: LayoutDescriptor[] = [];
                results.forEach((result: wemQ.PromiseState<LayoutDescriptor[]>) => {
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