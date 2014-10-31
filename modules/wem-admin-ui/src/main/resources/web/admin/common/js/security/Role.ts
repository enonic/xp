module api.security {

    export class Role extends Principal {

        constructor(builder: RoleBuilder) {
            super(builder.key, builder.displayName, PrincipalType.ROLE)
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

        constructor(source?: Role) {
            if (source) {
                this.key = source.getKey();
                this.displayName = source.getDisplayName();
            }
        }

        fromJson(json: api.security.RoleJson): RoleBuilder {
            this.key = PrincipalKey.fromString(json.key);
            this.displayName = json.displayName;
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

        build(): Role {
            return new Role(this);
        }
    }

}