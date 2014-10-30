module api.security {

    export class SynchPrincipalRequest extends SecurityResourceRequest<PrincipalJson, Principal> {

        private key: PrincipalKey;

        constructor(key: PrincipalKey) {
            super();
            super.setMethod("POST");
            this.key = key;
        }

        getParams(): Object {
            return {
                contentId: this.key.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "synch");
        }

        sendAndParse(): wemQ.Promise<Principal> {

            return this.send().then((response: api.rest.JsonResponse<PrincipalJson>) => {
                return this.fromJsonToPrincipal(response.getResult());
            });
        }
    }
}