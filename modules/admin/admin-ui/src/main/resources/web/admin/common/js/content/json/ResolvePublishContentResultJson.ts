module api.content.json {

    export interface ResolvePublishContentResultJson {

        requestedContents: RequestedContentJson[];
        dependentContents: ContentIdBaseItemJson[];
        requiredContents: ContentIdBaseItemJson[];
        containsInvalid: boolean;
    }

    export interface RequestedContentJson {
        id: ContentIdBaseItemJson;
        hasChildren: boolean;
    }
}
