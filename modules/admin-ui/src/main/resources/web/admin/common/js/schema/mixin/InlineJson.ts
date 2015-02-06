module api.schema.mixin {

    export class InlineJson extends api.form.json.FormItemJson {

        type: string;
        reference: string;
    }
}