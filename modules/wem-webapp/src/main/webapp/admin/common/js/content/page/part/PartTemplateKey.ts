module api.content.page.part{

    export class PartTemplateKey extends api.content.page.TemplateKey{

        constructor(siteTemplateKey:api.content.site.template.SiteTemplateKey, moduleKey:api.module.ModuleKey, templateName:PartTemplateName) {
            super(siteTemplateKey, moduleKey, templateName);
        }

        getPartTemplateName():PartTemplateName {
            return <PartTemplateName>this.getTemplateName();
        }

        public static fromString(str:string):PartTemplateKey {

            var elements:string[] = str.split(api.content.page.TemplateKey.SEPARATOR);
            var siteTemplateKey = api.content.site.template.SiteTemplateKey.fromString(elements[0]);
            var moduleKey = api.module.ModuleKey.fromString(elements[1]);
            var templateName = new PartTemplateName(elements[2]);
            return new PartTemplateKey(siteTemplateKey, moduleKey, templateName);
        }
    }
}