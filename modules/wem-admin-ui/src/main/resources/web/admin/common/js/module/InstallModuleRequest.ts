module api.module {

    export class InstallModuleRequest extends ModuleResourceRequest<api.module.json.ModuleJson> {

        private moduleUrl: string;

        constructor(moduleUrl: string) {
            super();
            super.setMethod("POST");
            this.moduleUrl = moduleUrl;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "install");
        }

        getParams(): Object {
            return {
                url: this.moduleUrl
            };
        }

        sendAndParse(): Q.Promise<api.module.Module> {

            return this.send().then((response: api.rest.JsonResponse<api.module.json.ModuleJson>) => {
                return this.fromJsonToModule(response.getResult());
            });
        }
    }
}