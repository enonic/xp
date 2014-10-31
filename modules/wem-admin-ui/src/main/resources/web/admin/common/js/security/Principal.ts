module api.security {

    export class Principal implements api.Equitable {

        private key: PrincipalKey;

        private displayName: string;

        private type: PrincipalType;

        constructor(principalKey: PrincipalKey, displayName: string, type: PrincipalType) {
            this.key = principalKey;
            this.displayName = displayName;
            this.type = type;
        }

        getKey(): PrincipalKey {
            return this.key;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getType(): PrincipalType {
            return this.type;
        }

        isUser(): boolean {
            return this.type === PrincipalType.USER;
        }

        isGroup(): boolean {
            return this.type === PrincipalType.GROUP;
        }

        isRole(): boolean {
            return this.type === PrincipalType.ROLE;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Principal)) {
                return false;
            }

            var other = <Principal> o;
            return this.key.equals(other.key) &&
                   this.displayName === other.displayName &&
                   this.type === other.type;
        }
    }
}
