module api.schema.content {

    import ApplicationKey = api.application.ApplicationKey;

    export class ContentTypeName extends api.application.ApplicationBasedName {

        // Built-in ContentTypes can be listed here
        static UNSTRUCTURED: ContentTypeName = ContentTypeName.from(ApplicationKey.BASE, "unstructured");

        static FOLDER: ContentTypeName = ContentTypeName.from(ApplicationKey.BASE, "folder");

        static SHORTCUT: ContentTypeName = ContentTypeName.from(ApplicationKey.BASE, "shortcut");

        static MEDIA: ContentTypeName = ContentTypeName.from(ApplicationKey.BASE, "media");

        static MEDIA_TEXT: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "text");

        static MEDIA_DATA: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "data");

        static MEDIA_AUDIO: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "audio");

        static MEDIA_VIDEO: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "video");

        static MEDIA_IMAGE: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "image");

        static MEDIA_VECTOR: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "vector");

        static MEDIA_ARCHIVE: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "archive");

        static MEDIA_DOCUMENT: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "document");

        static MEDIA_SPREADSHEET: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "spreadsheet");

        static MEDIA_PRESENTATION: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "presentation");

        static MEDIA_CODE: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "code");

        static MEDIA_EXECUTABLE: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "executable");

        static MEDIA_UNKNOWN: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "unknown");

        static SITE: ContentTypeName = ContentTypeName.from(ApplicationKey.PORTAL, "site");

        static PAGE_TEMPLATE: ContentTypeName = ContentTypeName.from(ApplicationKey.PORTAL, "page-template");

        static TEMPLATE_FOLDER: ContentTypeName = ContentTypeName.from(ApplicationKey.PORTAL, "template-folder");

        static FRAGMENT: ContentTypeName = ContentTypeName.from(ApplicationKey.PORTAL, "fragment");

        static IMAGE: ContentTypeName = ContentTypeName.from(ApplicationKey.MEDIA, "image");

        constructor(name: string) {
            api.util.assertNotNull(name, "Content type name can't be null");
            var parts = name.split(api.application.ApplicationBasedName.SEPARATOR);
            super(ApplicationKey.fromString(parts[0]), parts[1]);
        }

        static from(applicationKey: api.application.ApplicationKey, localName: string) {
            return new ContentTypeName(applicationKey.toString() + ":" + localName);
        }

        isFolder(): boolean {
            return ContentTypeName.FOLDER.equals(this);
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

        isFragment(): boolean {
            return ContentTypeName.FRAGMENT.equals(this);
        }

        isImage(): boolean {
            return ContentTypeName.IMAGE.equals(this);
        }

        isMedia(): boolean {
            return ContentTypeName.MEDIA.equals(this);
        }

        isVectorMedia(): boolean {
            return ContentTypeName.MEDIA_VECTOR.equals(this);
        }

        isShortcut(): boolean {
            return ContentTypeName.SHORTCUT.equals(this);
        }

        static getMediaTypes(): ContentTypeName[] {
            return [
                ContentTypeName.MEDIA_ARCHIVE,
                ContentTypeName.MEDIA_AUDIO,
                ContentTypeName.MEDIA_VIDEO,
                ContentTypeName.MEDIA_CODE,
                ContentTypeName.MEDIA_DATA,
                ContentTypeName.MEDIA_DOCUMENT,
                ContentTypeName.MEDIA_EXECUTABLE,
                ContentTypeName.MEDIA_IMAGE,
                ContentTypeName.MEDIA_SPREADSHEET,
                ContentTypeName.MEDIA_PRESENTATION,
                ContentTypeName.MEDIA_VECTOR,
                ContentTypeName.MEDIA_TEXT,
                ContentTypeName.MEDIA_UNKNOWN
            ];
        }

        isDescendantOfMedia(): boolean {
            return ContentTypeName.getMediaTypes().some((contentTypeName: ContentTypeName) => {
                return contentTypeName.equals(this);
            });
        }
    }
}