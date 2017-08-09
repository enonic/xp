module api.security {

    export class GetPrincipalsByUserStoreRequest extends SecurityResourceRequest<PrincipalListJson, Principal[]> {

        private userStore: UserStoreKey;

        private principalTypes: PrincipalType[];

        constructor(userStore: UserStoreKey, principalTypes: PrincipalType[]) {
            super();
            super.setMethod('POST');
            this.userStore = userStore;
            this.principalTypes = principalTypes;
        }

        getParams(): Object {
            return {
                userStoreKey: this.userStore.getId(),
                types: PrincipalTypeUtil.typesToStrings(this.principalTypes)
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
