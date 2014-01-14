module api.content.query {

    export class ContentQuery {

        public static DEFAULT_FETCH_SIZE = 10;

        private queryExpr:api.query.expr.QueryExpr;

        private contentTypeNames:api.schema.content.ContentTypeName[];

        private from:number;

        private size:number;

        constructor( builder:ContentQueryBuilder )
        {
            this.queryExpr = builder.queryExpr;
            this.contentTypeNames = builder.contentTypeNames;
            this.from = builder.from;
            this.size = builder.size;
        }

        public static newContentQuery():ContentQueryBuilder
        {
            return new ContentQueryBuilder();
        }

        getQueryExpr():api.query.expr.QueryExpr
        {
            return this.queryExpr;
        }

        getContentTypes():api.schema.content.ContentTypeName[]
        {
            return this.contentTypeNames;
        }

        getFrom():number
        {
            return this.from;
        }

        getSize():number
        {
            return this.size;
        }
    }

    export class ContentQueryBuilder {

        queryExpr:api.query.expr.QueryExpr;

        contentTypeNames:api.schema.content.ContentTypeName[] = [];

        from:number = 0;

        size:number = ContentQuery.DEFAULT_FETCH_SIZE;

        public setQueryExpr( queryExpr:api.query.expr.QueryExpr ):ContentQueryBuilder
        {
            this.queryExpr = queryExpr;
            return this;
        }

        public setContentTypeNames( contentTypeNamesFilter:api.schema.content.ContentTypeName[] ):ContentQueryBuilder
        {
            this.contentTypeNames = contentTypeNamesFilter;
            return this;
        }

        public addContentTypeName( contentTypeName:api.schema.content.ContentTypeName ):ContentQueryBuilder
        {
            this.contentTypeNames.push( contentTypeName );
            return this;
        }

        public setFrom( from:number ):ContentQueryBuilder
        {
            this.from = from;
            return this;
        }

        public setSize( size:number ):ContentQueryBuilder
        {
            this.size = size;
            return this;
        }

        public build(): ContentQuery {
            return new ContentQuery(this);
        }
    }
}