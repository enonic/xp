module api_schema_mixin_json {

    export interface MixinJson extends api_schema.SchemaJson {

        items:api_form_json.FormItemJson[];

    }
}