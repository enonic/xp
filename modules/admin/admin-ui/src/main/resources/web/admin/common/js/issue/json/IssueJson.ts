module api.issue.json {

    export interface IssueJson extends IssueSummaryJson {

        approverIds: string[];

        publishRequest: PublishRequestJson;
    }
}
