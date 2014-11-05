module api.security {

    export class GetPrincipalsByUserStoreRequest extends SecurityResourceRequest<PrincipalListJson, Principal[]> {

        private userStore: UserStoreKey;

        private principalType: PrincipalType;

        constructor(userStore: UserStoreKey, principalType: PrincipalType) {
            super();
            super.setMethod("GET");
            this.userStore = userStore;
            this.principalType = principalType;
        }

        getParams(): Object {
            return {
                'userStoreKey': this.userStore.toString(),
                'type': PrincipalType[this.principalType]

            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals');
        }

        sendAndParse(): wemQ.Promise<Principal[]> {

            return this.send().then((response: api.rest.JsonResponse<PrincipalListJson>) => {
                return response.getResult().principals.map((principalJson: PrincipalJson) => {
                    return this.fromJsonToPrincipal(principalJson);
                });
            });
        }

    }
}