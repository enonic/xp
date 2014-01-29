module api.content.page.layout {

    export class LayoutTemplateKey extends api.content.page.TemplateKey {

        constructor(moduleName: string, templateName: LayoutTemplateName) {
            super(moduleName, templateName);
        }

        getLayoutTemplateName(): LayoutTemplateName {
            return <LayoutTemplateName>this.getTemplateName();
        }

        public static fromString(str: string): LayoutTemplateKey {

            var elements: string[] = str.split(api.content.page.TemplateKey.SEPARATOR);
            var moduleName = elements[0];
            var templateName = new LayoutTemplateName(elements[1]);
            return new LayoutTemplateKey(moduleName, templateName);
        }
    }
}