module api.query.aggregation {

    export class TermsAggregation extends Aggregation {

        private buckets:Bucket[];

        constructor( builder:TermsAggregationBuilder )
        {
            super( builder );
            this.buckets = builder.buckets;
        }

        public getBuckets():Bucket[]
        {
            return this.buckets;
        }
    }

    export class TermsAggregationBuilder extends AggregationBuilder {

        buckets:Bucket[] = [];

        public setBuckets( buckets:Bucket[] ):TermsAggregationBuilder
        {
            this.buckets = buckets;
            return this;
        }

        public addBucket( bucket:Bucket ):TermsAggregationBuilder
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
