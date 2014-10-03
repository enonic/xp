module api.content.page {

    export class PageTemplateKey extends api.content.ContentId {

        public static fromContentId(id: api.content.ContentId): PageTemplateKey {

            return new PageTemplateKey(id.toString());
        }

        public static fromString(s: string): PageTemplateKey {

            return new PageTemplateKey(s);
        }

        constructor(s: string) {
            super(s);
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PageTemplateKey)) {
                return false;
            }

            var other = <PageTemplateKey>o;
            return super.equals(other);
        }
    }
}