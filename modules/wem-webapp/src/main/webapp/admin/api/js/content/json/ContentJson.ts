module api_content_json{

    export interface ContentJson  extends ContentSummaryJson {

        data: api_data_json.DataTypeWrapperJson[];

        form: api_form_json.FormJson;
    }
}