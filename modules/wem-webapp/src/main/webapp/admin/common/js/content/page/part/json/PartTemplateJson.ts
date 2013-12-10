module api_content_page_part_json{

    export interface PartTemplateJson extends PartTemplateSummaryJson
    {
        descriptor:PartDescriptorJson;
    }
}