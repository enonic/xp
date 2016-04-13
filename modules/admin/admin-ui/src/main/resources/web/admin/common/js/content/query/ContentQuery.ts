module api.content.query {

    export class ContentQuery{

        static POSTLOAD_SIZE = 10;

        static DEFAULT_SIZE = 100;

        private queryExpr: api.query.expr.QueryExpr;

        private contentTypeNames: api.schema.content.ContentTypeName[] = [];

        private aggregationQueries: api.query.aggregation.AggregationQuery[] = [];

        private queryFilters: api.query.filter.Filter[] = [];

        private from: number = 0;

        private size: number = ContentQuery.DEFAULT_SIZE;

        setQueryExpr(queryExpr: api.query.expr.QueryExpr): ContentQuery {
            this.queryExpr = queryExpr;
            return this;
        }

        getQueryExpr(): api.query.expr.QueryExpr {
            return this.queryExpr;
        }

        setContentTypeNames(contentTypeNames: api.schema.content.ContentTypeName[]): ContentQuery {
            this.contentTypeNames = contentTypeNames;
            return this;
        }

        getContentTypes(): api.schema.content.ContentTypeName[] {
            return this.contentTypeNames;
        }

        setFrom(from: number): ContentQuery {
            this.from = from;
            return this;
        }

        getFrom(): number {
            return this.from;
        }

        setSize(size: number): ContentQuery {
            this.size = size;
            return this;
        }

        getSize(): number {
            return this.size;
        }

        addAggregationQuery(aggregationQuery: api.query.aggregation.AggregationQuery): ContentQuery {
            this.aggregationQueries.push(aggregationQuery);
            return this;
        }

        setAggregationQueries(aggregationQueries: api.query.aggregation.AggregationQuery[]): ContentQuery {
            this.aggregationQueries = aggregationQueries;
            return this;
        }

        getAggregationQueries(): api.query.aggregation.AggregationQuery[] {
            return this.aggregationQueries;
        }

        addQueryFilter(queryFilter: api.query.filter.Filter): ContentQuery {
            this.queryFilters.push(queryFilter);
            return this;
        }

        setQueryFilters(queryFilters: api.query.filter.Filter[]): ContentQuery {
            this.queryFilters = queryFilters;
            return this;
        }

        getQueryFilters(): api.query.filter.Filter[] {
            return this.queryFilters;
        }

    }
}