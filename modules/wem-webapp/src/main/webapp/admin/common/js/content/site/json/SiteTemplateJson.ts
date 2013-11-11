module api_content_site_json{

    export class SiteTemplateJson {

        name:string;

        displayName:string;

        vendor:VendorJson;

        modules:string[];

        supportedContentTypes:string[];

        rootContentType:string;
    }
}