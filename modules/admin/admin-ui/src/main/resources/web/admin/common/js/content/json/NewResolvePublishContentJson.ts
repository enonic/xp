module api.content.json {

    export interface NewResolvePublishContentJson {

        dependentContents: NewContentPublishItem[];
        requestedContents: NewContentPublishItem[];
    }
}