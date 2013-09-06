module api_content_json{

    export interface ContentJson  extends ContentSummaryJson {

        data: api_data_json.DataJson[];
    }
}