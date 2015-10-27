module api.security {

    export class ResolveMembersResult {

        private values: ResolveMemberResult[];

        getValues(): ResolveMemberResult[] {
            return this.values;
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