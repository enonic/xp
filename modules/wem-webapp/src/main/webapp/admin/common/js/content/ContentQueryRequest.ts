module api.content {

    export class ContentQueryRequest<T> extends ContentResourceRequest<ContentQueryResult<T>> {

        private contentQuery: api.content.query.ContentQuery;

        private expand: api.rest.Expand = api.rest.Expand.SUMMARY;

        constructor(contentQuery: api.content.query.ContentQuery) {
            super();
            super.setMethod("POST");
            this.contentQuery = contentQuery;
        }

        setExpand(expand: api.rest.Expand): ContentQueryRequest<T> {
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