module api_content_page_layout_json
{
    export interface LayoutTemplateSummaryJson extends api_content_page_json.TemplateSummaryJson
    {
        key: string;

        name:string;

        displayName:string;

        descriptor:string;
    }
}