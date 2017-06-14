import {IssueMetadata} from '../IssueMetadata';
import {IssueWithAssigneesJson} from '../json/IssueWithAssigneesJson';

export interface ListIssuesResult {

    issues: IssueWithAssigneesJson[];

    metadata: IssueMetadata;
}
