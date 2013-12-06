module api_content_page_json
{
    export interface TemplateSummaryJson  extends api_item.ItemJson
    {
        key: string;

        name:string;

        displayName:string;

        descriptor:string;
    }
}