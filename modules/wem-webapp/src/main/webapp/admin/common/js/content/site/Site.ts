module api_content_site{

    export class Site {

        private templateName:string;

        private moduleConfigs:ModuleConfig[] = [];

        constructor(siteJson:api_content_site_json.SiteJson ){
            this.templateName = siteJson.templateName;

            if(siteJson.moduleConfigs != null) {
                siteJson.moduleConfigs.forEach( (moduleConfigJson:api_content_site_json.ModuleConfigJson) => {
                    this.moduleConfigs.push( new ModuleConfig(moduleConfigJson) );
                });
            }
        }
    }
}