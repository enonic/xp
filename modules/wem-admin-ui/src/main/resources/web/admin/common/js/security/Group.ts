module api.security {

    export class Group extends Principal {

        private members: PrincipalKey[];

        constructor(builder: GroupBuilder) {
            super(builder.key, builder.displayName, builder.modifiedTime);
            api.util.assert(builder.key.isGroup(), 'Expected PrincipalKey of type Group');
            this.members = builder.members || [];
        }

        getMembers(): PrincipalKey[] {
            return this.members;
        }

        setMembers(members: PrincipalKey[]): void {
            this.members = members || [];
        }

        addMember(member: PrincipalKey): void {
            this.members.push(member);
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
        modifiedTime: Date;
        members: PrincipalKey[];

        constructor(source?: Group) {
            if (source) {
                this.key = source.getKey();
                this.displayName = source.getDisplayName();
                this.modifiedTime = source.getModifiedTime();
                this.members = source.getMembers().slice(0);
            } else {
                this.members = [];
            }
        }

        fromJson(json: api.security.GroupJson): GroupBuilder {
            this.key = PrincipalKey.fromString(json.key);
            this.displayName = json.displayName;
            this.modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;
            if (json.members) {
                this.members = json.members.map((memberStr) => PrincipalKey.fromString(memberStr));
            }
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
