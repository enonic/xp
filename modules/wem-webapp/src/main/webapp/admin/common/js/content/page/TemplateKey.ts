module api_content_page{

    export class TemplateKey {

        public static SEPARATOR:string = "|";

        private siteTemplateKey:api_content_site_template.SiteTemplateKey;

        private moduleKey:api_module.ModuleKey;

        private templateName:TemplateName;

        private refString:string;

        constructor(siteTemplateKey:api_content_site_template.SiteTemplateKey, moduleKey:api_module.ModuleKey, templateName:TemplateName) {
            if( name == null ) {
                throw new Error("name cannot be null");
            }
            this.siteTemplateKey = siteTemplateKey;
            this.moduleKey = moduleKey;
            this.templateName = templateName;
            this.refString = siteTemplateKey.toString() + TemplateKey.SEPARATOR + moduleKey + TemplateKey.SEPARATOR +  templateName;
        }

        getSiteTemplateKey():api_content_site_template.SiteTemplateKey {
            return this.siteTemplateKey;
        }

        getModuleKey():api_module.ModuleKey {
            return this.moduleKey;
        }

        getTemplateName():TemplateName {
            return this.templateName;
        }

        public toString():string {
            return this.refString;
        }
    }
}