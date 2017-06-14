import {IssueResponse} from './IssueResponse';
import {ListIssuesResult} from './ListIssuesResult';
import {IssueMetadata} from '../IssueMetadata';
import {IssueResourceRequest} from './IssueResourceRequest';
import {IssueStatus} from '../IssueStatus';
import {IssueWithAssigneesJson} from '../json/IssueWithAssigneesJson';
import {IssueWithAssignees} from '../IssueWithAssignees';

export class ListIssuesRequest extends IssueResourceRequest<ListIssuesResult, IssueResponse> {

    private static DEFAULT_FETCH_SIZE: number = 10;

    private issueStatus: IssueStatus;

    private from: number = 0;

    private size: number = ListIssuesRequest.DEFAULT_FETCH_SIZE;

    private assignedToMe: boolean = false;

    private createdByMe: boolean = false;

    private resolveAssignees: boolean = false;

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

    setResolveAssignees(value: boolean): ListIssuesRequest {
        this.resolveAssignees = value;
        return this;
    }

    getParams(): Object {
        return {
            type: IssueStatus[this.issueStatus],
            from: this.from,
            size: this.size,
            assignedToMe: this.assignedToMe,
            createdByMe: this.createdByMe,
            resolveAssignees: this.resolveAssignees
        };
    }

    getRequestPath(): api.rest.Path {
        return api.rest.Path.fromParent(super.getResourcePath(), 'list');
    }

    sendAndParse(): wemQ.Promise<IssueResponse> {
        return this.send().then((response: api.rest.JsonResponse<ListIssuesResult>) => {
            const issuesWithAssignees: IssueWithAssignees[] = response.getResult().issues.map(
                (issueWithAssigneesJson: IssueWithAssigneesJson) => {
                    return IssueWithAssignees.fromJson(issueWithAssigneesJson);
                });

            issuesWithAssignees.sort((a, b) => {
                return b.getIssue().getModifiedTime().getTime() - a.getIssue().getModifiedTime().getTime();
            });

            const metadata: IssueMetadata = new IssueMetadata(response.getResult().metadata['hits'],
                response.getResult().metadata['totalHits']);

            return new IssueResponse(issuesWithAssignees, metadata);
        });
    }
}
