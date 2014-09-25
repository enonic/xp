module api.content.site.template {

    export class SiteTemplateKey implements api.Equitable {

        private name: string;

        public static fromString(name: string): SiteTemplateKey {
            return new SiteTemplateKey(name);
        }

        constructor(siteTemplateName: string) {
            this.name = siteTemplateName;
        }

        getName(): string {
            return this.name;
        }

        toString(): string {
            return this.name;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, SiteTemplateKey)) {
                return false;
            }

            var other = <SiteTemplateKey>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            return true;
        }

    }
}
