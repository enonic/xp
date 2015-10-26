module api.security {

    export class ResolveMembershipsRequest extends SecurityResourceRequest<any, ResolveMembershipsResult> {

        private keys: PrincipalKey[] = [];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKeys(keys: PrincipalKey[]): ResolveMembershipsRequest {
            this.keys = keys;
            return this;
        }

        addKey(key: PrincipalKey): ResolveMembershipsRequest {
            this.keys.push(key);
            return this;
        }

        getParams(): Object {
            return {
                'keys': this.keys.map((key) => key.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals', 'resolveMemberships');
        }

        sendAndParse(): wemQ.Promise<ResolveMembershipsResult> {
            return this.send().then((response: api.rest.JsonResponse<ResolveMembershipsResultJson>) => {
                return ResolveMembershipsResult.fromJson(response.getResult());
            });
        }

    }
}