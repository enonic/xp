module api.schema.mixin {

    export interface InlineMixinJson extends api.form.json.FormItemJson {

        type: string;
        reference: string;
    }
}
