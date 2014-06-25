module api.module {

    export class StopModuleRequest extends ModuleResourceRequest<api.module.json.ModuleJson> {

        private moduleKeys: string[];

        constructor(moduleKeys:string[]) {
            super();
            super.setMethod("POST");
            this.moduleKeys = moduleKeys;
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "stop");
        }

        getParams():Object {
            return {
                key: this.moduleKeys
            };
        }

        sendAndParse(): Q.Promise<api.module.Module> {

            return this.send().then((response: api.rest.JsonResponse<api.module.json.ModuleJson>) => {
                return this.fromJsonToModule(response.getResult());
            });
        }
    }
}