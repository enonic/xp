module api.content {

    export class ContentQueryResult<C extends ContentIdBaseItem> {

        private contents: C[];
        private aggregations: api.aggregation.Aggregation[];

        constructor(contents:C[], aggreations:api.aggregation.Aggregation[]) {
            this.contents = contents;
            this.aggregations = aggreations;
        }

        getContents(): C[] {
            return this.contents;
        }

        getAggregations(): api.aggregation.Aggregation[] {
            return this.aggregations;
        }
    }
}