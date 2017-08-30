import {IssueMetadata} from '../IssueMetadata';
import {IssueWithAssignees} from '../IssueWithAssignees';

export class IssueResponse {

    private issues: IssueWithAssignees[];

    private metadata: IssueMetadata;

    constructor(issues: IssueWithAssignees[], metadata: IssueMetadata) {
        this.issues = issues;
        this.metadata = metadata;
    }

    getIssues(): IssueWithAssignees[] {
        return this.issues;
    }

    getMetadata(): IssueMetadata {
        return this.metadata;
    }
}
