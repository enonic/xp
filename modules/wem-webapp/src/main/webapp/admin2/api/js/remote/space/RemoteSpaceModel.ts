module api_remote_space {

    export interface Space extends SpaceSummary {
        deletable:boolean;
        editable:boolean;
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

    export interface ListResult {
        total: number;
        spaces: SpaceSummary[];
    }

    export interface GetParams {
        spaceNames: string[];
    }

    export interface GetResult {
        spaces: Space[];
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

    export interface CreateOrUpdateResult {
        created:boolean;
        updated:boolean;
    }

    export interface DeleteParams {
        spaceName:string[];
    }

    export interface DeleteResult {
        deleted:boolean;
        failureReason?:string;
    }
}