module api.security {

    export class SyncUserStoreRequest extends SecurityResourceRequest<SyncUserStoreResultsJson, SyncUserStoreResult[]> {

        private keys: UserStoreKey[];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKeys(keys: UserStoreKey[]): SyncUserStoreRequest {
            this.keys = keys.slice(0);
            return this;
        }

        getParams(): Object {
            return {
                keys: this.keys.map((memberKey) => memberKey.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'userstore', 'sync');
        }

        sendAndParse(): wemQ.Promise<SyncUserStoreResult[]> {
            return this.send().then((response: api.rest.JsonResponse<SyncUserStoreResultsJson>) => {
                return response.getResult().results.map((resultJson) => SyncUserStoreResult.fromJson(resultJson));
            });
        }

    }
}
