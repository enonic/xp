import IssueResourceRequest = api.issue.resource.IssueResourceRequest;
import {IssuesJson} from './IssuesJson';
import {IssueSummary} from './IssueSummary';
import {IssueJson} from './IssueJson';
import {IssueType} from './IssueType';

export class ListIssuesRequest extends IssueResourceRequest<IssuesJson, IssueSummary[]> {

    private issueType: IssueType;

    private from: number;

    private size: number;

    constructor() {
        super();
    }

    setFrom(value: number): ListIssuesRequest {
        this.from = value;
        return this;
    }

    setSize(value: number): ListIssuesRequest {
        this.size = value;
        return this;
    }

    setIssueType(value: IssueType): ListIssuesRequest {
        this.issueType = value;
        return this;
    }

    getParams(): Object {
        return {
            type: IssueType[this.issueType],
            from: this.from,
            size: this.size
        };
    }

    getRequestPath(): api.rest.Path {
        return api.rest.Path.fromParent(super.getResourcePath(), 'list');
    }

    sendAndParse(): wemQ.Promise<IssueSummary[]> {
        return this.send().then((response: api.rest.JsonResponse<IssuesJson>) => {
            return response.getResult().issues.map((issueJson: IssueJson) => {
                return IssueSummary.fromJson(issueJson);
            });
        });
    }
}
