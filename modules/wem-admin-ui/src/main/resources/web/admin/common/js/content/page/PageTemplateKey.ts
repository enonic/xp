module api.content.page {

    export class PageTemplateKey implements api.Equitable {

        private templateName: PageTemplateName;

        private refString: string;

        public static fromString(str: string): PageTemplateKey {

            var templateName = new PageTemplateName(str);
            return new PageTemplateKey(templateName);
        }

        constructor(templateName: PageTemplateName) {
            if (name == null) {
                throw new Error("name cannot be null");
            }
            this.templateName = templateName;
            this.refString = this.templateName.toString();
        }

        getTemplateName(): PageTemplateName {
            return this.templateName;
        }

        toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PageTemplateKey)) {
                return false;
            }

            var other = <PageTemplateKey>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }
    }
}