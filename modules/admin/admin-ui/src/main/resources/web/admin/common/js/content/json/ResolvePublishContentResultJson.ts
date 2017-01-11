module api.content.json {

    export interface ResolvePublishContentResultJson {

        dependentContents: ContentIdBaseItemJson[];
        requestedContents: ContentIdBaseItemJson[];
        containsRemovable: boolean;
        containsInvalid: boolean;
    }
}
