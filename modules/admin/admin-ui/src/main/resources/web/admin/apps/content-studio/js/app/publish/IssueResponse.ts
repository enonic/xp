import '../../api.ts';
import {IssueMetadata} from './IssueMetadata';
import {IssueSummary} from './IssueSummary';

export class IssueResponse {

    private issues: IssueSummary[];

    private metadata: IssueMetadata;

    constructor(issues: IssueSummary[], metadata: IssueMetadata) {
        this.issues = issues;
        this.metadata = metadata;
    }

    getIssues(): IssueSummary[] {
        return this.issues;
    }

    getMetadata(): IssueMetadata {
        return this.metadata;
    }
}
