module api.security {

    export class ResolveMembershipsResult {

        private values: ResolveMembershipResult[];

        getValues(): ResolveMembershipResult[] {
            return this.values;
        }

        static fromJson(json: api.security.ResolveMembershipsResultJson): ResolveMembershipsResult {
            var result = new ResolveMembershipsResult();
            result.values = json.results.map((resultJson) => {
                return ResolveMembershipResult.fromJson(resultJson)

            });
            return result;
        }
    }
}