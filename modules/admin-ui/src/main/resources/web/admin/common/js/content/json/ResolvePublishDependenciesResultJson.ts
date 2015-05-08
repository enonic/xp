module api.content.json {

    export interface ResolvePublishDependenciesResultJson {

        dependantsResolvedWithChildrenIncluded: ResolvedPublishDependencyJson[];

        dependantsResolvedWithoutChildrenIncluded: ResolvedPublishDependencyJson[];

        childrenResolved: ResolvedPublishDependencyJson[];

        pushRequestedContents: ResolvedPublishDependencyJson[];
    }
}