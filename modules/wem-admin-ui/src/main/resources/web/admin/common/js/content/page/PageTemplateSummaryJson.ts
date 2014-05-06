module api.content.page
{
    export interface PageTemplateSummaryJson extends api.item.ItemJson
    {
        key: string;

        displayName:string;

        descriptorKey:string;
    }
}
