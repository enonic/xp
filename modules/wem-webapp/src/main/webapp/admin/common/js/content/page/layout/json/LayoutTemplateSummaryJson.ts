module api_content_page_layout_json
{
    export interface LayoutTemplateSummaryJson  extends api_item.ItemJson
    {
        key: string;

        name:string;

        displayName:string;

        descriptor:string;
    }
}