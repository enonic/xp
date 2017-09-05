import {IssueResourceRequest} from './IssueResourceRequest';
import {PublishRequest} from '../PublishRequest';
import {IssueJson} from '../json/IssueJson';
import {Issue} from '../Issue';
import Path = api.rest.Path;
import JsonResponse = api.rest.JsonResponse;
import PrincipalKey = api.security.PrincipalKey;

export class CreateIssueRequest extends IssueResourceRequest<IssueJson, Issue> {

    private title: string;

    private description: string;

    private approvers: PrincipalKey[] = [];

    private publishRequest: PublishRequest;

    constructor() {
        super();
        super.setMethod('POST');
    }

    setTitle(value: string): CreateIssueRequest {
        this.title = value;
        return this;
    }

    setDescription(value: string): CreateIssueRequest {
        this.description = value;
        return this;
    }

    setApprovers(value: PrincipalKey[]): CreateIssueRequest {
        this.approvers = value;
        return this;
    }

    setPublishRequest(value: PublishRequest): CreateIssueRequest {
        this.publishRequest = value;
        return this;
    }

    getParams(): Object {
        return {
            title: this.title ? this.title.toString() : '',
            description: this.description ? this.description.toString() : '',
            approvers: this.approvers.map((el) => {
                return el.toString();
            }),
            publishRequest: this.publishRequest.toJson()
        };
    }

    getRequestPath(): Path {
        return Path.fromParent(super.getResourcePath(), 'create');
    }

    sendAndParse(): wemQ.Promise<Issue> {
        return this.send().then((response: JsonResponse<IssueJson>) => {
            return Issue.fromJson(response.getResult());
        });
    }
}
