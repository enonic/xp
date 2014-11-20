module api.ui.security {

    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalKey = api.security.PrincipalKey;
    import UserStoreKey = api.security.UserStoreKey;
    import PrincipalListJson = api.security.PrincipalListJson;
    import FindPrincipalsRequest = api.security.FindPrincipalsRequest;

    export class PrincipalLoader extends api.util.loader.BaseLoader<PrincipalListJson, Principal> {

        private findRequest: FindPrincipalsRequest;

        constructor() {
            this.findRequest = new FindPrincipalsRequest();
            // allow all by default
            this.setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER, PrincipalType.ROLE]);
            super(this.findRequest);
        }

        setUserStoreKey(key: UserStoreKey): PrincipalLoader {
            this.findRequest.setUserStoreKey(key);
            return this;
        }

        setAllowedTypes(principalTypes: PrincipalType[]): PrincipalLoader {
            this.findRequest.setAllowedTypes(principalTypes);
            return this;
        }

        search(searchString: string) {
            this.findRequest.setSearchQuery(searchString);
            this.load();
        }

    }

}