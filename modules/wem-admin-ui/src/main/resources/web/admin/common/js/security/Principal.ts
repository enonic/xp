module api.security {

    export class Principal {

        private key: PrincipalKey;

        private displayName: string;

        constructor(principalKey: PrincipalKey, displayName: string) {
            this.key = principalKey;
            this.displayName = displayName;
        }

        getKey(): PrincipalKey {
            return this.key;
        }

        getDisplayName(): string {
            return this.displayName;
        }
    }
}
