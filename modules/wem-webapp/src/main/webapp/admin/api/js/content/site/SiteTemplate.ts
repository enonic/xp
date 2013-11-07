module api_content_site{

    export class SiteTemplate {

        private name:string;

        private displayName:string;

        private vendor:Vendor;

        private modules:api_module.ModuleKey[];

        private supportedContentTypes:string[];

        private rootContentType:string;

        constructor( json:api_content_site_json.SiteTemplateJson ) {
            this.name = json.name;
            this.displayName = json.name;
            this.vendor = new Vendor( json.vendor );
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

        getVendor():Vendor {
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
    }
}