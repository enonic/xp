module api.security {

    export class Principal extends UserItem {

        private type: PrincipalType;

        private modifiedTime: Date;

        constructor(builder: PrincipalBuilder) {
            super(builder);
            this.type = (<PrincipalKey>builder.key).getType();
            this.modifiedTime = builder.modifiedTime;
        }

        static fromPrincipal(principal: Principal): Principal {
            return new PrincipalBuilder(principal).build();
        }

        toJson(): PrincipalJson {
            return {
                displayName: this.getDisplayName(),
                key: this.getKey().toString()
            };
        }

        getType(): PrincipalType {
            return this.type;
        }

        getKey(): PrincipalKey {
            return <PrincipalKey>super.getKey();
        }

        getTypeName(): string {
            switch (this.type) {
                case PrincipalType.GROUP:
                    return 'Group';
                case PrincipalType.USER:
                    return 'User';
                case PrincipalType.ROLE:
                    return 'Role';
                default:
                    return '';
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

            let other = <Principal> o;

            if (!super.equals(o)) {
                return false;
            }

            if (!api.ObjectHelper.dateEquals(this.modifiedTime, other.modifiedTime)) {
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

    export class PrincipalBuilder extends UserItemBuilder {

        modifiedTime: Date;

        constructor(source?: Principal) {
            if (source) {
                super(source);
                this.modifiedTime = source.getModifiedTime();
            }
        }

        fromJson(json: api.security.PrincipalJson): PrincipalBuilder {
            super.fromJson(json);
            this.key = PrincipalKey.fromString(json.key);
            this.modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;
            return this;
        }

        setKey(key: PrincipalKey): PrincipalBuilder {
            this.key = key;
            return this;
        }

        setModifiedTime(modifiedTime: Date): PrincipalBuilder {
            this.modifiedTime = modifiedTime;
            return this;
        }

        setDisplayName(displayName: string): PrincipalBuilder {
            super.setDisplayName(displayName);
            return this;
        }

        setDescription(description: string): PrincipalBuilder {
            super.setDescription(description);
            return this;
        }

        build(): Principal {
            return new Principal(this);
        }
    }
}
