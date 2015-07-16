module api.module {

    export class UninstallModuleRequest extends ModuleResourceRequest<void, void> {

        private applicationKeys: ApplicationKey[];

        constructor(applicationKeys: ApplicationKey[]) {
            super();
            super.setMethod("POST");
            this.applicationKeys = applicationKeys;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "uninstall");
        }

        getParams(): Object {
            return {
                key: ApplicationKey.toStringArray(this.applicationKeys)
            };
        }

        sendAndParse(): wemQ.Promise<void> {

            return this.send().then((response: api.rest.JsonResponse<void>) => {

            });
        }
    }
}