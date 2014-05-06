module api.content {

    export class ContentQueryResult<C extends ContentIdBaseItem,CJ extends json.ContentIdBaseItemJson> {

        private contents: C[];
        private aggregations: api.aggregation.Aggregation[];
        private contentsAsJson: CJ[];

        constructor(contents: C[], aggreations: api.aggregation.Aggregation[], contentsAsJson: CJ[]) {
            this.contents = contents;
            this.aggregations = aggreations;
            this.contentsAsJson = contentsAsJson;
        }

        getContents(): C[] {
            return this.contents;
        }

        getContentsAsJson(): CJ[] {
            return this.contentsAsJson;
        }

        getAggregations(): api.aggregation.Aggregation[] {
            return this.aggregations;
        }
    }
}