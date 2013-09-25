module api_schema_mixin_json {

    export class MixinJson {

        name:string;
        displayName:string;
        module:string;
        formItemSet:api_schema_content_form_json.FormItemSetJson;
        layout:api_schema_content_form_json.LayoutJson;
        input:api_schema_content_form_json.InputJson;
        mixinReferenceJson:MixinReferenceJson;
        iconUrl:string;
        deletable:boolean;
        editable:boolean;
    }
}