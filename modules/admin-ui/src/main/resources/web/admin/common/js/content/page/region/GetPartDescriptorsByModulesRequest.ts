module api.content.page.region {

    import ModuleKey = api.module.ModuleKey;

    export class GetPartDescriptorsByModulesRequest extends PartDescriptorsResourceRequest {

        private moduleKeys: ModuleKey[];

        constructor(moduleKeys: ModuleKey[]) {
            super();
            this.moduleKeys = moduleKeys;
        }

        setModuleKeys(moduleKeys: ModuleKey[]) {
            this.moduleKeys = moduleKeys;
        }

        getParams(): Object {
            throw new Error("Unexpected call");
        }

        getRequestPath(): api.rest.Path {
            throw new Error("Unexpected call");
        }

        sendAndParse(): wemQ.Promise<PartDescriptor[]> {

            var promises = this.moduleKeys.map((moduleKey: ModuleKey) => new GetPartDescriptorsByModuleRequest(moduleKey).sendAndParse());

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