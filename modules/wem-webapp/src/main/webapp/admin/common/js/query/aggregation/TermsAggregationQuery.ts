module api.query.aggregation {

    export class TermsAggregationQuery {

        public static TERM_DEFAULT_SIZE:number = 10;

        private name:string;

        private fieldName:string;

        private size:number;

        constructor( builder:TermsAggregationQueryBuilder )
        {
            this.name = builder.name;
            this.fieldName = builder.fieldName;
            this.size = builder.size;
        }

        public getName():string
        {
            return this.name;
        }

        public getFieldName():string
        {
            return this.fieldName;
        }

        public getSize():number
        {
            return this.size;
        }

        public static newTermsAggregation( name:string ):TermsAggregationQueryBuilder
        {
            return new TermsAggregationQueryBuilder( name );
        }
    }

    export class TermsAggregationQueryBuilder {

        name:string;

        fieldName:string;

        size:number = TermsAggregationQuery.TERM_DEFAULT_SIZE;

        constructor( name:string ) {
            this.name = name;
        }

        public setName( name:string ):TermsAggregationQueryBuilder
        {
            this.name = name;
            return this;
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
