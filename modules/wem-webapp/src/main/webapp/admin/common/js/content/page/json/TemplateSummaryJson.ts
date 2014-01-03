module api.content.page.json {
    export interface TemplateSummaryJson extends api.item.ItemJson {
        key: string;

        name:string;

        displayName:string;

        descriptorKey:string;
    }
}