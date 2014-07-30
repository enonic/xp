module api.module {

    export class ListModulesRequest extends ModuleResourceRequest<ModuleListResult> {

        constructor()
        {
            super();
            super.setMethod("GET");
        }

        getParams():Object {
            return {};
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): Q.Promise<api.module.ModuleSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<ModuleListResult>) => {
                return ModuleSummary.fromJsonArray(response.getResult().modules);
            });
        }
    }
}