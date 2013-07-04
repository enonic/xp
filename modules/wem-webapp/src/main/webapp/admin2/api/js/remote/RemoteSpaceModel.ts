module api_remote {

    export interface Space extends SpaceSummary {

    }

    export interface SpaceSummary extends Item {

        displayName:string;

        name:string;

        iconUrl:string;

        modifiedTime:Date;

        createdTime:Date;

        rootContentId:string;
    }

    export interface RemoteCallSpaceListParams {
    }

    export interface RemoteCallSpaceListResult extends RemoteCallResultBase {
        total: number;
        spaces: SpaceSummary[];
    }

    export interface RemoteCallSpaceGetParams {
        spaceName: string[];
    }

    export interface RemoteCallSpaceGetResult extends RemoteCallResultBase {
        space: Space;
    }

    export interface RemoteCallSpaceCreateParams extends RemoteCallSpaceCreateOrUpdateParams {
        spaceName:string;
        displayName:string;
        iconReference:string;
    }

    export interface RemoteCallSpaceUpdateParams extends RemoteCallSpaceCreateOrUpdateParams {
        spaceName:string;
        newSpaceName:string;
        displayName:string;
        iconReference:string;
    }

    export interface RemoteCallSpaceCreateOrUpdateParams {

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