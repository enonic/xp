module api.security {

    export class ResolveMembersRequest extends SecurityResourceRequest<any, ResolveMembersResult> {

        private keys: PrincipalKey[] = [];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKeys(keys: PrincipalKey[]): ResolveMembersRequest {
            this.keys = keys;
            return this;
        }

        addKeys(keys: PrincipalKey[]): ResolveMembersRequest {
            this.keys = this.keys.concat(keys);
            return this;
        }

        addKey(key: PrincipalKey): ResolveMembersRequest {
            this.keys.push(key);
            return this;
        }

        getParams(): Object {
            return {
                'keys': this.keys.map((key) => key.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals', 'resolveMembers');
        }

        sendAndParse(): wemQ.Promise<ResolveMembersResult> {
            return this.send().then((response: api.rest.JsonResponse<ResolveMembersResultJson>) => {
                return ResolveMembersResult.fromJson(response.getResult());
            });
        }

    }
}