module api.security {

    export class PrincipalLoader extends api.util.loader.BaseLoader<any, any> {

        protected request: FindPrincipalListRequest;
        private skipPrincipalKeys: { [key:string]:PrincipalKey; };

        constructor() {
            super();

            this.skipPrincipalKeys = {};
            // allow all by default
            this.setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER, PrincipalType.ROLE]);
        }

        protected createRequest(): FindPrincipalListRequest {
            return new FindPrincipalListRequest();
        }

        protected getRequest(): FindPrincipalListRequest {
            return this.request;
        }

        setUserStoreKey(key: UserStoreKey): PrincipalLoader {
            this.getRequest().setUserStoreKey(key);
            return this;
        }

        setAllowedTypes(principalTypes: PrincipalType[]): PrincipalLoader {
            this.getRequest().setAllowedTypes(principalTypes);
            return this;
        }

        search(searchString: string): wemQ.Promise<Principal[]> {
            this.getRequest().setSearchQuery(searchString);
            return this.load();
        }

        skipPrincipals(principalKeys: PrincipalKey[]): PrincipalLoader {
            this.skipPrincipalKeys = {};
            principalKeys.forEach((principalKey: PrincipalKey) => {
                this.skipPrincipalKeys[principalKey.toString()] = principalKey;
            });
            this.getRequest().setResultFilter((principal) => !this.skipPrincipalKeys[principal.getKey().toString()])
            return this;
        }

        skipPrincipal(principalKey: PrincipalKey): PrincipalLoader {
            this.skipPrincipalKeys[principalKey.toString()] = principalKey;
            this.getRequest().setResultFilter((principal) => !this.skipPrincipalKeys[principal.getKey().toString()])
            return this;
        }

    }

}