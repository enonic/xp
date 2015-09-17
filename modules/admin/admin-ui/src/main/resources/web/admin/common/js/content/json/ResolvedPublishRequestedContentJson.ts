module api.content.json {

    export interface ResolvedPublishRequestedContentJson extends ResolvedPublishContentJson {

        childrenCount: number;

        dependantsCount: number;

    }

    export interface ResolvedPublishDependencyContentJson extends ResolvedPublishContentJson {

        child: boolean;
    }
}