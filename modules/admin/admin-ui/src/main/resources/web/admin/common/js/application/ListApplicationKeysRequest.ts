module api.application {

    export class ListApplicationKeysRequest extends ApplicationResourceRequest<string[], ApplicationKey[]> {

        private searchQuery: string;
        private apiName: string;

        constructor(apiName: string = "listKeys") {
            super();
            super.setMethod("GET");

            this.apiName = apiName;
        }

        getParams(): Object {
            return {
                query: this.searchQuery
            };
        }

        setSearchQuery(query: string): ListApplicationKeysRequest {
            this.searchQuery = query;
            return this;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), this.apiName);
        }

        sendAndParse(): wemQ.Promise<ApplicationKey[]> {

            return this.send().then((response: api.rest.JsonResponse<string[]>) => {
                return response.getResult().map(application => ApplicationKey.fromString(application));
            });
        }
    }
}
