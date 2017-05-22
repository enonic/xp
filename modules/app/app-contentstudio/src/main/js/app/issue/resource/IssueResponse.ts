import {IssueMetadata} from "../IssueMetadata";
import {Issue} from "../Issue";

export class IssueResponse {

    private issues: Issue[];

    private metadata: IssueMetadata;

    constructor(issues: Issue[], metadata: IssueMetadata) {
        this.issues = issues;
        this.metadata = metadata;
    }

    getIssues(): Issue[] {
        return this.issues;
    }

    getMetadata(): IssueMetadata {
        return this.metadata;
    }
}
