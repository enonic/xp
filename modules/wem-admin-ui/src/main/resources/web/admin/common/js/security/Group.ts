module api.security {

    export class Group extends Principal {

        constructor(builder: GroupBuilder) {
            super(builder.key, builder.displayName, PrincipalType.GROUP)
        }

        static create(): GroupBuilder {
            return new GroupBuilder();
        }

        static fromJson(json: api.security.GroupJson): Group {
            return new GroupBuilder().fromJson(json).build();
        }

    }

    export class GroupBuilder {

        key: PrincipalKey;

        displayName: string;

        constructor(source?: Group) {
            if (source) {
                this.key = source.getKey();
                this.displayName = source.getDisplayName();
            }
        }

        fromJson(json: api.security.GroupJson): GroupBuilder {
            this.key = PrincipalKey.fromString(json.key);
            this.displayName = json.displayName;
            return this;
        }

        setKey(key: PrincipalKey): GroupBuilder {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): GroupBuilder {
            this.displayName = displayName;
            return this;
        }

        build(): Group {
            return new Group(this);
        }
    }
}
