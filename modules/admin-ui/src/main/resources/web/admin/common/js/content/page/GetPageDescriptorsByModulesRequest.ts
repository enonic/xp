module api.content.page {

    import ModuleKey = api.module.ModuleKey;

    export class GetPageDescriptorsByModulesRequest extends PageDescriptorResourceRequest<PageDescriptorsJson, PageDescriptor[]> {

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

        sendAndParse(): wemQ.Promise<PageDescriptor[]> {

            var promises = this.moduleKeys.map((moduleKey: ModuleKey) => new GetPageDescriptorsByModuleRequest(moduleKey).sendAndParse());

            return wemQ.all(promises).then((results: PageDescriptor[][]) => {
                var all: PageDescriptor[] = [];
                results.forEach((descriptors: PageDescriptor[]) => {
                    Array.prototype.push.apply(all, descriptors);
                });
                return all;
            });
        }
    }
}