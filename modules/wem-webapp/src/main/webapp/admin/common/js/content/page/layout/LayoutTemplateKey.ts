module api_content_page_layout{

    export class LayoutTemplateKey extends api_content_page.TemplateKey{

        constructor(siteTemplateKey:api_content_site_template.SiteTemplateKey, moduleKey:api_module.ModuleKey, templateName:LayoutTemplateName) {
            super(siteTemplateKey, moduleKey, templateName);
        }

        getLayoutTemplateName():LayoutTemplateName {
            return <LayoutTemplateName>this.getTemplateName();
        }

        public static fromString(str:string):LayoutTemplateKey {

            var elements:string[] = str.split(api_content_page.TemplateKey.SEPARATOR);
            var siteTemplateKey = api_content_site_template.SiteTemplateKey.fromString(elements[0]);
            var moduleKey = api_module.ModuleKey.fromString(elements[1]);
            var templateName = new LayoutTemplateName(elements[2]);
            return new LayoutTemplateKey(siteTemplateKey, moduleKey, templateName);
        }
    }
}