module api_content_page{

    export class PageTemplateKey extends api_content_page.TemplateKey{

        constructor(siteTemplateKey:api_content_site_template.SiteTemplateKey, moduleKey:api_module.ModuleKey, templateName:PageTemplateName) {
            super(siteTemplateKey, moduleKey, templateName);
        }

        getPageTemplateName():PageTemplateName {
            return <PageTemplateName>this.getTemplateName();
        }

        public static fromString(str:string):PageTemplateKey {

            var elements:string[] = str.split(api_content_page.TemplateKey.SEPARATOR);
            var siteTemplateKey = api_content_site_template.SiteTemplateKey.fromString(elements[0]);
            var moduleKey = api_module.ModuleKey.fromString(elements[1]);
            var templateName = new PageTemplateName(elements[2]);
            return new PageTemplateKey(siteTemplateKey, moduleKey, templateName);
        }
    }
}