module api.module {

    export class GetModuleRequest extends ModuleResourceRequest<json.ModuleJson, Module> {

        private moduleKey:ModuleKey;

        constructor(moduleKey:ModuleKey) {
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

        sendAndParse(): Q.Promise<Module> {

            return this.send().then((response: api.rest.JsonResponse<json.ModuleJson>) => {
                return this.fromJsonToModule(response.getResult());
            });
        }
    }
}