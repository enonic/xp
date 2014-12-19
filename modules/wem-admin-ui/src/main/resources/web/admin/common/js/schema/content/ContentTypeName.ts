module api.schema.content {

    import ModuleKey = api.module.ModuleKey;

    export class ContentTypeName extends api.module.ModuleBasedName {

        // Built-in ContentTypes can be listed here

        static MEDIA = new ContentTypeName("system:media");

        static MEDIA_TEXT = new ContentTypeName("system:text");

        static MEDIA_DATA = new ContentTypeName("system:data");

        static MEDIA_AUDIO = new ContentTypeName("system:audio");

        static MEDIA_VIDEO = new ContentTypeName("system:video");

        static MEDIA_IMAGE = new ContentTypeName("system:image");

        static MEDIA_VECTOR = new ContentTypeName("system:vector");

        static MEDIA_ARCHIVE = new ContentTypeName("system:archive");

        static MEDIA_DOCUMENT = new ContentTypeName("system:document");

        static MEDIA_SPREADSHEET = new ContentTypeName("system:spreadsheet");

        static MEDIA_PRESENTATION = new ContentTypeName("system:presentation");

        static MEDIA_CODE = new ContentTypeName("system:code");

        static MEDIA_EXECUTABLE = new ContentTypeName("system:executable");

        static MEDIA_UNKNOWN = new ContentTypeName("system:unknown");

        static SITE = new ContentTypeName('system:site');

        static PAGE_TEMPLATE = new ContentTypeName('system:page-template');

        static TEMPLATE_FOLDER = new ContentTypeName('system:template-folder');

        static IMAGE = new ContentTypeName('system:image');

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