module api.security {

    export class ResolveMemberResult {

        private principalKey: PrincipalKey;

        private members: Principal[];

        getPrincipalKey(): PrincipalKey {
            return this.principalKey;
        }

        getMembers(): Principal[] {
            return this.members;
        }

        static fromJson(json: ResolveMemberResultJson): ResolveMemberResult {
            var result = new ResolveMemberResult();
            result.principalKey = PrincipalKey.fromString(json.principalKey);
            result.members = json.members.principals.map(principalJson => Principal.fromJson(principalJson));
            return result;
        }

    }
}