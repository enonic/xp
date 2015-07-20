module api.module {

    export class ListModulesRequest extends ModuleResourceRequest<ModuleListResult, Application[]> {

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

        setSearchQuery(query: string): ListModulesRequest {
            this.searchQuery = query;
            return this;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): wemQ.Promise<Application[]> {

            return this.send().then((response: api.rest.JsonResponse<ModuleListResult>) => {
                return Application.fromJsonArray(response.getResult().modules);
            });
        }
    }
}