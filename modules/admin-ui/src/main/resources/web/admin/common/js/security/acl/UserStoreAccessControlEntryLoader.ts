module api.security.acl {

    import PrincipalListJson = api.security.PrincipalListJson;
    import PrincipalJson = api.security.PrincipalJson;
    import PrincipalType = api.security.PrincipalType;
    import UserStoreKey = api.security.UserStoreKey;

    export class FindUserStoreAccessControlEntriesRequest extends api.security.SecurityResourceRequest<PrincipalListJson, UserStoreAccessControlEntry[]> {

        private allowedTypes: PrincipalType[];
        private searchQuery: string;
        private userStoreKey: UserStoreKey;

        constructor() {
            super();
        }

        getParams(): Object {
            return {
                /*"types": this.enumToStrings(this.allowedTypes),*/
                "query": this.searchQuery
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals');
        }

        sendAndParse(): wemQ.Promise<UserStoreAccessControlEntry[]> {
            return this.send().
                then((response: api.rest.JsonResponse<PrincipalListJson>) => {
                    return response.getResult().principals.map((principalJson: PrincipalJson) => {
                        return new UserStoreAccessControlEntry(this.fromJsonToPrincipal(principalJson));
                    });
                });
        }

        private enumToStrings(types: PrincipalType[]): string[] {
            return types.map((type: PrincipalType) => {
                return PrincipalType[type].toUpperCase();
            });
        }

        setUserStoreKey(key: UserStoreKey): FindUserStoreAccessControlEntriesRequest {
            this.userStoreKey = key;
            return this;
        }

        setAllowedTypes(types: PrincipalType[]): FindUserStoreAccessControlEntriesRequest {
            this.allowedTypes = types;
            return this;
        }

        setSearchQuery(query: string): FindUserStoreAccessControlEntriesRequest {
            this.searchQuery = query;
            return this;
        }
    }

    export class UserStoreAccessControlEntryLoader extends api.util.loader.BaseLoader<PrincipalListJson, UserStoreAccessControlEntry> {

        private findRequest: FindUserStoreAccessControlEntriesRequest;

        constructor() {
            this.findRequest = new FindUserStoreAccessControlEntriesRequest();
            // allow all by default
            this.setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER, PrincipalType.ROLE]);
            super(this.findRequest);
        }

        setUserStoreKey(key: UserStoreKey): UserStoreAccessControlEntryLoader {
            this.findRequest.setUserStoreKey(key);
            return this;
        }

        setAllowedTypes(principalTypes: PrincipalType[]): UserStoreAccessControlEntryLoader {
            this.findRequest.setAllowedTypes(principalTypes);
            return this;
        }

        search(searchString: string) {
            this.findRequest.setSearchQuery(searchString);
            this.load();
        }

    }

}
