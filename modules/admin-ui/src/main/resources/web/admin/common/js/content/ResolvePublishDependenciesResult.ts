module api.content {

    export interface ResolvePublishDependenciesResult {

        dependantsResolvedWithChildrenIncluded: {id:string;}[];

        dependantsResolvedWithoutChildrenIncluded: {id:string;}[];

        childrenCount: number;
    }
}