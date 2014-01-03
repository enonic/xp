module api.content.site.template.json {

    export interface SiteTemplateSummaryJson extends api.item.ItemJson {

        name:string;

        displayName:string;

        vendor:api.content.site.json.VendorJson;

        modules:string[];

        supportedContentTypes:string[];

        rootContentType:string;

        editable:boolean;

        deletable:boolean;

        version:string;

        url:string;

        key:string;

        description:string;
    }
}