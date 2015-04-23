module api.security {

    export class DeleteUserStoreRequest extends SecurityResourceRequest<DeleteUserStoreResultsJson, DeleteUserStoreResult[]> {

        private keys: UserStoreKey[];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKeys(keys: UserStoreKey[]): DeleteUserStoreRequest {
            this.keys = keys.slice(0);
            return this;
        }

        getParams(): Object {
            return {
                keys: this.keys.map((memberKey) => memberKey.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'userstore', 'delete');
        }

        sendAndParse(): wemQ.Promise<DeleteUserStoreResult[]> {
            return this.send().then((response: api.rest.JsonResponse<DeleteUserStoreResultsJson>) => {
                return response.getResult().results.map((resultJson) => DeleteUserStoreResult.fromJson(resultJson));
            });
        }

    }
}