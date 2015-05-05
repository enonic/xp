module api.content {

    export interface ResolvePublishDependenciesResult {

        dependantsResolvedWithChildrenIncluded: {id:string; path:string; compareStatus:string}[];

        dependantsResolvedWithoutChildrenIncluded: {id:string; path:string; compareStatus:string}[];

        childrenResolved: {id:string; path:string; compareStatus:string}[];

        deletedDependantsResolvedWithChildrenIncluded: {id:string; path:string; compareStatus:string}[];

        deletedDependantsResolvedWithoutChildrenIncluded: {id:string; path:string; compareStatus:string}[];

        deletedChildrenResolved: {id:string; path:string; compareStatus:string}[];
    }
}