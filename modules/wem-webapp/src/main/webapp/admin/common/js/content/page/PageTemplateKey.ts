module api.content.page {

    export class PageTemplateKey extends api.content.page.TemplateKey {

        constructor(moduleKey: string, templateName: PageTemplateName) {
            super(moduleKey, templateName);
        }

        getPageTemplateName(): PageTemplateName {
            return <PageTemplateName>this.getTemplateName();
        }

        public static fromString(str: string): PageTemplateKey {

            var elements: string[] = str.split(api.content.page.TemplateKey.SEPARATOR);
            var moduleName = elements[0];
            var templateName = new PageTemplateName(elements[1]);
            return new PageTemplateKey(moduleName, templateName);
        }
    }
}