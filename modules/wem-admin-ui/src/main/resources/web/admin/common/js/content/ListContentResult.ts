module api.content {

    export interface ListContentResult<T> {

        contents: T[];

        metadata: ContentMetadata;
    }
}