module api.query.aggregation {

    export class AggregationQuery {

        private name:string;

        constructor( name:string )
        {
            this.name = name;
        }

        public getName():string
        {
            return this.name;
        }

        public static newTermsAggregation( name:string ):TermsAggregationQueryBuilder
        {
            return new TermsAggregationQueryBuilder( name );
        }
    }

    export class AggregationQueryBuilder {

        name:string;

        constructor( name:string ) {
            this.name = name;
        }

        public setName( name:string ):AggregationQueryBuilder
        {
            this.name = name;
            return this;
        }
    }
}
