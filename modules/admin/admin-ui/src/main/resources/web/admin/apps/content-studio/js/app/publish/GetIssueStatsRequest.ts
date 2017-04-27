import '../../api.ts';
import {IssueStatsJson} from './IssueStatsJson';
import IssueResourceRequest = api.issue.resource.IssueResourceRequest;

export class GetIssueStatsRequest extends IssueResourceRequest<IssueStatsJson, IssueStatsJson> {

    constructor() {
        super();
    }

    getParams(): Object {
        return {};
    }

    getRequestPath(): api.rest.Path {
        return api.rest.Path.fromParent(super.getResourcePath(), 'stats');
    }

    sendAndParse(): wemQ.Promise<IssueStatsJson> {
        return this.send().then((response: api.rest.JsonResponse<IssueStatsJson>) => {
            return response.getResult();
        });
    }
}
