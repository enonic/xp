import {IssueSummaryJson} from './IssueSummaryJson';
import PublishRequestJson = api.issue.resource.PublishRequestJson;

export interface IssueJson extends IssueSummaryJson {

    approverIds: string[];

    publishRequest: PublishRequestJson;
}
