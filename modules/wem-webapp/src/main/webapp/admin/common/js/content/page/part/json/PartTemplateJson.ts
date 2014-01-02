module api.content.page.part.json{

    export interface PartTemplateJson extends PartTemplateSummaryJson
    {
        descriptor:PartDescriptorJson;
    }
}