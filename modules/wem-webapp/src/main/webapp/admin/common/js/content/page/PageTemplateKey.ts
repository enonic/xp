module api.content.page {

    export class PageTemplateKey {

        public static SEPARATOR: string = "|";

        private moduleName: string;

        private templateName: PageTemplateName;

        private refString: string;

        public static fromString(str: string): PageTemplateKey {

            var elements: string[] = str.split(api.content.page.PageTemplateKey.SEPARATOR);
            var moduleName = elements[0];
            var templateName = new PageTemplateName(elements[1]);
            return new PageTemplateKey(moduleName, templateName);
        }

        constructor(moduleName: string, templateName: PageTemplateName) {
            if (name == null) {
                throw new Error("name cannot be null");
            }
            this.moduleName = moduleName;
            this.templateName = templateName;
            this.refString = this.moduleName + PageTemplateKey.SEPARATOR + this.templateName;
        }

        getModuleName(): string {
            return this.moduleName;
        }

        getTemplateName(): PageTemplateName {
            return this.templateName;
        }

        toString(): string {
            return this.refString;
        }

        equals(other: PageTemplateKey) {
            return other && (this.refString === other.refString);
        }
    }
}