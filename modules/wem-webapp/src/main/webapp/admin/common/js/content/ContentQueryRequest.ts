module api.content {

    export class ContentQueryRequest<CONTENT_JSON extends json.ContentIdBaseItemJson,CONTENT extends ContentIdBaseItem> extends ContentResourceRequest<json.ContentQueryResultJson<CONTENT_JSON>> {

        private contentQuery: api.content.query.ContentQuery;

        private expand: api.rest.Expand = api.rest.Expand.SUMMARY;

        constructor(contentQuery: api.content.query.ContentQuery) {
            super();
            super.setMethod("POST");
            this.contentQuery = contentQuery;
        }

        setExpand(expand: api.rest.Expand): ContentQueryRequest<CONTENT_JSON,CONTENT> {
            this.expand = expand;
            return this;
        }

        getParams(): Object {
            return {
                queryExpr: this.contentQuery.getQueryExpr().toString(),
                from: this.contentQuery.getFrom(),
                size: this.contentQuery.getSize(),
                contentTypeNames: this.contentTypeNamesAsString(this.contentQuery.getContentTypes()),
                expand: this.expandAsString(),
                aggregationQueries: this.aggregationQueriesToJson(this.contentQuery.getAggregationQueries())
            };
        }

        sendAndParse(): Q.Promise<ContentQueryResult<CONTENT>> {

            var deferred = Q.defer<ContentQueryResult<CONTENT>>();

            this.send().
                then((response: api.rest.JsonResponse<json.ContentQueryResultJson<CONTENT_JSON>>) => {

                    var responseResult: json.ContentQueryResultJson<CONTENT_JSON> = response.getResult();

                    var aggregations = api.aggregation.Aggregation.fromJsonArray(responseResult.aggregations);

                    var contentsAsJson: json.ContentIdBaseItemJson[] = responseResult.contents;

                    if (this.expand == api.rest.Expand.NONE) {

                        var contentIdBaseItems: CONTENT[] = <any[]> this.fromJsonToContentIdBaseItemArray(contentsAsJson);
                        var contentQueryResult = new ContentQueryResult<CONTENT>(contentIdBaseItems, aggregations);
                        deferred.resolve(contentQueryResult);
                    }
                    else if (this.expand == api.rest.Expand.SUMMARY) {
                        var contentSummaries: CONTENT[] = <any[]> this.fromJsonToContentSummaryArray(<json.ContentSummaryJson[]>contentsAsJson);
                        var contentQueryResult = new ContentQueryResult<CONTENT>(contentSummaries, aggregations);
                        deferred.resolve(contentQueryResult);
                    }
                    else {
                        var contents: CONTENT[] = <any[]>this.fromJsonToContentArray(<json.ContentJson[]>contentsAsJson);
                        var contentQueryResult = new ContentQueryResult<CONTENT>(contents, aggregations);
                        deferred.resolve(contentQueryResult);
                    }

                }).catch((response: api.rest.RequestError) => {
                    deferred.reject(null);
                }).done();

            return deferred.promise;
        }

        private aggregationQueriesToJson(aggregationQueries: api.query.aggregation.AggregationQuery[]): api.query.aggregation.AggregationQueryTypeWrapperJson[] {
            var aggregationQueryJsons: api.query.aggregation.AggregationQueryTypeWrapperJson[] = [];

            if (aggregationQueries == null) {
                return aggregationQueryJsons;
            }

            aggregationQueries.forEach((aggregationQuery: api.query.aggregation.AggregationQuery) => {
                aggregationQueryJsons.push(aggregationQuery.toJson());
            });

            return aggregationQueryJsons;
        }

        private expandAsString(): string {
            switch (this.expand) {
            case api.rest.Expand.FULL:
                return "full";
            case api.rest.Expand.SUMMARY:
                return "summary";
            case api.rest.Expand.NONE:
                return "none";
            default:
                return "summary";
            }
        }

        contentTypeNamesAsString(names: api.schema.content.ContentTypeName[]): string[] {
            var result: string[] = [];

            names.forEach((name: api.schema.content.ContentTypeName) => {
                result.push(name.toString());
            });

            return result;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "query");
        }
    }
}