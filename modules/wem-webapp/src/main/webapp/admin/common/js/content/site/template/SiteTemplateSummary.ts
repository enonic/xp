module api_content_site_template {

    export class SiteTemplateSummary {

        private name:string;

        private displayName:string;

        private vendor:api_content_site.Vendor;

        private modules:api_module.ModuleKey[];

        private supportedContentTypes:string[];

        private rootContentType:string;

        constructor( json:api_content_site_template_json.SiteTemplateSummaryJson ) {
            this.name = json.name;
            this.displayName = json.name;
            this.vendor = new api_content_site.Vendor( json.vendor );
            json.modules.forEach( (moduleKey:string) => {
                this.modules.push( api_module.ModuleKey.fromString( moduleKey ) );
            } );
            this.supportedContentTypes = json.supportedContentTypes;
            this.rootContentType = json.rootContentType;
        }

        getName():string {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getVendor():api_content_site.Vendor {
            return this.vendor;
        }

        getModules():api_module.ModuleKey[] {
            return this.modules;
        }

        getSupportedContentTypes():string[] {
            return this.supportedContentTypes;
        }

        getRootContentType():string {
            return this.rootContentType;
        }

        static fromJsonArray(jsonArray:api_content_site_template_json.SiteTemplateSummaryJson[]):SiteTemplateSummary[] {
            var array:SiteTemplateSummary[] = [];

            jsonArray.forEach( (json:api_content_site_template_json.SiteTemplateSummaryJson) => {
                array.push(new SiteTemplateSummary(json));
            } );
            return array;
        }
    }
}