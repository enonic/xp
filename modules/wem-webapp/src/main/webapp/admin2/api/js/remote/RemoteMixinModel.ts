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
}