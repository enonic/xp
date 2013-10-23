module api_schema_content_json{

    export interface ContentTypeJson extends ContentTypeSummaryJson {

        form: api_form_json.FormItemJson[];

    }
}