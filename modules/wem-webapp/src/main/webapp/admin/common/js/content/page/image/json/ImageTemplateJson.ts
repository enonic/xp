module api.content.page.image.json
{
    export interface ImageTemplateJson extends ImageTemplateSummaryJson
    {
        image:string;

        descriptor:ImageDescriptorJson;
    }
}
