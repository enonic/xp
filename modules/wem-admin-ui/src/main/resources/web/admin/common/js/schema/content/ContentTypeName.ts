module api.schema.content {

    import ModuleKey = api.module.ModuleKey;

    export class ContentTypeName extends api.schema.ModuleBasedName {

        // Built-in ContentTypes can be listed here

        static SITE = new ContentTypeName('system:site');

        static PAGE_TEMPLATE = new ContentTypeName('system:page-template');

        static IMAGE = new ContentTypeName('system:image');

        constructor(name: string) {
            var parts = name.split(api.schema.ModuleBasedName.SEPARATOR);
            super(ModuleKey.fromString(parts[0]), parts[1]);
        }

        isSite(): boolean {
            return this.equals(ContentTypeName.SITE);
        }

        isPageTemplate(): boolean {
            return this.equals(ContentTypeName.PAGE_TEMPLATE);
        }

        isImage(): boolean {
            return this.equals(ContentTypeName.IMAGE);
        }
    }
}