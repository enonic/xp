module api.content.json {

    export interface ResolvedPublishRequestedContentJson extends ResolvedPublishDependencyJson {

        childrenCount: number;

        dependantsCount: number;

    }
}