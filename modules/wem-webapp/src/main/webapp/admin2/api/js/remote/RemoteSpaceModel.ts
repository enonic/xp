module api_remote {

    export interface Space {
        createdTime:Date;
        deletable:bool;
        displayName:string;
        editable:bool;
        iconUrl:string;
        modifiedTime:Date;
        name:string;
        rootContentId:string;
    }

    export interface SpaceSummary {
        createdTime:Date;
        displayName:string;
        iconUrl:string;
        modifiedTime:Date;
        name:string;
        rootContentId:string;
    }

    export interface RemoteCallSpaceListParams {
    }

    export interface RemoteCallSpaceListResult extends RemoteCallResultBase {
        total: number;
        spaces: Space[];
    }

    export interface RemoteCallSpaceGetParams {
        spaceName: string[];
    }

    export interface RemoteCallSpaceGetResult extends RemoteCallResultBase {
        space: SpaceSummary;
    }

    export interface RemoteCallSpaceCreateOrUpdateParams {
        spaceName:string;
        displayName:string;
        iconReference:string;
    }

    export interface RemoteCallSpaceCreateOrUpdateResult extends RemoteCallResultBase {
        created:bool;
        updated:bool;
    }

    export interface RemoteCallSpaceDeleteParams {
        spaceName:string[];
    }

    export interface RemoteCallSpaceDeleteResult extends RemoteCallResultBase {
        deleted:bool;
        failureReason?:string;
    }
}