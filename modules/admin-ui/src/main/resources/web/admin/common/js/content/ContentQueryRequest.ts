module api.content {

    export class ContentQueryRequest<CONTENT_JSON extends json.ContentIdBaseItemJson,CONTENT extends ContentIdBaseItem>
    extends ContentResourceRequest<json.ContentQueryResultJson<CONTENT_JSON>, ContentQueryResult<CONTENT,CONTENT_JSON>> {

        private contentQuery: api.content.query.ContentQuery;

        private expand: api.rest.Expand = api.rest.Expand.SUMMARY;

        constructor(contentQuery?: api.content.query.ContentQuery) {
            super();
            super.setMethod("POST");
            this.contentQuery = contentQuery;
        }

        setContentQuery(contentQuery: api.content.query.ContentQuery) {
            this.contentQuery = contentQuery;
        }

        setExpand(expand: api.rest.Expand): ContentQueryRequest<CONTENT_JSON,CONTENT> {
            this.expand = expand;
            return this;
        }

        getParams(): Object {

            var queryExprAsString = this.contentQuery.getQueryExpr() ? this.contentQuery.getQueryExpr().toString() : "";

            return {
                queryExpr: queryExprAsString,
                from: this.contentQuery.getFrom(),
                size: this.contentQuery.getSize(),
                contentTypeNames: this.contentTypeNamesAsString(this.contentQuery.getContentTypes()),
                expand: this.expandAsString(),
                aggregationQueries: this.aggregationQueriesToJson(this.contentQuery.getAggregationQueries()),
                queryFilters: this.queryFiltersToJson(this.contentQuery.getQueryFilters())
            };
        }

        sendAndParse(): wemQ.Promise<ContentQueryResult<CONTENT,CONTENT_JSON>> {

            return this.send().
                then((response: api.rest.JsonResponse<json.ContentQueryResultJson<CONTENT_JSON>>) => {

                    var responseResult: json.ContentQueryResultJson<CONTENT_JSON> = response.getResult();

                    var aggregations = api.aggregation.Aggregation.fromJsonArray(responseResult.aggregations);

                    var contentsAsJson: json.ContentIdBaseItemJson[] = responseResult.contents;

                    var contentQueryResult: ContentQueryResult<CONTENT, CONTENT_JSON>;

                    var metadata = new ContentMetadata(response.getResult().metadata["hits"], response.getResult().metadata["totalHits"]);

                    if (this.expand == api.rest.Expand.NONE) {

                        var contentIdBaseItems: CONTENT[] = <any[]> this.fromJsonToContentIdBaseItemArray(contentsAsJson);
                        contentQueryResult =
                        new ContentQueryResult<CONTENT,CONTENT_JSON>(contentIdBaseItems, aggregations, <CONTENT_JSON[]>contentsAsJson,
                            metadata);
                    }
                    else if (this.expand == api.rest.Expand.SUMMARY) {
                        var contentSummaries: CONTENT[] = <any[]> this.fromJsonToContentSummaryArray(<json.ContentSummaryJson[]>contentsAsJson);
                        contentQueryResult =
                        new ContentQueryResult<CONTENT,CONTENT_JSON>(contentSummaries, aggregations, <CONTENT_JSON[]>contentsAsJson,
                            metadata);
                    }
                    else {
                        var contents: CONTENT[] = <any[]>this.fromJsonToContentArray(<json.ContentJson[]>contentsAsJson);
                        contentQueryResult =
                        new ContentQueryResult<CONTENT,CONTENT_JSON>(contents, aggregations, <CONTENT_JSON[]>contentsAsJson, metadata);
                    }

                    return contentQueryResult;
                });
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


        private queryFiltersToJson(queryFilters: api.query.filter.Filter[]): api.query.filter.FilterTypeWrapperJson[] {

            var queryFilterJsons: api.query.filter.FilterTypeWrapperJson[] = [];

            if (queryFilters == null || queryFilters.length == 0) {
                return queryFilterJsons;
            }

            queryFilters.forEach((queryFilter: api.query.filter.Filter)=> {

                queryFilterJsons.push(queryFilter.toJson());

            });

            return queryFilterJsons;
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