module api.schema.content {

    import ModuleKey = api.module.ModuleKey;

    export class ContentTypeName extends api.module.ModuleBasedName {

        // Built-in ContentTypes can be listed here
//
        static UNSTRUCTURED = ContentTypeName.from(ModuleKey.BASE, "unstructured");

        static FOLDER = ContentTypeName.from(ModuleKey.BASE, "folder");

        static SHORTCUT = ContentTypeName.from(ModuleKey.BASE, "shortcut");

        static MEDIA = ContentTypeName.from(ModuleKey.BASE, "media");

        static MEDIA_TEXT = ContentTypeName.from(ModuleKey.MEDIA, "text");

        static MEDIA_DATA = ContentTypeName.from(ModuleKey.MEDIA, "data");

        static MEDIA_AUDIO = ContentTypeName.from(ModuleKey.MEDIA, "audio");

        static MEDIA_VIDEO = ContentTypeName.from(ModuleKey.MEDIA, "video");

        static MEDIA_IMAGE = ContentTypeName.from(ModuleKey.MEDIA, "image");

        static MEDIA_VECTOR = ContentTypeName.from(ModuleKey.MEDIA, "vector");

        static MEDIA_ARCHIVE = ContentTypeName.from(ModuleKey.MEDIA, "archive");

        static MEDIA_DOCUMENT = ContentTypeName.from(ModuleKey.MEDIA, "document");

        static MEDIA_SPREADSHEET = ContentTypeName.from(ModuleKey.MEDIA, "spreadsheet");

        static MEDIA_PRESENTATION = ContentTypeName.from(ModuleKey.MEDIA, "presentation");

        static MEDIA_CODE = ContentTypeName.from(ModuleKey.MEDIA, "code");

        static MEDIA_EXECUTABLE = ContentTypeName.from(ModuleKey.MEDIA, "executable");

        static MEDIA_UNKNOWN = ContentTypeName.from(ModuleKey.MEDIA, "unknown");

        static SITE = ContentTypeName.from(ModuleKey.PORTAL, "site");

        static PAGE_TEMPLATE = ContentTypeName.from(ModuleKey.PORTAL, "page-template");

        static TEMPLATE_FOLDER = ContentTypeName.from(ModuleKey.PORTAL, "template-folder");

        static IMAGE = ContentTypeName.from(ModuleKey.MEDIA, "image");

        constructor(name: string) {
            api.util.assertNotNull(name, "Content type name can't be null");
            var parts = name.split(api.module.ModuleBasedName.SEPARATOR);
            super(ModuleKey.fromString(parts[0]), parts[1]);
        }

        static from(moduleKey: api.module.ModuleKey, localName: string) {
            return new ContentTypeName(moduleKey.toString() + ":" + localName);
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