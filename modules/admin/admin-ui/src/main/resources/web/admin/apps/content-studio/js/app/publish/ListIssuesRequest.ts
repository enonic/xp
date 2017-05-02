import IssueResourceRequest = api.issue.resource.IssueResourceRequest;
import {IssueSummary} from './IssueSummary';
import {IssueJson} from './IssueJson';
import {IssueType} from './IssueType';
import {ListIssuesResult} from './ListIssuesResult';
import {IssueResponse} from './IssueResponse';
import {IssueMetadata} from './IssueMetadata';

export class ListIssuesRequest extends IssueResourceRequest<ListIssuesResult, IssueResponse> {

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

    sendAndParse(): wemQ.Promise<IssueResponse> {
        return this.send().then((response: api.rest.JsonResponse<ListIssuesResult>) => {
            const issues: IssueSummary[] = response.getResult().issues.map((issueJson: IssueJson) => {
                return IssueSummary.fromJson(issueJson);
            });
            const metadata: IssueMetadata = new IssueMetadata(response.getResult().metadata['hits'],
                response.getResult().metadata['totalHits']);

            return new IssueResponse(issues, metadata);
        });
    }
}
