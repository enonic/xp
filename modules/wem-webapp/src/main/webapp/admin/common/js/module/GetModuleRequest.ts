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

        sendAndParse(): JQueryPromise<api.module.Module> {

            var deferred = jQuery.Deferred<api.module.Module>();

            this.send().done((response: api.rest.JsonResponse<api.module.json.ModuleJson>) => {
                if (response.getJson().error) {
                    deferred.fail(response.getJson().error);
                } else {
                    deferred.resolve(this.fromJsonToModule(response.getResult()));
                }
            }).fail((response: api.rest.RequestError) => {
                        deferred.reject(null);
                    });

            return deferred;
        }
    }
}