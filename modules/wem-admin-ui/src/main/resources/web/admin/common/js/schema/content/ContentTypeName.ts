module api.schema.content {

    export class ContentTypeName implements api.Equitable {

        // Built-in ContentTypes can be listed here

        static SITE = new ContentTypeName('system:site');

        static PAGE_TEMPLATE = new ContentTypeName('system:page-template');

        static IMAGE = new ContentTypeName('system:image');

        private value: string;

        constructor(name: string) {
            this.value = name
        }

        toString(): string {
            return this.value;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentTypeName)) {
                return false;
            }

            var other = <ContentTypeName>o;

            if (!api.ObjectHelper.stringEquals(this.value, other.value)) {
                return false;
            }

            return true;
        }
    }
}