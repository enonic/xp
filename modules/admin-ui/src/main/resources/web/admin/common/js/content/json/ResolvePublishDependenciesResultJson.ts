module api.content.json {

    export interface ResolvePublishDependenciesResultJson {

        dependantContents: ResolvedPublishDependencyJson[];

        childrenContents: ResolvedPublishDependencyJson[];
    }
}