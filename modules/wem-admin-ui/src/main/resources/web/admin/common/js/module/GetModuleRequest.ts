module api.module {

    export class GetModuleRequest extends ModuleResourceRequest<json.ModuleJson, Module> {

        private moduleKey: ModuleKey;

        constructor(moduleKey: ModuleKey) {
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
            return api.rest.Path.fromParent(super.getResourcePath());
        }

        sendAndParse(): wemQ.Promise<Module> {

            var cache = ModuleCache.get();
            var cachedObject = cache.getByKey(this.moduleKey);
            if (cachedObject) {
                return Q(cachedObject);
            }
            else {
                return this.send().then((response: api.rest.JsonResponse<json.ModuleJson>) => {
                    var moduleObj = this.fromJsonToModule(response.getResult());
                    cache.put(moduleObj);
                    return moduleObj;
                });
            }
        }
    }
}