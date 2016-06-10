module api.security {

    export class PrincipalLoader extends api.util.loader.BaseLoader<any, any> {

        private findRequest: FindPrincipalListRequest;
        private skipPrincipalKeys: { [key:string]:PrincipalKey; };

        constructor() {
            this.findRequest = new FindPrincipalListRequest();
            this.skipPrincipalKeys = {};
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

        search(searchString: string): wemQ.Promise<Principal[]> {
            this.findRequest.setSearchQuery(searchString);
            return this.load();
        }

        skipPrincipals(principalKeys: PrincipalKey[]): PrincipalLoader {
            this.skipPrincipalKeys = {};
            principalKeys.forEach((principalKey: PrincipalKey) => {
                this.skipPrincipalKeys[principalKey.toString()] = principalKey;
            });
            this.findRequest.setResultFilter((principal) => !this.skipPrincipalKeys[principal.getKey().toString()])
            return this;
        }

        skipPrincipal(principalKey: PrincipalKey): PrincipalLoader {
            this.skipPrincipalKeys[principalKey.toString()] = principalKey;
            this.findRequest.setResultFilter((principal) => !this.skipPrincipalKeys[principal.getKey().toString()])
            return this;
        }

    }

}