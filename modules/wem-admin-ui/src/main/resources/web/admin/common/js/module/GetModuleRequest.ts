module api.module {

    export class GetModuleRequest extends ModuleResourceRequest<api.module.json.ModuleJson> {

        private moduleKey:api.module.ModuleKey;

        constructor(moduleKey:api.module.ModuleKey) {
            super();
            super.setMethod("GET");
            this.moduleKey = moduleKey;
        }

        getParams():Object {
            return {
                moduleKey: this.moduleKey.toString()
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath());
        }

        sendAndParse(): Q.Promise<api.module.Module> {

            return this.send().then((response: api.rest.JsonResponse<api.module.json.ModuleJson>) => {
                return this.fromJsonToModule(response.getResult());
            });
        }
    }
}