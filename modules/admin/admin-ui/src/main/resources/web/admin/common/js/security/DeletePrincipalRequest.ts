module api.security {

    export class DeletePrincipalRequest extends SecurityResourceRequest<DeletePrincipalResultsJson, DeletePrincipalResult[]> {

        private keys: PrincipalKey[];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKeys(keys: PrincipalKey[]): DeletePrincipalRequest {
            this.keys = keys.slice(0);
            return this;
        }

        getParams(): Object {
            return {
                keys: this.keys.map((memberKey) => memberKey.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals', 'delete');
        }

        sendAndParse(): wemQ.Promise<DeletePrincipalResult[]> {

            return this.send().then((response: api.rest.JsonResponse<DeletePrincipalResultsJson>) => {
                return response.getResult().results.map((resultJson) => DeletePrincipalResult.fromJson(resultJson));
            });
        }

    }
}