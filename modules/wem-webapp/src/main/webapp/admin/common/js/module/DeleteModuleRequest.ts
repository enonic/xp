module api.module {

    export class DeleteModuleRequest extends ModuleResourceRequest<api.module.json.ModuleJson> {

        private key:string;

        constructor(key:string) {
            super();
            super.setMethod("POST");
            this.key = key;
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "delete");
        }

        getParams():Object {
            return {
                key: this.key
            };
        }

        sendAndParse(): Q.Promise<api.module.Module> {
            var deferred = Q.defer<api.module.Module>();

            this.send().
                done((response: api.rest.JsonResponse<api.module.json.ModuleJson>) => {
                deferred.resolve(this.fromJsonToModule(response.getResult()));
            });

            return deferred.promise;
        }
    }
}