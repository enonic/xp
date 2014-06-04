module api.content.site.template {

    export interface SiteTemplateSummaryJson extends api.item.ItemJson {

        key:string;

        name:string;

        displayName:string;

        description:string;

        vendor:api.content.site.VendorJson;

        modules:string[];

        editable:boolean;

        deletable:boolean;

        version:string;

        url:string;

        contentTypeFilter:ContentTypeFilterJson;

        pageTemplateKeys:string[];

        iconUrl: string;
    }
}