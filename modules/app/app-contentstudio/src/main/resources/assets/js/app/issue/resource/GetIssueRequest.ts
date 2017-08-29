import {Issue} from '../Issue';
import {IssueJson} from '../json/IssueJson';
import {IssueResourceRequest} from './IssueResourceRequest';

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
