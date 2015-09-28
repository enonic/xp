module api.schema.mixin {

    export interface MixinJson extends api.schema.SchemaJson {

        items:api.form.json.FormItemJson[];

    }
}