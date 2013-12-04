module api_content_page_part{

    export class PartTemplateKey extends api_content_page.TemplateKey{

        constructor(siteTemplateKey:api_content_site_template.SiteTemplateKey, moduleKey:api_module.ModuleKey, templateName:PartTemplateName) {
            super(siteTemplateKey, moduleKey, templateName);
        }

        getPartTemplateName():PartTemplateName {
            return <PartTemplateName>this.getTemplateName();
        }

        public static fromString(str:string):PartTemplateKey {

            var elements:string[] = str.split(api_content_page.TemplateKey.SEPARATOR);
            var siteTemplateKey = api_content_site_template.SiteTemplateKey.fromString(elements[0]);
            var moduleKey = api_module.ModuleKey.fromString(elements[1]);
            var templateName = new PartTemplateName(elements[2]);
            return new PartTemplateKey(siteTemplateKey, moduleKey, templateName);
        }
    }
}