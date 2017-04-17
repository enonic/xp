import {IssueStatsJson} from './IssueStatsJson';
import {IssueSummary} from './IssueSummary';
import {IssueType} from './IssueType';
import {GetIssueStatsRequest} from './GetIssueStatsRequest';
import {GetIssuesByTypeRequest} from './GetIssuesByTypeRequest';

export class IssueFetcher {

    static fetchIssueStats(): wemQ.Promise<IssueStatsJson> {
        return new GetIssueStatsRequest().sendAndParse();
    }

    static fetchIssuesByType(issueType: IssueType): wemQ.Promise<IssueSummary[]> {
        return new GetIssuesByTypeRequest(issueType).sendAndParse();
    }

}
