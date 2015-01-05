module api.security {

    export class FindPrincipalsRequest extends api.security.SecurityResourceRequest<PrincipalListJson, Principal[]> {

        private allowedTypes: PrincipalType[];
        private searchQuery: string;
        private userStoreKey: UserStoreKey;
        private filterPredicate: (principal: Principal) => boolean;

        constructor() {
            super();
        }

        getParams(): Object {
            return {
                "types": this.enumToStrings(this.allowedTypes).join(','),
                "query": this.searchQuery,
                "userStoreKey": this.userStoreKey ? this.userStoreKey.toString() : undefined
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals');
        }

        sendAndParse(): wemQ.Promise<Principal[]> {
            return this.send().
                then((response: api.rest.JsonResponse<PrincipalListJson>) => {
                    var principals: Principal[] = response.getResult().principals.map((principalJson: PrincipalJson) => {
                        return this.fromJsonToPrincipal(principalJson);
                    });
                    if (this.filterPredicate) {
                        principals = principals.filter(this.filterPredicate);
                    }
                    return principals;
                });
        }

        private enumToStrings(types: PrincipalType[]): string[] {
            return types.map((type: PrincipalType) => {
                return PrincipalType[type].toUpperCase();
            });
        }

        setUserStoreKey(key: UserStoreKey): FindPrincipalsRequest {
            this.userStoreKey = key;
            return this;
        }

        setAllowedTypes(types: PrincipalType[]): FindPrincipalsRequest {
            this.allowedTypes = types;
            return this;
        }

        setSearchQuery(query: string): FindPrincipalsRequest {
            this.searchQuery = query;
            return this;
        }

        setResultFilter(filterPredicate: (principal: Principal) => boolean) {
            this.filterPredicate = filterPredicate;
        }
    }
}