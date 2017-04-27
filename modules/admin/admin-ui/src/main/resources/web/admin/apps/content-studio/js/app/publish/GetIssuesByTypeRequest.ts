import IssueResourceRequest = api.issue.resource.IssueResourceRequest;
import {IssuesJson} from './IssuesJson';
import {IssueSummary} from './IssueSummary';
import {IssueJson} from './IssueJson';
import {IssueType} from './IssueType';

export class GetIssuesByTypeRequest extends IssueResourceRequest<IssuesJson, IssueSummary[]> {

    private issueType: IssueType;

    constructor(issueType: IssueType) {
        super();
        this.issueType = issueType;
    }

    getParams(): Object {
        return {type: IssueType[this.issueType]};
    }

    getRequestPath(): api.rest.Path {
        return api.rest.Path.fromParent(super.getResourcePath(), 'bytype');
    }

    sendAndParse(): wemQ.Promise<IssueSummary[]> {
        return this.send().then((response: api.rest.JsonResponse<IssuesJson>) => {
            return response.getResult().issues.map((issueJson: IssueJson) => {
                return IssueSummary.fromJson(issueJson);
            });
        });
    }
}
