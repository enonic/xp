module api.content.page{

    export class TemplateKey {

        public static SEPARATOR:string = "|";

        private siteTemplateKey:api.content.site.template.SiteTemplateKey;

        private moduleKey:api.module.ModuleKey;

        private templateName:TemplateName;

        private refString:string;

        constructor(siteTemplateKey:api.content.site.template.SiteTemplateKey, moduleKey:api.module.ModuleKey, templateName:TemplateName) {
            if( name == null ) {
                throw new Error("name cannot be null");
            }
            this.siteTemplateKey = siteTemplateKey;
            this.moduleKey = moduleKey;
            this.templateName = templateName;
            this.refString = siteTemplateKey.toString() + TemplateKey.SEPARATOR + moduleKey + TemplateKey.SEPARATOR +  templateName;
        }

        getSiteTemplateKey():api.content.site.template.SiteTemplateKey {
            return this.siteTemplateKey;
        }

        getModuleKey():api.module.ModuleKey {
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