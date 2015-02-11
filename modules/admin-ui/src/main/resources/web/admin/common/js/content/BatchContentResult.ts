module api.content {

    export interface BatchContentResult<T> {

        contents: T[];

        metadata: ContentMetadata;
    }
}