module api.security {

    export class GetPrincipalByKeyRequest extends SecurityResourceRequest<PrincipalJson, Principal> {

        private principalKey: PrincipalKey;

        constructor(principalKey: PrincipalKey) {
            super();
            super.setMethod("GET");
            this.principalKey = principalKey;
        }

        getParams(): Object {
            return {};
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