module api.module {

    export class GetModuleRequest extends ModuleResourceRequest<json.ModuleJson, Application> {

        private applicationKey: ApplicationKey;

        private skipCache: boolean;

        constructor(applicationKey: ApplicationKey, skipCache: boolean = false) {
            super();
            super.setMethod("GET");
            this.applicationKey = applicationKey;
            this.skipCache = skipCache;
        }

        getParams(): Object {
            return {
                applicationKey: this.applicationKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath());
        }

        sendAndParse(): wemQ.Promise<Application> {

            var cache = ModuleCache.get();
            var moduleObj = this.skipCache ? null : cache.getByKey(this.applicationKey);
            if (moduleObj) {
                return wemQ(moduleObj);
            }
            else {
                return this.send().then((response: api.rest.JsonResponse<json.ModuleJson>) => {
                    moduleObj = this.fromJsonToModule(response.getResult());
                    cache.put(moduleObj);
                    return moduleObj;
                });
            }
        }
    }
}