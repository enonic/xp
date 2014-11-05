module api.security {

    export class Principal extends UserTreeGridItem {

        private key: PrincipalKey;

        private type: PrincipalType;

        constructor(principalKey: PrincipalKey, displayName: string, type: PrincipalType) {
            super(displayName);
            this.key = principalKey;
            this.type = type;
        }

        getKey(): PrincipalKey {
            return this.key;
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
            if (!super.equals(o)) {
                return false;
            }

            var other = <Principal> o;
            return this.key.equals(other.key) && this.type === other.type;
        }
    }
}
