module api.content {

    export class ContentQueryResult<C extends ContentIdBaseItem,CJ extends json.ContentIdBaseItemJson> {

        private contents: C[];
        private aggregations: api.aggregation.Aggregation[];
        private contentsAsJson: CJ[];
        private metadata: ContentMetadata;

        constructor(contents: C[], aggreations: api.aggregation.Aggregation[], contentsAsJson: CJ[], metadata?: ContentMetadata) {
            this.contents = contents;
            this.aggregations = aggreations;
            this.contentsAsJson = contentsAsJson;
            this.metadata = metadata;
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

        getMetadata(): ContentMetadata {
            return this.metadata;
        }
    }
}