module api.content.page {

    export class TemplateKey {

        public static SEPARATOR: string = "|";

        private moduleName: string;

        private templateName: TemplateName;

        private refString: string;

        public static fromString(str: string): TemplateKey {

            var elements: string[] = str.split(api.content.page.TemplateKey.SEPARATOR);
            var moduleName = elements[0];
            var templateName = new TemplateName(elements[1]);
            return new TemplateKey(moduleName, templateName);
        }

        constructor(moduleName: string, templateName: TemplateName) {
            if (name == null) {
                throw new Error("name cannot be null");
            }
            this.moduleName = moduleName;
            this.templateName = templateName;
            this.refString = this.moduleName + TemplateKey.SEPARATOR + this.templateName;
        }

        getModuleName(): string {
            return this.moduleName;
        }

        getTemplateName(): TemplateName {
            return this.templateName;
        }

        public toString(): string {
            return this.refString;
        }
    }
}