import {IssueStatsJson} from './IssueStatsJson';
import {IssueType} from './IssueType';
import {GetIssueStatsRequest} from './GetIssueStatsRequest';
import {ListIssuesRequest} from './ListIssuesRequest';
import {IssueResponse} from './IssueResponse';

export class IssueFetcher {

    static fetchIssueStats(): wemQ.Promise<IssueStatsJson> {
        return new GetIssueStatsRequest().sendAndParse();
    }

    static fetchIssuesByType(issueType: IssueType, from: number = 0, size: number = -1): wemQ.Promise<IssueResponse> {
        return new ListIssuesRequest().setIssueType(issueType).setFrom(from).setSize(size).sendAndParse();
    }

}
