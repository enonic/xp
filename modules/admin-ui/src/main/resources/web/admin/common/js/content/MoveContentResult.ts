module api.content {

    export interface MoveContentResult<T> {

        contents: T[];

        metadata: ContentMetadata;
    }
}