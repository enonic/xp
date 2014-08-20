module api.module {

    export class InstallModuleRequest extends ModuleResourceRequest<api.item.ItemJson, void> {

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

        sendAndParse(): wemQ.Promise<void> {

            return this.send().then((response: api.rest.JsonResponse<api.item.ItemJson>) => {

            });
        }
    }
}