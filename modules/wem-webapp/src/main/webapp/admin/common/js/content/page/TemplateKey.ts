module api.content.page {

    export class TemplateKey {

        public static SEPARATOR: string = "|";

        private moduleName: string;

        private templateName: TemplateName;

        private refString: string;

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