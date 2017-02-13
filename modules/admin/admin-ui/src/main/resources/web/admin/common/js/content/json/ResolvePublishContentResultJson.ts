module api.content.json {

    export interface ResolvePublishContentResultJson {

        dependentContents: ContentIdBaseItemJson[];
        requestedContents: ContentIdBaseItemJson[];
        requiredContents: ContentIdBaseItemJson[];
        containsRemovable: boolean;
        containsInvalid: boolean;
    }
}
