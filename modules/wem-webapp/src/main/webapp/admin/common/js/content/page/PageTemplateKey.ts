module api.content.page{

    export class PageTemplateKey extends api.content.page.TemplateKey{

        constructor(siteTemplateKey:api.content.site.template.SiteTemplateKey, moduleKey:api.module.ModuleKey, templateName:PageTemplateName) {
            super(siteTemplateKey, moduleKey, templateName);
        }

        getPageTemplateName():PageTemplateName {
            return <PageTemplateName>this.getTemplateName();
        }

        public static fromString(str:string):PageTemplateKey {

            var elements:string[] = str.split(api.content.page.TemplateKey.SEPARATOR);
            var siteTemplateKey = api.content.site.template.SiteTemplateKey.fromString(elements[0]);
            var moduleKey = api.module.ModuleKey.fromString(elements[1]);
            var templateName = new PageTemplateName(elements[2]);
            return new PageTemplateKey(siteTemplateKey, moduleKey, templateName);
        }
    }
}