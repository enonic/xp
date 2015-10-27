module api.content.json {

    export interface ResolvePublishContentResultJson {

        dependentContents: ContentPublishItemJson[];
        requestedContents: ContentPublishItemJson[];
    }
}