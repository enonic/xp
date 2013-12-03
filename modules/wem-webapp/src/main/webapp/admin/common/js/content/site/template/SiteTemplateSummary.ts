module api_content_site_template {

    export class SiteTemplateSummary {

        private name:string;

        private displayName:string;

        private vendor:api_content_site.Vendor;

        private modules:api_module.ModuleKey[] = [];

        private supportedContentTypes:string[];

        private siteContent:string;

        private editable:boolean;

        private deletable:boolean;

        private version:string;

        private url:string;

        private key:string;

        private description:string;

        constructor( json:api_content_site_template_json.SiteTemplateSummaryJson ) {
            this.name = json.name;
            this.displayName = json.name;
            this.vendor = new api_content_site.Vendor( json.vendor );
            for ( var i = 0; i < json.modules.length; i++ )
            {
                this.modules.push( api_module.ModuleKey.fromString( json.modules[i] ) );
            }
            this.supportedContentTypes = json.supportedContentTypes;
            this.siteContent = json.siteContent;
            this.editable = json.editable;
            this.deletable = json.deletable;
            this.version = json.version;
            this.url = json.url;
            this.key = json.key;
            this.description = json.description;
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

        getSiteContent():string {
            return this.siteContent;
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