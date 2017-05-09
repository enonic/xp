import {IssueJson} from './IssueJson';
import {IssueMetadata} from './IssueMetadata';

export interface ListIssuesResult {

    issues: IssueJson[];

    metadata: IssueMetadata;
}
