module api.module {

    export class StartModuleRequest extends ModuleResourceRequest<void, void> {

        private moduleKeys: ModuleKey[];

        constructor(moduleKeys: ModuleKey[]) {
            super();
            super.setMethod("POST");
            this.moduleKeys = moduleKeys;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "start");
        }

        getParams(): Object {
            return {
                key: ModuleKey.toStringArray(this.moduleKeys)
            };
        }

        sendAndParse(): wemQ.Promise<void> {
            return this.send().then((response: api.rest.JsonResponse<void>) => {

            });
        }
    }
}