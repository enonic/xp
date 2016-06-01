module api.security {

    export class SynchUserStoreRequest extends SecurityResourceRequest<SynchUserStoreResultsJson, SynchUserStoreResult[]> {

        private keys: UserStoreKey[];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKeys(keys: UserStoreKey[]): SynchUserStoreRequest {
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

        sendAndParse(): wemQ.Promise<SynchUserStoreResult[]> {
            return this.send().then((response: api.rest.JsonResponse<SynchUserStoreResultsJson>) => {
                return response.getResult().results.map((resultJson) => SynchUserStoreResult.fromJson(resultJson));
            });
        }

    }
}