module api_schema_mixin_json {

    export interface MixinJson extends api_item.ItemJson {

        name:string;

        displayName:string;

        items:api_form_json.FormItemJson[];

        iconUrl:string;
    }
}