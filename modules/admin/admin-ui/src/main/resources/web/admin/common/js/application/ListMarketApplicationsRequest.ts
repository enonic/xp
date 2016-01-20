module api.application {

    export class ListMarketApplicationsRequest extends ApplicationResourceRequest<api.application.json.MarketApplicationsListJson, MarketApplication[]> {

        //TODO: replace with value from config or somewhere else
        private version: string = "6.4.0";

        constructor(version?: string) {
            super();
            super.setMethod("POST");
            if (!!version) {
                this.version = version;
            }
        }

        getParams(): Object {
            return {
                version: this.version
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "getMarketApplications");
        }

        sendAndParse(): wemQ.Promise<MarketApplication[]> {

            return this.send().then((response: api.rest.JsonResponse<api.application.json.MarketApplicationsListJson>) => {
                return MarketApplication.fromJsonArray(response.getResult().hits);
            });
        }
    }
}