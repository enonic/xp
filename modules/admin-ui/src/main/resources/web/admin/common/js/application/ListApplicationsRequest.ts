module api.application {

    export class ListApplicationsRequest extends ApplicationResourceRequest<ApplicationListResult, Application[]> {

        private searchQuery: string;

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams(): Object {
            return {
                "query": this.searchQuery
            }
        }

        setSearchQuery(query: string): ListApplicationsRequest {
            this.searchQuery = query;
            return this;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): wemQ.Promise<Application[]> {

            return this.send().then((response: api.rest.JsonResponse<ApplicationListResult>) => {
                return Application.fromJsonArray(response.getResult().applications);
            });
        }
    }
}