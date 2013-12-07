module api_content_page_json{

    export interface PageTemplateJson extends PageTemplateSummaryJson {

        descriptor:PageDescriptorJson;

        config: api_data_json.DataTypeWrapperJson[];

        canRender: string[];

    }
}