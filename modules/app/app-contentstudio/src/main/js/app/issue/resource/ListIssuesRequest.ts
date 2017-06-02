import {IssueResponse} from './IssueResponse';
import {ListIssuesResult} from './ListIssuesResult';
import {IssueJson} from '../json/IssueJson';
import {IssueMetadata} from '../IssueMetadata';
import {Issue} from '../Issue';
import {IssueResourceRequest} from './IssueResourceRequest';
import {IssueStatus} from '../IssueStatus';

export class ListIssuesRequest extends IssueResourceRequest<ListIssuesResult, IssueResponse> {

    private static DEFAULT_FETCH_SIZE: number = 10;

    private issueStatus: IssueStatus;

    private from: number = 0;

    private size: number = ListIssuesRequest.DEFAULT_FETCH_SIZE;

    private assignedToMe: boolean = false;

    private createdByMe: boolean = false;

    constructor() {
        super();
        super.setMethod('POST');
    }

    setFrom(value: number): ListIssuesRequest {
        this.from = value;
        return this;
    }

    setSize(value: number): ListIssuesRequest {
        this.size = value;
        return this;
    }

    setIssueStatus(value: IssueStatus): ListIssuesRequest {
        this.issueStatus = value;
        return this;
    }

    setAssignedToMe(value: boolean): ListIssuesRequest {
        this.assignedToMe = value;
        return this;
    }

    setCreatedByMe(value: boolean): ListIssuesRequest {
        this.createdByMe = value;
        return this;
    }

    getParams(): Object {
        return {
            type: IssueStatus[this.issueStatus],
            from: this.from,
            size: this.size,
            assignedToMe: this.assignedToMe,
            createdByMe: this.createdByMe
        };
    }

    getRequestPath(): api.rest.Path {
        return api.rest.Path.fromParent(super.getResourcePath(), 'list');
    }

    sendAndParse(): wemQ.Promise<IssueResponse> {
        return this.send().then((response: api.rest.JsonResponse<ListIssuesResult>) => {
            const issues: Issue[] = response.getResult().issues.map((issueJson: IssueJson) => {
                return Issue.fromJson(issueJson);
            });
            const metadata: IssueMetadata = new IssueMetadata(response.getResult().metadata['hits'],
                response.getResult().metadata['totalHits']);

            return new IssueResponse(issues, metadata);
        });
    }
}
