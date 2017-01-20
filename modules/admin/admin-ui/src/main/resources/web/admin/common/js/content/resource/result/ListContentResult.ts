module api.content.resource.result {

    export interface ListContentResult<T> {

        contents: T[];

        metadata: ContentMetadata;
    }
}
