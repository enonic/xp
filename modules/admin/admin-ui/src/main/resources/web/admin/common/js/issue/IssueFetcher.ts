module api.issue {

    import GetIssueStatsRequest = api.issue.resource.GetIssueStatsRequest;
    import IssueStatsJson = api.issue.json.IssueStatsJson;
    import ListIssuesRequest = api.issue.resource.ListIssuesRequest;
    import IssueResponse = api.issue.resource.IssueResponse;

    export class IssueFetcher {

        static fetchIssueStats(): wemQ.Promise<IssueStatsJson> {
            return new GetIssueStatsRequest().sendAndParse();
        }

        static fetchIssuesByType(issueType: IssueType, from: number = 0, size: number = -1): wemQ.Promise<IssueResponse> {
            return new ListIssuesRequest().setIssueType(issueType).setFrom(from).setSize(size).sendAndParse();
        }
    }
}
