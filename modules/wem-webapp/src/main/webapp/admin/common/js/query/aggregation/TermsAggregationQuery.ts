module api.query.aggregation {

    export class TermsAggregationQuery extends AggregationQuery {

        public static TERM_DEFAULT_SIZE:number = 10;

        private fieldName:string;

        private size:number;

        constructor( builder:TermsAggregationQueryBuilder )
        {
            super(builder.name);
            this.fieldName = builder.fieldName;
            this.size = builder.size;
        }

        public getFieldName():string
        {
            return this.fieldName;
        }

        public getSize():number
        {
            return this.size;
        }
    }

    export class TermsAggregationQueryBuilder extends AggregationQueryBuilder {

        fieldName:string;

        size:number = TermsAggregationQuery.TERM_DEFAULT_SIZE;

        constructor( name:string ) {
            super( name );
        }

        public setFieldName( fieldName:string ):TermsAggregationQueryBuilder
        {
            this.fieldName = fieldName;
            return this;
        }

        public setSize( size:number ):TermsAggregationQueryBuilder
        {
            this.size = size;
            return this;
        }

        public build():TermsAggregationQuery
        {
            return new TermsAggregationQuery( this );
        }
    }
}
