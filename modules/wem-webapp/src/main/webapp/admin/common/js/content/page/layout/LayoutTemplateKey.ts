module api.content.page.layout{

    export class LayoutTemplateKey extends api.content.page.TemplateKey{

        constructor(siteTemplateKey:api.content.site.template.SiteTemplateKey, moduleKey:api.module.ModuleKey, templateName:LayoutTemplateName) {
            super(siteTemplateKey, moduleKey, templateName);
        }

        getLayoutTemplateName():LayoutTemplateName {
            return <LayoutTemplateName>this.getTemplateName();
        }

        public static fromString(str:string):LayoutTemplateKey {

            var elements:string[] = str.split(api.content.page.TemplateKey.SEPARATOR);
            var siteTemplateKey = api.content.site.template.SiteTemplateKey.fromString(elements[0]);
            var moduleKey = api.module.ModuleKey.fromString(elements[1]);
            var templateName = new LayoutTemplateName(elements[2]);
            return new LayoutTemplateKey(siteTemplateKey, moduleKey, templateName);
        }
    }
}