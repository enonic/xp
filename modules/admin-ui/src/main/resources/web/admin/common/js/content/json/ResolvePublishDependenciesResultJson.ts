module api.content.json {

    export interface ResolvePublishDependenciesResultJson {

        dependantsResolved: ResolvedPublishDependencyJson[];

        childrenResolved: ResolvedPublishDependencyJson[];

        pushRequestedContents: ResolvedPublishDependencyJson[];
    }
}