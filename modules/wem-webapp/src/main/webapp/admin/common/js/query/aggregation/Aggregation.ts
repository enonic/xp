module api.query.aggregation {

    export class Aggregation {

        private name:string;

        constructor( builder:AggregationBuilder )
        {
            this.name = builder.name;
        }

        public getName():string
        {
            return this.name;
        }

        public static terms():TermsAggregationBuilder
        {
            return new TermsAggregationBuilder();
        }
    }

    export class AggregationBuilder {

        name:string;

        public setName( name:string ):AggregationBuilder
        {
            this.name = name;
            return this;
        }
    }
}
