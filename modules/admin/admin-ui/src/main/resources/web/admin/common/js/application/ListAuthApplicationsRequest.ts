module api.application {

    export class ListAuthApplicationsRequest extends ApplicationResourceRequest<ApplicationListResult, Application[]> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams(): Object {
            return {};
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "getIdProviderApplications");
        }

        sendAndParse(): wemQ.Promise<Application[]> {
            return this.send().then((response: api.rest.JsonResponse<ApplicationListResult>) => {
                return Application.fromJsonArray(response.getResult().applications);
            });
        }
    }
}
