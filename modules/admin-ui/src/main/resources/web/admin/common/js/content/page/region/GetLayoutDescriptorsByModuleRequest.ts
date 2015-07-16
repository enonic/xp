module api.content.page.region {

    export class GetLayoutDescriptorsByModuleRequest extends LayoutDescriptorsResourceRequest {

        private applicationKey: api.module.ApplicationKey;

        constructor(applicationKey: api.module.ApplicationKey) {
            super();
            super.setMethod("GET");
            this.applicationKey = applicationKey;
        }

        getParams(): Object {
            return {
                applicationKey: this.applicationKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list", "by_module");
        }

        sendAndParse(): wemQ.Promise<LayoutDescriptor[]> {

            var cached = this.cache.getByModule(this.applicationKey);
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