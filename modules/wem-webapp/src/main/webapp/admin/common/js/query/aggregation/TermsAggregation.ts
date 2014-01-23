module api.query.aggregation {

    export class TermsAggregation {

        private name:string;
        private buckets:Bucket[];

        constructor( builder:AggregationBuilder )
        {
            this.name = builder.name;
            this.buckets = builder.buckets;
        }

        public getName():string
        {
            return this.name;
        }

        public getBuckets():Bucket[]
        {
            return this.buckets;
        }

        public static terms():AggregationBuilder
        {
            return new AggregationBuilder();
        }
    }

    export class AggregationBuilder {

        name:string;

        buckets:Bucket[] = [];

        public setName( name:string ):AggregationBuilder
        {
            this.name = name;
            return this;
        }

        public setBuckets( buckets:Bucket[] ):AggregationBuilder
        {
            this.buckets = buckets;
            return this;
        }

        public addBucket( bucket:Bucket ):AggregationBuilder
        {
            this.buckets.push( bucket );
            return this;
        }

        public build():TermsAggregation
        {
            return new TermsAggregation( this );
        }
    }
}
