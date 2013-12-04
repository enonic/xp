module api_content_page_image_json
{

    export interface ImageTemplateSummaryJson extends api_item.ItemJson
    {
        key: string;
        name: string;
        displayName: string;
        descriptor: string;
        moduleKey: string;
        siteTemplateKey:string;
    }
}
