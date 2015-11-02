module api.security {

    export class ResolveMembersResult {

        private values: ResolveMemberResult[];

        getValues(): ResolveMemberResult[] {
            return this.values;
        }


        getByPrincipalKey(principalKey: PrincipalKey): ResolveMemberResult {
            var result = this.values.filter((resolveMembershipResult: ResolveMemberResult) => {
                return resolveMembershipResult.getPrincipalKey().equals(principalKey);
            });
            return result && result.length > 0 ? result[0] : null;

        }


        static fromJson(json: api.security.ResolveMembersResultJson): ResolveMembersResult {
            var result = new ResolveMembersResult();
            result.values = json.results.map((resultJson) => {
                return ResolveMemberResult.fromJson(resultJson)

            });
            return result;
        }
    }
}