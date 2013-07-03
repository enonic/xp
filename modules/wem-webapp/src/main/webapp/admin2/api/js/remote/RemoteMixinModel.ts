module api_remote {

    export interface Mixin {
        name:string;
        module:string;
        displayName:string;
        FormItemSet?: FormItemSet;
        Layout?: Layout;
        Input?: Input;
        MixinReference?: MixinReference;
        iconUrl:string;
    }

    export interface RemoteCallMixinGetParams {
        format:string;
        mixin:string;
    }

    export interface RemoteCallMixinGetResult extends RemoteCallResultBase {
        mixin?: Mixin;
        mixinXml:string;
        iconUrl:string;
    }

    export interface RemoteCallMixinDeleteParams {
        qualifiedMixinNames:string[];
    }

    export interface RemoteCallMixinDeleteResult extends RemoteCallResultBase {
        successes: {
            qualifiedMixinName:string;
        }[];
        failures: {
            qualifiedMixinName:string;
            reason:string;
        }[];
    }

    export interface RemoteCallMixinCreateOrUpdateParams {
        mixin:string;
        iconReference:string;
    }

    export interface RemoteCallMixinCreateOrUpdateResult extends RemoteCallResultBase {
        created:bool;
        updated:bool;
    }

}