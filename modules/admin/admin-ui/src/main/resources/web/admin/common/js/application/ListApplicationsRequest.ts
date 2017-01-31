module api.application {

    export class ListApplicationsRequest extends ApplicationResourceRequest<ApplicationListResult, Application[]> {

        private searchQuery: string;
        private apiName: string;

        constructor(apiName: string = 'list') {
            super();
            super.setMethod('GET');

            this.apiName = apiName;
        }

        getParams(): Object {
            return {
                query: this.searchQuery
            };
        }

        setSearchQuery(query: string): ListApplicationsRequest {
            this.searchQuery = query;
            return this;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), this.apiName);
        }

        sendAndParse(): wemQ.Promise<Application[]> {

            return this.send().then((response: api.rest.JsonResponse<ApplicationListResult>) => {
                return Application.fromJsonArray(response.getResult().applications);
            });
        }
    }
}
