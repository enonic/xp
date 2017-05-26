import {Issue} from '../Issue';
import {IssueJson} from '../json/IssueJson';
import {IssueResourceRequest} from './IssueResourceRequest';
import {IssuesJson} from '../json/IssuesJson';

export class GetIssuesRequest extends IssueResourceRequest<IssuesJson, Issue[]> {

    private ids: string[];

    constructor(ids: string[]) {
        super();
        super.setMethod('POST');

        this.ids = ids;
    }

    getParams(): Object {
        return {ids: this.ids};
    }

    getRequestPath(): api.rest.Path {
        return api.rest.Path.fromParent(super.getResourcePath(), 'getIssues');
    }

    sendAndParse(): wemQ.Promise<Issue[]> {
        return this.send().then((response: api.rest.JsonResponse<IssuesJson>) => {
            return response.getResult().issues.map((issueJson: IssueJson) => {
                return Issue.fromJson(issueJson);
            })
        });
    }
}
