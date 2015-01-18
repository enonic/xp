module api.module {

    export class ListModulesRequest extends ModuleResourceRequest<ModuleListResult, Module[]> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams(): Object {
            return {};
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): wemQ.Promise<Module[]> {

            return this.send().then((response: api.rest.JsonResponse<ModuleListResult>) => {
                return Module.fromJsonArray(response.getResult().modules);
            });
        }
    }
}