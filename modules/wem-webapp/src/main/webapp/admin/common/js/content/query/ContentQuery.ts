module api.content.query {

    export class ContentQuery {

        public static DEFAULT_FETCH_SIZE = 10;

        private queryExpr:api.query.expr.QueryExpr;

        private contentTypeNames:api.schema.content.ContentTypeName[] = [];

        private from:number = 0;

        private size:number = ContentQuery.DEFAULT_FETCH_SIZE;

        constructor( queryExpr:api.query.expr.QueryExpr, contentTypeNames:api.schema.content.ContentTypeName[], from:number, size:number )
        {
            this.queryExpr = queryExpr;
            this.contentTypeNames = contentTypeNames;
            this.from = from;
            this.size = size;
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
}