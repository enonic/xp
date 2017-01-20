module api.content.resource.result {

    export interface BatchContentResult<T> {

        contents: T[];

        metadata: ContentMetadata;
    }
}
