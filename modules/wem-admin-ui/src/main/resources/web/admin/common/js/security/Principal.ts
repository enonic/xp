module api.security {

    export class Principal implements api.Equitable {

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

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Principal)) {
                return false;
            }


            var other = <Principal>o;
            return true;
        }
    }
}
