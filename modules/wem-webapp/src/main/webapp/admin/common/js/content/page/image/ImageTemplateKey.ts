module api.content.page.image {

    export class ImageTemplateKey extends api.content.page.TemplateKey {

        constructor(moduleName: string, templateName: ImageTemplateName) {
            super(moduleName, templateName);
        }

        getImageTemplateName(): ImageTemplateName {
            return <ImageTemplateName>this.getTemplateName();
        }

        public static fromString(str: string): ImageTemplateKey {

            var elements: string[] = str.split(api.content.page.TemplateKey.SEPARATOR);
            var moduleName = elements[0];
            var templateName = new ImageTemplateName(elements[1]);
            return new ImageTemplateKey(moduleName, templateName);
        }
    }
}