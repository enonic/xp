module api.schema.content {

    export interface ContentTypeJson extends ContentTypeSummaryJson {

        form: api.form.json.FormJson;
    }
}