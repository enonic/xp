module api.schema.content {

    import ModuleKey = api.module.ModuleKey;

    export class ContentTypeName extends api.module.ModuleBasedName {

        // Built-in ContentTypes can be listed here
//
        static MEDIA = new ContentTypeName("base:media");

        static MEDIA_TEXT = new ContentTypeName("media:text");

        static MEDIA_DATA = new ContentTypeName("media:data");

        static MEDIA_AUDIO = new ContentTypeName("media:audio");

        static MEDIA_VIDEO = new ContentTypeName("media:video");

        static MEDIA_IMAGE = new ContentTypeName("media:image");

        static MEDIA_VECTOR = new ContentTypeName("media:vector");

        static MEDIA_ARCHIVE = new ContentTypeName("media:archive");

        static MEDIA_DOCUMENT = new ContentTypeName("media:document");

        static MEDIA_SPREADSHEET = new ContentTypeName("media:spreadsheet");

        static MEDIA_PRESENTATION = new ContentTypeName("media:presentation");

        static MEDIA_CODE = new ContentTypeName("media:code");

        static MEDIA_EXECUTABLE = new ContentTypeName("media:executable");

        static MEDIA_UNKNOWN = new ContentTypeName("media:unknown");

        static SITE = new ContentTypeName('portal:site');

        static PAGE_TEMPLATE = new ContentTypeName('portal:page-template');

        static TEMPLATE_FOLDER = new ContentTypeName('portal:template-folder');

        static IMAGE = new ContentTypeName('media:image');

        constructor(name: string) {
            api.util.assertNotNull(name, "Content type name can't be null");
            var parts = name.split(api.module.ModuleBasedName.SEPARATOR);
            super(ModuleKey.fromString(parts[0]), parts[1]);
        }

        isSite(): boolean {
            return ContentTypeName.SITE.equals(this);
        }

        isPageTemplate(): boolean {
            return ContentTypeName.PAGE_TEMPLATE.equals(this);
        }

        isTemplateFolder(): boolean {
            return ContentTypeName.TEMPLATE_FOLDER.equals(this);
        }

        isImage(): boolean {
            return ContentTypeName.IMAGE.equals(this);
        }

        isMedia(): boolean {
            return ContentTypeName.MEDIA.equals(this);
        }

        isDescendantOfMedia(): boolean {
            return ContentTypeName.MEDIA_ARCHIVE.equals(this) ||
                   ContentTypeName.MEDIA_AUDIO.equals(this) ||
                   ContentTypeName.MEDIA_VIDEO.equals(this) ||
                   ContentTypeName.MEDIA_CODE.equals(this) ||
                   ContentTypeName.MEDIA_DATA.equals(this) ||
                   ContentTypeName.MEDIA_DOCUMENT.equals(this) ||
                   ContentTypeName.MEDIA_EXECUTABLE.equals(this) ||
                   ContentTypeName.MEDIA_IMAGE.equals(this) ||
                   ContentTypeName.MEDIA_SPREADSHEET.equals(this) ||
                   ContentTypeName.MEDIA_PRESENTATION.equals(this) ||
                   ContentTypeName.MEDIA_VECTOR.equals(this) ||
                   ContentTypeName.MEDIA_TEXT.equals(this) ||
                   ContentTypeName.MEDIA_UNKNOWN.equals(this);
        }
    }
}