module api.schema.content.json{

    export interface ContentTypeJson extends ContentTypeSummaryJson {

        form: api.form.json.FormJson;
    }
}