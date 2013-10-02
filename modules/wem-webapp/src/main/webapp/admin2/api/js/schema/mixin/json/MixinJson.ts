module api_schema_mixin_json {

    export class MixinJson {

        name:string;
        displayName:string;
        module:string;
        items:api_schema_content_form_json.FormItemJson[];
        iconUrl:string;
        deletable:boolean;
        editable:boolean;
    }
}