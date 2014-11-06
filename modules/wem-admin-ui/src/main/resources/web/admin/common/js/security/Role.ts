module api.security {
    export class Role extends Principal {
        constructor(builder: RoleBuilder) {
            super(builder.key, builder.displayName, PrincipalType.ROLE, builder.modifiedTime)
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

        constructor(source?: Role) {
            if (source) {
                this.key = source.getKey();
                this.displayName = source.getDisplayName();
                this.modifiedTime = source.getModifiedTime();
            }
        }

        fromJson(json: api.security.RoleJson): RoleBuilder {
            this.key = PrincipalKey.fromString(json.key);
            this.displayName = json.displayName;
            this.modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;
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