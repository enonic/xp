module api.security.query {

    export class PrincipalQuery {

        private queryExpr: api.query.expr.QueryExpr;

        private principalTypes: api.security.PrincipalType[] = [];

        private aggregationQueries: api.query.aggregation.AggregationQuery[] = [];

        private queryFilters: api.query.filter.Filter[] = [];

        private from: number = 0;

        private size: number = 100;

        setQueryExpr(queryExpr: api.query.expr.QueryExpr) {
            this.queryExpr = queryExpr;
        }

        getQueryExpr(): api.query.expr.QueryExpr {
            return this.queryExpr;
        }

        setContentTypeNames(principalTypes: api.security.PrincipalType[]) {
            this.principalTypes = principalTypes
        }

        getContentTypes(): api.security.PrincipalType[] {
            return this.principalTypes;
        }

        setFrom(from: number) {
            this.from = from;
        }

        getFrom(): number {
            return this.from;
        }

        setSize(size: number) {
            this.size = size;
        }

        getSize(): number {
            return this.size;
        }

        addAggregationQuery(aggregationQuery: api.query.aggregation.AggregationQuery) {
            this.aggregationQueries.push(aggregationQuery);
        }

        getAggregationQueries(): api.query.aggregation.AggregationQuery[] {
            return this.aggregationQueries;
        }

        addQueryFilter(queryFilter: api.query.filter.Filter) {
            this.queryFilters.push(queryFilter);
        }

        getQueryFilters(): api.query.filter.Filter[] {
            return this.queryFilters;
        }

    }
}