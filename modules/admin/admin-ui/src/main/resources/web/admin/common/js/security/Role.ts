module api.security {

    export class Role extends Principal {

        private members: PrincipalKey[];

        constructor(builder: RoleBuilder) {
            super(builder.key, builder.displayName, builder.modifiedTime);
            api.util.assert(builder.key.isRole(), 'Expected PrincipalKey of type Role');
            this.members = builder.members || [];
        }

        getMembers(): PrincipalKey[] {
            return this.members;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Role)) {
                return false;
            }

            var other = <Role> o;
            return super.equals(o) && api.ObjectHelper.arrayEquals(this.members, other.getMembers());
        }

        clone(): Role {
            return this.newBuilder().build();
        }

        newBuilder(): RoleBuilder {
            return new RoleBuilder(this);
        }

        static create(): RoleBuilder {
            return new RoleBuilder();
        }

        static fromJson(json: api.security.RoleJson): Role {
            return new RoleBuilder().fromJson(json).build();
        }
    }

    export class RoleBuilder {
        key: PrincipalKey;
        displayName: string;
        modifiedTime: Date;
        members: PrincipalKey[];

        constructor(source?: Role) {
            if (source) {
                this.key = source.getKey();
                this.displayName = source.getDisplayName();
                this.modifiedTime = source.getModifiedTime();
                this.members = source.getMembers().slice(0);
            } else {
                this.members = [];
            }
        }

        fromJson(json: api.security.RoleJson): RoleBuilder {
            this.key = PrincipalKey.fromString(json.key);
            this.displayName = json.displayName;
            this.modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;
            if (json.members) {
                this.members = json.members.map((memberStr) => PrincipalKey.fromString(memberStr));
            }
            return this;
        }

        setKey(key: PrincipalKey): RoleBuilder {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): RoleBuilder {
            this.displayName = displayName;
            return this;
        }

        setMembers(members: PrincipalKey[]): RoleBuilder {
            this.members = members || [];
            return this;
        }

        build(): Role {
            return new Role(this);
        }
    }
}