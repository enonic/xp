module api.security {

    export class DeletePathGuardRequest extends SecurityResourceRequest<DeletePathGuardResultsJson, DeletePathGuardResult[]> {

        private keys: api.security.PathGuardKey[];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKeys(keys: api.security.PathGuardKey[]): DeletePathGuardRequest {
            this.keys = keys;
            return this;
        }

        getParams(): Object {
            return {
                keys: this.keys.map(pathGuardKey => pathGuardKey.toString())
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'pathguard', 'delete');
        }

        sendAndParse(): wemQ.Promise<DeletePathGuardResult[]> {

            return this.send().then((response: api.rest.JsonResponse<DeletePathGuardResultsJson>) => {
                return response.getResult().results.map((resultJson) => DeletePathGuardResult.fromJson(resultJson));
            });
        }

    }
}