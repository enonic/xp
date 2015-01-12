module api.content.page.region {

    import ModuleKey = api.module.ModuleKey;

    export class GetLayoutDescriptorsByModulesRequest extends LayoutDescriptorsResourceRequest {

        private moduleKeys: api.module.ModuleKey[];

        constructor(moduleKeys: api.module.ModuleKey[]) {
            super();
            this.moduleKeys = moduleKeys;
        }

        setModuleKeys(moduleKeys: api.module.ModuleKey[]) {
            this.moduleKeys = moduleKeys;
        }

        getParams(): Object {
            throw new Error("Unexpected call");
        }

        getRequestPath(): api.rest.Path {
            throw new Error("Unexpected call");
        }


        sendAndParse(): wemQ.Promise<LayoutDescriptor[]> {

            var promises = this.moduleKeys.map((moduleKey: ModuleKey) => new GetLayoutDescriptorsByModuleRequest(moduleKey).sendAndParse());

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