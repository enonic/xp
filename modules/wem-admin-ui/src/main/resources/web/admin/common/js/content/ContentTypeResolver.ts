module api.content {

    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ContentTypeResolver {

        static resolveFromMimeType(mimeType: string): ContentTypeName {

            if (mimeType == "image/jpeg" || mimeType == "image/jpg" || mimeType == "image/png" || mimeType == "image/gif") {
                return ContentTypeName.IMAGE;
            }
            else {
                return null;
            }
        }
    }
}
