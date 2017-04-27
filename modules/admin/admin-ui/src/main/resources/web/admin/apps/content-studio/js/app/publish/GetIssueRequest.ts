import "../../api.ts";
import {IssueJson} from "./IssueJson";
import {Issue} from "./Issue";
import IssueResourceRequest = api.issue.resource.IssueResourceRequest;

export class GetIssueRequest extends IssueResourceRequest<IssueJson, Issue> {

    private id: string;

    constructor(id: string) {
        super();
        super.setMethod('GET');

        this.id = id;
    }

    getParams(): Object {
        return {id: this.id};
    }

    getRequestPath(): api.rest.Path {
        return api.rest.Path.fromParent(super.getResourcePath(), 'id');
    }

    sendAndParse(): wemQ.Promise<Issue> {
        return this.send().then((response: api.rest.JsonResponse<IssueJson>) => {
            return Issue.fromJson(response.getResult());
        });
    }
}
