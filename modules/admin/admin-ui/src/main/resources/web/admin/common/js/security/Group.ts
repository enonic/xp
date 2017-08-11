module api.security {

    export class Group extends Principal {

        private members: PrincipalKey[];

        private memberships: Principal[];

        constructor(builder: GroupBuilder) {
            super(builder);
            api.util.assert(this.getKey().isGroup(), 'Expected PrincipalKey of type Group');
            this.members = builder.members || [];
            this.memberships = builder.memberships || [];
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

        getMemberships(): Principal[] {
            return this.memberships;
        }

        setMemberships(memberships: Principal[]) {
            this.memberships = memberships;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Group)) {
                return false;
            }

            let other = <Group> o;
            return super.equals(o) &&
                   api.ObjectHelper.arrayEquals(this.members, other.getMembers()) &&
                   api.ObjectHelper.arrayEquals(this.memberships, other.getMemberships());
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

        memberships: Principal[];

        constructor(source?: Group) {
            if (source) {
                super(source);
                this.members = source.getMembers().slice(0);
                this.memberships = source.getMemberships().slice(0);
            } else {
                this.members = [];
                this.memberships = [];
            }
        }

        fromJson(json: api.security.GroupJson): GroupBuilder {
            super.fromJson(json);

            if (json.members) {
                this.members = json.members.map((memberStr) => PrincipalKey.fromString(memberStr));
            }
            if (json.memberships) {
                this.memberships = json.memberships.map((principalJson) => Principal.fromJson(principalJson));
            }
            return this;
        }

        setMembers(members: PrincipalKey[]): GroupBuilder {
            this.members = members || [];
            return this;
        }

        setMemberships(memberships: Principal[]): GroupBuilder {
            this.memberships = memberships || [];
            return this;
        }

        build(): Group {
            return new Group(this);
        }
    }
}
