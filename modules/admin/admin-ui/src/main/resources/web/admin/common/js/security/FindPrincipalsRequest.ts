module api.security {

    export class FindPrincipalsRequest extends api.security.SecurityResourceRequest<FindPrincipalsResultJson, FindPrincipalsResult> {

        private allowedTypes: PrincipalType[];
        private resolveMemberships: boolean = false;
        private searchQuery: string;
        private userStoreKey: UserStoreKey;
        private filterPredicate: (principal: Principal) => boolean;
        private from: number;
        private size: number;

        constructor() {
            super();
            super.setMethod('POST');
        }

        getParams(): Object {
            return {
                types: PrincipalTypeUtil.typesToStrings(this.allowedTypes),
                memberships: this.resolveMemberships,
                query: this.searchQuery || null,
                userStoreKey: this.userStoreKey ? this.userStoreKey.toString() : null,
                from: this.from || null,
                size: this.size || null
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals');
        }

        sendAndParse(): wemQ.Promise<FindPrincipalsResult> {
            return this.send().
                then((response: api.rest.JsonResponse<FindPrincipalsResultJson>) => {
                    let principals: Principal[] = response.getResult().principals.map((principalJson: PrincipalJson) => {
                        return this.fromJsonToPrincipal(principalJson);
                    });
                    if (this.filterPredicate) {
                        principals = principals.filter(this.filterPredicate);
                    }
                    return new FindPrincipalsResult(principals, response.getResult().totalSize);
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

        setResolveMemberships(value: boolean): FindPrincipalsRequest {
            this.resolveMemberships = value;
            return this;
        }

        setFrom(from: number): FindPrincipalsRequest {
            this.from = from;
            return this;
        }

        setSize(size: number): FindPrincipalsRequest {
            this.size = size;
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
