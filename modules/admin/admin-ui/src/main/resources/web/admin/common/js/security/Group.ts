module api.security {

    export class Group extends Principal {

        private members: PrincipalKey[];

        constructor(builder: GroupBuilder) {
            super(builder);
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

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Group)) {
                return false;
            }

            var other = <Group> o;
            return super.equals(o) && api.ObjectHelper.arrayEquals(this.members, other.getMembers());
        }

        clone(): Group {
            return this.newBuilder().build();
        }

        newBuilder(): GroupBuilder {
            return new GroupBuilder(this);
        }

        static create(): GroupBuilder {
            return new GroupBuilder();
        }

        static fromJson(json: api.security.GroupJson): Group {
            return new GroupBuilder().fromJson(json).build();
        }
    }

    export class GroupBuilder extends PrincipalBuilder {
        members: PrincipalKey[];

        constructor(source?: Group) {
            if (source) {
                super(source);
                this.members = source.getMembers().slice(0);
            } else {
                this.members = [];
            }
        }

        fromJson(json: api.security.GroupJson): GroupBuilder {
            super.fromJson(json);

            if (json.members) {
                this.members = json.members.map((memberStr) => PrincipalKey.fromString(memberStr));
            }
            return this;
        }

        setMembers(members: PrincipalKey[]): GroupBuilder {
            this.members = members || [];
            return this;
        }

        build(): Group {
            return new Group(this);
        }
    }
}
