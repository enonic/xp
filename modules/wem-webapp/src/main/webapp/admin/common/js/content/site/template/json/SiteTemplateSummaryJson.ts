module api.content.site.template.json {

    export interface SiteTemplateSummaryJson extends api.item.ItemJson {

        key:string;

        name:string;

        displayName:string;

        description:string;

        vendor:api.content.site.json.VendorJson;

        modules:string[];

        rootContentType:string;

        editable:boolean;

        deletable:boolean;

        version:string;

        url:string;

        contentTypeFilter:ContentTypeFilterJson;

        pageTemplateKeys:string[];

    }
}