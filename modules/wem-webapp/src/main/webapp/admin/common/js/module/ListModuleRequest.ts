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

        sendAndParse(): JQueryPromise<api.module.ModuleSummary[]> {

            var deferred = jQuery.Deferred<api.module.Module>();

            this.send().done((response: api.rest.JsonResponse<ModuleListResult>) => {
                if (response.getJson().error) {
                    deferred.reject(response.getJson().error);
                } else {
                    deferred.resolve(api.module.ModuleSummary.fromJsonArray(response.getResult().modules));
                }
            }).fail((response: api.rest.RequestError) => {
                        deferred.reject(null);
                    });

            return deferred;
        }
    }
}