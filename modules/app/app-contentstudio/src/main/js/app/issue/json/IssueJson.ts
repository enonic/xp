import {PublishRequestJson} from "./PublishRequestJson";
import {IssueSummaryJson} from "./IssueSummaryJson";

export interface IssueJson extends IssueSummaryJson {

    approverIds: string[];

    publishRequest: PublishRequestJson;
}
