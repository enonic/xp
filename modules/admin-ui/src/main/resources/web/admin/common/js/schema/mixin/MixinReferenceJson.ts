module api.schema.mixin {

    export class MixinReferenceJson extends api.form.json.FormItemJson {

        type: string;
        reference: string;
    }
}