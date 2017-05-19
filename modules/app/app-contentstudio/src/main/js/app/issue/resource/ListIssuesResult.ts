import {IssueJson} from "../json/IssueJson";
import {IssueMetadata} from "../IssueMetadata";

export interface ListIssuesResult {

    issues: IssueJson[];

    metadata: IssueMetadata;
}
