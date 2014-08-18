module api.module {

    export class UpdateModuleRequest extends ModuleResourceRequest<api.item.ItemJson, void> {

        private moduleKeys: string[];

        constructor(moduleKeys:string[]) {
            super();
            super.setMethod("POST");
            this.moduleKeys = moduleKeys;
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "update");
        }

        getParams():Object {
            return {
                key: this.moduleKeys
            };
        }

        sendAndParse(): Q.Promise<void> {

            return this.send().then((response: api.rest.JsonResponse<api.item.ItemJson>) => {

            });
        }
    }
}