module api.content.page.json
{
    export interface PageTemplateSummaryJson extends api.item.ItemJson
    {
        key: string;

        displayName:string;

        descriptorKey:string;
    }
}
