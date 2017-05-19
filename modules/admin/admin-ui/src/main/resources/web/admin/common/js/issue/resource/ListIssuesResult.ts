module api.issue.resource {

    import IssueJson = api.issue.json.IssueJson;

    export interface ListIssuesResult {

        issues: IssueJson[];

        metadata: IssueMetadata;
    }
}
