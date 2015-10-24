module api.security {

    export class ResolveMembershipResult {

        private principalKey: PrincipalKey;

        private memberKeys: PrincipalKey[];

        getPrincipalKey(): PrincipalKey {
            return this.principalKey;
        }

        getMemberKeys(): PrincipalKey[] {
            return this.memberKeys;
        }

        static fromJson(json: ResolveMembershipResultJson): ResolveMembershipResult {
            var result = new ResolveMembershipResult();
            result.principalKey = PrincipalKey.fromString(json.principalKey);
            result.memberKeys = json.members.map(keyStr => PrincipalKey.fromString(keyStr));
            return result;
        }

    }
}