module api.module {

    export class ListModuleRequest extends ModuleResourceRequest<ModuleListResult> {

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

            var deferred = Q.defer<api.module.ModuleSummary[]>();

            this.send().
                done((response: api.rest.JsonResponse<ModuleListResult>) => {

                deferred.resolve(ModuleSummary.fromJsonArray(response.getResult().modules));
            });

            return deferred.promise;
        }
    }
}