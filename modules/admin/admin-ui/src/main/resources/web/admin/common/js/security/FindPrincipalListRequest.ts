module api.security {

    export class FindPrincipalListRequest extends api.security.SecurityResourceRequest<PrincipalListJson, Principal[]> {

        private request: FindPrincipalsRequest;

        constructor() {
            super();
            this.request = new api.security.FindPrincipalsRequest();
        }

        sendAndParse(): wemQ.Promise<Principal[]> {
            return this.request.sendAndParse().then((result: FindPrincipalsResult) => {
                return result.getPrincipals();
                });
        }

        setUserStoreKey(key: UserStoreKey): FindPrincipalListRequest {
            this.request.setUserStoreKey(key);
            return this;
        }

        setAllowedTypes(types: PrincipalType[]): FindPrincipalListRequest {
            this.request.setAllowedTypes(types);
            return this;
        }

        setSearchQuery(query: string): FindPrincipalListRequest {
            this.request.setSearchQuery(query);
            return this;
        }

        setResultFilter(filterPredicate: (principal: Principal) => boolean) {
            this.request.setResultFilter(filterPredicate);
        }

    }
}
