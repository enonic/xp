module api.security {

    export class Role extends Principal {

        private members: PrincipalKey[];

        constructor(builder: RoleBuilder) {
            super(builder);
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

    export class RoleBuilder extends PrincipalBuilder{

        members: PrincipalKey[];

        constructor(source?: Role) {
            if (source) {
                super(source);
                this.members = source.getMembers().slice(0);
            } else {
                this.members = [];
            }
        }

        fromJson(json: api.security.RoleJson): RoleBuilder {
            super.fromJson(json);

            if (json.members) {
                this.members = json.members.map((memberStr) => PrincipalKey.fromString(memberStr));
            }
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