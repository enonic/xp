module api.issue.resource {

    import LocalDateTime = api.util.LocalDateTime;
    import IssueResourceRequest = api.issue.resource.IssueResourceRequest;
    import PrincipalKey = api.security.PrincipalKey;

    export class CreateIssueRequest extends IssueResourceRequest<api.task.TaskIdJson, api.task.TaskId> {

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

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'create');
        }

        sendAndParse(): wemQ.Promise<api.task.TaskId> {
            return this.send().then((response: api.rest.JsonResponse<api.task.TaskIdJson>) => {
                return api.task.TaskId.fromJson(response.getResult());
            });
        }
    }
}
