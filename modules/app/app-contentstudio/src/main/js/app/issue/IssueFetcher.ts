import {IssueType} from './IssueType';
import {ListIssuesRequest} from './resource/ListIssuesRequest';
import {IssueResponse} from './resource/IssueResponse';

export class IssueFetcher {

    static fetchIssuesByType(issueType: IssueType, from: number = 0, size: number = -1): wemQ.Promise<IssueResponse> {
        return new ListIssuesRequest().setIssueType(issueType).setFrom(from).setSize(size).sendAndParse();
    }
}

