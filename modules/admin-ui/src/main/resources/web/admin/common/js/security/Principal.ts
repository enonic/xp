module api.security {

    export class Principal implements api.Equitable {

        private key: PrincipalKey;

        private displayName: string;

        private type: PrincipalType;

        private modifiedTime: Date;

        constructor(principalKey: PrincipalKey, displayName: string, modifiedTime?: Date) {
            this.key = principalKey;
            this.displayName = displayName || "";
            this.type = principalKey.getType();
            this.modifiedTime = modifiedTime;
        }

        static fromJson(json: PrincipalJson): Principal {
            var key = PrincipalKey.fromString(json.key);
            var date = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;
            return new Principal(key, json.displayName, date);
        }

        static fromPrincipal(principal: Principal): Principal {
            return new Principal(principal.getKey(), principal.getDisplayName(), principal.getModifiedTime());
        }

        toJson(): PrincipalJson {
            return {
                displayName: this.displayName,
                key: this.key.toString()
            }
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

        getTypeName(): string {
            switch (this.type) {
                case PrincipalType.GROUP:
                    return "Group";
                case PrincipalType.USER:
                    return "User";
                case PrincipalType.ROLE:
                    return "Role";
                default:
                    return "";
            }
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

        asUser(): User {
            return (this instanceof api.security.User) ? <api.security.User> this : null;
        }

        asGroup(): Group {
            return (this instanceof api.security.Group) ? <api.security.Group> this : null;
        }

        asRole(): Role {
            return (this instanceof api.security.Role) ? <api.security.Role> this : null;
        }

        getModifiedTime(): Date {
            return this.modifiedTime;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Principal)) {
                return false;
            }

            var other = <Principal> o;

            if (!api.ObjectHelper.equals(this.key, other.key)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.displayName, other.displayName)) {
                return false;
            }

            if (!api.ObjectHelper.dateEquals(this.modifiedTime, other.modifiedTime)) {
                return false;
            }

            return true;
        }

        clone(): Principal {
            return new Principal(this.key, this.displayName, this.modifiedTime);
        }
    }
}
