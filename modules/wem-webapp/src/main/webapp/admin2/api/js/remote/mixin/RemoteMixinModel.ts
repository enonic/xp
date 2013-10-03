
module api_remote_mixin {

    export interface Mixin extends api_remote.Item {
        name:string;
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
        qualifiedName:string;
    }

    export interface GetResult {
        mixin?: Mixin;
        mixinXml:string;
        iconUrl:string;
    }

    export interface DeleteParams {
        qualifiedMixinNames:string[];
    }

    export interface DeleteResult {
        successes: {
            qualifiedMixinName:string;
        }[];
        failures: {
            qualifiedMixinName:string;
            reason:string;
        }[];
    }

    export interface CreateOrUpdateParams {
        name:string;
        mixin:string;
        iconReference:string;
    }

    export interface CreateOrUpdateResult {
        created:boolean;
        updated:boolean;
    }

}