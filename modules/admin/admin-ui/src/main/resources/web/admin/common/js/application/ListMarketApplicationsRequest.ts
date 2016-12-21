module api.application {

    import MarketApplicationsListJson = api.application.json.MarketApplicationsListJson;

    export class ListMarketApplicationsRequest extends ApplicationResourceRequest<MarketApplicationsListJson, MarketApplicationResponse> {

        private version: string;
        private start: number = 0;
        private count: number = 10;
        private ids: string[] = [];

        constructor() {
            super();
            this.setMethod("POST");
        }

        setIds(ids: string[]): ListMarketApplicationsRequest {
            this.ids = ids;
            return this;
        }

        setVersion(version: string): ListMarketApplicationsRequest {
            this.version = version;
            return this;
        }

        setStart(start: number): ListMarketApplicationsRequest {
            this.start = start;
            return this;
        }

        setCount(count: number): ListMarketApplicationsRequest {
            this.count = count;
            return this;
        }

        getParams(): Object {
            return {
                ids: this.ids,
                version: this.version,
                start: this.start,
                count: this.count,
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "getMarketApplications");
        }

        sendAndParse(): wemQ.Promise<MarketApplicationResponse> {
            return this.send().then((response: api.rest.JsonResponse<MarketApplicationsListJson>) => {
                let applications = MarketApplication.fromJsonArray(response.getResult().hits);
                let hits = applications.length;
                let totalHits = response.getResult().total;
                return new MarketApplicationResponse(applications, new MarketApplicationMetadata(hits, totalHits));
            });
        }
    }
}