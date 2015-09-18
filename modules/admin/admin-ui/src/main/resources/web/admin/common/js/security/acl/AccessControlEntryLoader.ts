module api.security.acl {

    import PrincipalListJson = api.security.PrincipalListJson;
    import PrincipalJson = api.security.PrincipalJson;
    import PrincipalType = api.security.PrincipalType;
    import UserStoreKey = api.security.UserStoreKey;

    export class FindAccessControlEntriesRequest extends api.security.SecurityResourceRequest<PrincipalListJson, AccessControlEntry[]> {

        private allowedTypes: PrincipalType[];
        private searchQuery: string;
        private userStoreKey: UserStoreKey;

        constructor() {
            super();
        }

        getParams(): Object {
            return {
                "types": this.enumToStrings(this.allowedTypes),
                "query": this.searchQuery,
                "userStoreKey": this.userStoreKey ? this.userStoreKey.toString() : undefined
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals');
        }

        sendAndParse(): wemQ.Promise<AccessControlEntry[]> {
            return this.send().
                then((response: api.rest.JsonResponse<PrincipalListJson>) => {
                    return response.getResult().principals.map((principalJson: PrincipalJson) => {
                        return new AccessControlEntry(this.fromJsonToPrincipal(principalJson));
                    });
                });
        }

        private enumToStrings(types: PrincipalType[]): string[] {
            return types.map((type: PrincipalType) => {
                return PrincipalType[type].toUpperCase();
            });
        }

        setUserStoreKey(key: UserStoreKey): FindAccessControlEntriesRequest {
            this.userStoreKey = key;
            return this;
        }

        setAllowedTypes(types: PrincipalType[]): FindAccessControlEntriesRequest {
            this.allowedTypes = types;
            return this;
        }

        setSearchQuery(query: string): FindAccessControlEntriesRequest {
            this.searchQuery = query;
            return this;
        }
    }

    export class AccessControlEntryLoader extends api.util.loader.BaseLoader<PrincipalListJson, AccessControlEntry> {

        private findRequest: FindAccessControlEntriesRequest;

        constructor() {
            this.findRequest = new FindAccessControlEntriesRequest();
            // allow all by default
            this.setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER, PrincipalType.ROLE]);
            super(this.findRequest);
        }

        setUserStoreKey(key: UserStoreKey): AccessControlEntryLoader {
            this.findRequest.setUserStoreKey(key);
            return this;
        }

        setAllowedTypes(principalTypes: PrincipalType[]): AccessControlEntryLoader {
            this.findRequest.setAllowedTypes(principalTypes);
            return this;
        }

        search(searchString: string): wemQ.Promise<AccessControlEntry[]> {
            this.findRequest.setSearchQuery(searchString);
            return this.load();
        }

    }

}
