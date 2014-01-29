module api.content.page.json {
    export interface TemplateSummaryJson extends api.item.ItemJson {

        key: string;

        displayName:string;

        descriptorKey:string;
    }
}