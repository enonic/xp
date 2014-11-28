module api.security {

    export class GetPrincipalByKeyRequest extends SecurityResourceRequest<PrincipalJson, Principal> {

        private principalKey: PrincipalKey;

        private includeMemberships: boolean;

        constructor(principalKey: PrincipalKey) {
            super();
            super.setMethod("GET");
            this.principalKey = principalKey;
            this.includeMemberships = false;
        }

        includeUserMemberships(includeMemberships: boolean): GetPrincipalByKeyRequest {
            this.includeMemberships = includeMemberships;
            return this;
        }

        getParams(): Object {
            return {
                memberships: this.includeMemberships && this.principalKey.isUser()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals', this.principalKey.toString());
        }

        sendAndParse(): wemQ.Promise<Principal> {

            return this.send().then((response: api.rest.JsonResponse<PrincipalJson>) => {
                return this.fromJsonToPrincipal(response.getResult());
            });
        }

    }
}