module api.module {

    export class StopModuleRequest extends ModuleResourceRequest<api.item.ItemJson, void> {

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

        sendAndParse(): wemQ.Promise<void> {

            return this.send().then((response: api.rest.JsonResponse<api.item.ItemJson>) => {

            });
        }
    }
}