module api.content.page.part {

    export class PartTemplateKey extends api.content.page.TemplateKey {

        constructor(moduleName: string, templateName: PartTemplateName) {
            super(moduleName, templateName);
        }

        getPartTemplateName(): PartTemplateName {
            return <PartTemplateName>this.getTemplateName();
        }

        public static fromString(str: string): PartTemplateKey {

            var elements: string[] = str.split(api.content.page.TemplateKey.SEPARATOR);
            var moduleName = elements[0];
            var templateName = new PartTemplateName(elements[1]);
            return new PartTemplateKey(moduleName, templateName);
        }
    }
}