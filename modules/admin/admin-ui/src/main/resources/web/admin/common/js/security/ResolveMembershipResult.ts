module api.security {

    export class ResolveMembershipResult {

        private principalKey: PrincipalKey;

        private members: Principal[];

        getPrincipalKey(): PrincipalKey {
            return this.principalKey;
        }

        getMembers(): Principal[] {
            return this.members;
        }

        static fromJson(json: ResolveMembershipResultJson): ResolveMembershipResult {
            var result = new ResolveMembershipResult();
            result.principalKey = PrincipalKey.fromString(json.principalKey);
            result.members = json.members.principals.map(principalJson => Principal.fromJson(principalJson));
            return result;
        }

    }
}