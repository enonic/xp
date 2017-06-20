import {IssueResourceRequest} from './IssueResourceRequest';
import {IssueJson} from '../json/IssueJson';
import {Issue} from '../Issue';
import {IssueStatus, IssueStatusFormatter} from '../IssueStatus';
import {PublishRequest} from '../PublishRequest';
import PrincipalKey = api.security.PrincipalKey;

export class UpdateIssueRequest extends IssueResourceRequest<IssueJson, Issue> {

    private id: string;

    private title: string;

    private description: string;

    private status: IssueStatus;

    private isPublish: boolean = false;

    private approvers: PrincipalKey[];

    private publishRequest: PublishRequest;

    constructor(id: string) {
        super();
        super.setMethod('POST');
        this.id = id;
    }

    setId(id: string): UpdateIssueRequest {
        this.id = id;
        return this;
    }

    setTitle(title: string): UpdateIssueRequest {
        this.title = title;
        return this;
    }

    setDescription(description: string): UpdateIssueRequest {
        this.description = description;
        return this;
    }

    setStatus(status: IssueStatus): UpdateIssueRequest {
        this.status = status;
        return this;
    }

    setIsPublish(value: boolean): UpdateIssueRequest {
        this.isPublish = value;
        return this;
    }

    setApprovers(approvers: PrincipalKey[]): UpdateIssueRequest {
        this.approvers = approvers;
        return this;
    }

    setPublishRequest(publishRequest: PublishRequest): UpdateIssueRequest {
        this.publishRequest = publishRequest;
        return this;
    }

    getParams(): Object {
        const approvers = this.approvers ? this.approvers.map((el) => el.toString()) : undefined;
        const publishRequest = this.publishRequest ? this.publishRequest.toJson() : undefined;
        return {
            id: this.id,
            title: this.title,
            description: this.description,
            status: IssueStatusFormatter.formatStatus(this.status),
            isPublish: this.isPublish,
            approvers,
            publishRequest,
        };
    }

    getRequestPath(): api.rest.Path {
        return api.rest.Path.fromParent(super.getResourcePath(), 'update');
    }

    sendAndParse(): wemQ.Promise<Issue> {
        return this.send().then((response: api.rest.JsonResponse<IssueJson>) => {
            return Issue.fromJson(response.getResult());
        });
    }
}
