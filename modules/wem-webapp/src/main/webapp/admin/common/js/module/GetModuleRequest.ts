module api_module {

    export class GetModuleRequest extends ModuleResourceRequest<api_module_json.ModuleJson> {

        private moduleKey:api_module.ModuleKey;

        constructor(moduleKey:api_module.ModuleKey) {
            super();
            super.setMethod("GET");
            this.moduleKey = moduleKey;
        }

        getParams():Object {
            return {
                moduleKey: this.moduleKey.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath());
        }
    }
}