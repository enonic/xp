module api_content_site_template_json{

    export class SiteTemplateSummaryJson {

        name:string;

        displayName:string;

        vendor:api_content_site_json.VendorJson;

        modules:string[];

        supportedContentTypes:string[];

        rootContentType:string;
    }
}