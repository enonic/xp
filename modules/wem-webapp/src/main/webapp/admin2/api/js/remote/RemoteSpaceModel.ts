module api_remote_space {

    export interface Space extends SpaceSummary {
        deletable:bool;
        editable:bool;
    }

    export interface SpaceSummary extends api_remote.Item {
        createdTime:Date;
        displayName:string;
        iconUrl:string;
        modifiedTime:Date;
        name:string;
        rootContentId:string;
    }

    export interface ListParams {
    }

    export interface ListResult extends api_remote.ResultBase {
        total: number;
        spaces: SpaceSummary[];
    }

    export interface GetParams {
        spaceName: string[];
    }

    export interface GetResult extends api_remote.ResultBase {
        space: Space;
    }

    export interface CreateParams extends CreateOrUpdateParams {
        spaceName:string;
        displayName:string;
        iconReference:string;
    }

    export interface UpdateParams extends CreateOrUpdateParams {
        spaceName:string;
        newSpaceName:string;
        displayName:string;
        iconReference:string;
    }

    export interface CreateOrUpdateParams {

    }

    export interface CreateOrUpdateResult extends api_remote.ResultBase {
        created:bool;
        updated:bool;
    }

    export interface DeleteParams {
        spaceName:string[];
    }

    export interface DeleteResult extends api_remote.ResultBase {
        deleted:bool;
        failureReason?:string;
    }
}