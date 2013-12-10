module api_content_site_template_json {

    export interface SiteTemplateSummaryJson extends api_item.ItemJson {

        name:string;

        displayName:string;

        vendor:api_content_site_json.VendorJson;

        modules:string[];

        supportedContentTypes:string[];

        siteContent:string;

        editable:boolean;

        deletable:boolean;

        version:string;

        url:string;

        key:string;

        description:string;
    }
}