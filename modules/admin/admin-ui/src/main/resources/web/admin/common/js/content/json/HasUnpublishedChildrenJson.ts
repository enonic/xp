module api.content.json {

    export interface HasUnpublishedChildrenListJson {
        contents: HasUnpublishedChildrenJson[];
    }

    export interface HasUnpublishedChildrenJson {
        id: ContentIdBaseItemJson;
        hasChildren: boolean;
    }
}
