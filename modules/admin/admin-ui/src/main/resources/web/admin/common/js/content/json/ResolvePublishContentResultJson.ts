module api.content.json {

    export interface ResolvePublishContentResultJson {

        dependentContents: ContentIdBaseItemJson[];
        requestedContents: ContentIdBaseItemJson[];
        containsRemovable: boolean;
        allContentsAreValid: boolean;
    }
}