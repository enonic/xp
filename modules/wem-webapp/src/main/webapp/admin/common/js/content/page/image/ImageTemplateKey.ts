module api.content.page.image{

    export class ImageTemplateKey extends api.content.page.TemplateKey{

        constructor(siteTemplateKey:api.content.site.template.SiteTemplateKey, moduleKey:api.module.ModuleKey, templateName:ImageTemplateName) {
            super(siteTemplateKey, moduleKey, templateName);
        }

        getImageTemplateName():ImageTemplateName {
            return <ImageTemplateName>this.getTemplateName();
        }

        public static fromString(str:string):ImageTemplateKey {

            var elements:string[] = str.split(api.content.page.TemplateKey.SEPARATOR);
            var siteTemplateKey = api.content.site.template.SiteTemplateKey.fromString(elements[0]);
            var moduleKey = api.module.ModuleKey.fromString(elements[1]);
            var templateName = new ImageTemplateName(elements[2]);
            return new ImageTemplateKey(siteTemplateKey, moduleKey, templateName);
        }
    }
}