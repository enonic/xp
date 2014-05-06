module api.content.site.template {

    export class SiteTemplateKey implements api.Equitable {

        private static SEPARATOR: string = "-";

        private name: string;

        private version: string;

        private refString: string;

        public static fromString(str: string): SiteTemplateKey {
            var sepIndex: number = str.lastIndexOf(SiteTemplateKey.SEPARATOR);
            if (sepIndex == -1) {
                throw new Error("SiteTemplateKey must contain separator '" + SiteTemplateKey.SEPARATOR + "':" + str);
            }

            var name = str.substring(0, sepIndex);
            var version = str.substring(sepIndex + 1, str.length);

            return new SiteTemplateKey(name, version);
        }

        constructor(siteTemplateName: string, siteTemplateVersion: string) {
            this.name = siteTemplateName;
            this.version = siteTemplateVersion;
            this.refString = this.name + SiteTemplateKey.SEPARATOR + this.version;
        }

        getName(): string {
            return this.name;
        }

        getVersion(): string {
            return this.version;
        }

        toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, SiteTemplateKey)) {
                return false;
            }

            var other = <SiteTemplateKey>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }

    }
}
