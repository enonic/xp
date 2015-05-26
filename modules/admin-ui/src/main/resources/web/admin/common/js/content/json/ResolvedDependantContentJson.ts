module api.content.json {

    export interface ResolvedDependantContentJson extends ResolvedPublishDependencyJson {

        dependsOnContentId: string;

    }
}