module api.content.page {

    export class GetPageDescriptorsByModuleRequest extends PageDescriptorResourceRequest<PageDescriptorsJson, PageDescriptor[]> {

        private moduleKey: api.module.ModuleKey;

        constructor(moduleKey: api.module.ModuleKey) {
            super();
            super.setMethod("GET");
            this.moduleKey = moduleKey;
        }

        getParams(): Object {
            return {
                moduleKey: this.moduleKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list", "by_module");
        }

        sendAndParse(): wemQ.Promise<PageDescriptor[]> {

            var cached = this.cache.getByModule(this.moduleKey);
            if (cached) {
                return wemQ(cached);
            }
            else {
                return this.send().then((response: api.rest.JsonResponse<PageDescriptorsJson>) => {
                    return this.fromJsonToPageDescriptors(response.getResult());
                });
            }
        }
    }
}