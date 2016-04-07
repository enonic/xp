module api.security {

    export class Principal implements api.Equitable {

        private key: PrincipalKey;

        private displayName: string;

        private type: PrincipalType;

        private modifiedTime: Date;

        private description: string;

        constructor(builder: PrincipalBuilder) {
            this.key = builder.key;
            this.displayName = builder.displayName || "";
            this.type = builder.key.getType();
            this.modifiedTime = builder.modifiedTime;
            this.description = builder.description;
        }

        static fromPrincipal(principal: Principal): Principal {
            return new PrincipalBuilder(principal).build();
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

        getDescription(): string {
            return this.description;
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

            if (!api.ObjectHelper.stringEquals(this.description, other.description)) {
                return false;
            }

            return true;
        }

        clone(): Principal {
            return this.newBuilder().build();
        }

        newBuilder(): PrincipalBuilder {
            return new PrincipalBuilder(this);
        }

        static create(): PrincipalBuilder {
            return new PrincipalBuilder();
        }

        static fromJson(json: api.security.PrincipalJson): Principal {
            return new PrincipalBuilder().fromJson(json).build();
        }
    }

    export class PrincipalBuilder {
        key: PrincipalKey;

        displayName: string;

        modifiedTime: Date;

        description: string;

        constructor(source?: Principal) {
            if (source) {
                this.key = source.getKey();
                this.displayName = source.getDisplayName();
                this.modifiedTime = source.getModifiedTime();
                this.description = source.getDescription();
            }
        }

        fromJson(json: api.security.PrincipalJson): PrincipalBuilder {
            this.key = PrincipalKey.fromString(json.key);
            this.displayName = json.displayName;
            this.modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;
            this.description = json.description;
            return this;
        }

        setKey(key: PrincipalKey): PrincipalBuilder {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): PrincipalBuilder {
            this.displayName = displayName;
            return this;
        }

        setModifiedTime(modifiedTime: Date): PrincipalBuilder {
            this.modifiedTime = modifiedTime;
            return this;
        }

        setDescription(description: string): PrincipalBuilder {
            this.description = description;
            return this;
        }

        build(): Principal {
            return new Principal(this);
        }
    }
}
