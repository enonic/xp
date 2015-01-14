module api.content.page.region {

    export class GetLayoutDescriptorsByModuleRequest extends LayoutDescriptorsResourceRequest {

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

        sendAndParse(): wemQ.Promise<LayoutDescriptor[]> {

            var cached = this.cache.getByModule(this.moduleKey);
            if (cached) {
                return wemQ(cached);
            }
            else {
                return this.send().then((response: api.rest.JsonResponse<LayoutDescriptorsJson>) => {
                    return this.fromJsonToLayoutDescriptors(response.getResult());
                });
            }
        }
    }
}