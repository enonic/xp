import {IssueSummaryJson} from './IssueSummaryJson';

export interface IssueJson extends IssueSummaryJson {

    name: string;

    issuePath: string;

    description: string;

    createdTime: string;

    modifiedTime: string;

    issueStatus: string;

    modifier: string;

    approverIds: string[];

}
