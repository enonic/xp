module api.security {

    export class DeletePathGuardRequest extends SecurityResourceRequest<DeletePrincipalResultsJson, DeletePrincipalResult[]> {

        private keys: string[];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKeys(keys: string[]): DeletePathGuardRequest {
            this.keys = keys;
            return this;
        }

        getParams(): Object {
            return this.keys;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'pathguard', 'delete');
        }

        sendAndParse(): wemQ.Promise<DeletePrincipalResult[]> {

            return this.send().then((response: api.rest.JsonResponse<DeletePrincipalResultsJson>) => {
                return response.getResult().results.map((resultJson) => DeletePrincipalResult.fromJson(resultJson));
            });
        }

    }
}