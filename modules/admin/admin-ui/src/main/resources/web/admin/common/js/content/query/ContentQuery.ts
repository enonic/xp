module api.content.query {

    export class ContentQuery implements api.Equitable {

        static POSTLOAD_SIZE: number = 10;

        static DEFAULT_SIZE: number = 100;

        private queryExpr: api.query.expr.QueryExpr;

        private contentTypeNames: api.schema.content.ContentTypeName[] = [];

        private mustBeReferencedById: api.content.ContentId;

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

        setMustBeReferencedById(id: api.content.ContentId): ContentQuery {
            this.mustBeReferencedById = id;
            return this;
        }

        getMustBeReferencedById(): api.content.ContentId {
            return this.mustBeReferencedById;
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

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentQuery)) {
                return false;
            }

            let other = <ContentQuery>o;

            if (!api.ObjectHelper.numberEquals(this.from, other.from)) {
                return false;
            }

            if (!api.ObjectHelper.numberEquals(this.size, other.size)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.contentTypeNames, other.contentTypeNames)) {
                return false;
            }

            if (!api.ObjectHelper.anyArrayEquals(this.aggregationQueries, other.aggregationQueries)) {
                return false;
            }

            if (!api.ObjectHelper.anyArrayEquals(this.queryFilters, other.queryFilters)) {
                return false;
            }

            if ((!this.queryExpr && other.queryExpr) ||
                (this.queryExpr && !other.queryExpr) ||
                (this.queryExpr && other.queryExpr &&
                 !api.ObjectHelper.stringEquals(this.queryExpr.toString(), other.queryExpr.toString()))) {
                return false;
            }

            return true;
        }
    }
}
