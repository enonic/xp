module api_content_page_image{

    export class ImageTemplateKey extends api_content_page.TemplateKey{

        constructor(siteTemplateKey:api_content_site_template.SiteTemplateKey, moduleKey:api_module.ModuleKey, templateName:ImageTemplateName) {
            super(siteTemplateKey, moduleKey, templateName);
        }

        getImageTemplateName():ImageTemplateName {
            return <ImageTemplateName>this.getTemplateName();
        }

        public static fromString(str:string):ImageTemplateKey {

            var elements:string[] = str.split(api_content_page.TemplateKey.SEPARATOR);
            var siteTemplateKey = api_content_site_template.SiteTemplateKey.fromString(elements[0]);
            var moduleKey = api_module.ModuleKey.fromString(elements[1]);
            var templateName = new ImageTemplateName(elements[2]);
            return new ImageTemplateKey(siteTemplateKey, moduleKey, templateName);
        }
    }
}