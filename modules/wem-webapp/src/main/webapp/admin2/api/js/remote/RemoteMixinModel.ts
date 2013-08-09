
module api_remote_mixin {

    export interface Mixin extends api_remote.Item {
        name:string;
        module:string;
        displayName:string;
        FormItemSet?: api_remote_contenttype.FormItemSet;
        Layout?: api_remote_contenttype.Layout;
        Input?: api_remote_contenttype.Input;
        MixinReference?: MixinReference;
        iconUrl:string;
    }

    export interface MixinReference {
        name: string;
        reference: string;
        type: string;
    }

    export interface GetParams {
        format:string;
        mixin:string;
    }

    export interface GetResult extends api_remote.BaseResult {
        mixin?: Mixin;
        mixinXml:string;
        iconUrl:string;
    }

    export interface DeleteParams {
        qualifiedMixinNames:string[];
    }

    export interface DeleteResult extends api_remote.BaseResult {
        successes: {
            qualifiedMixinName:string;
        }[];
        failures: {
            qualifiedMixinName:string;
            reason:string;
        }[];
    }

    export interface CreateOrUpdateParams {
        mixin:string;
        iconReference:string;
    }

    export interface CreateOrUpdateResult extends api_remote.BaseResult {
        created:bool;
        updated:bool;
    }

}