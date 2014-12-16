module api.content {

    import BlobKey = api.blob.BlobKey;

    export class CreateImageContentRequest extends CreateContentRequest {

        public static fromAttachment(attachment: api.content.attachment.Attachment, parent: ContentPath): CreateImageContentRequest {

            var request = new CreateImageContentRequest();

            var data = new api.content.image.ImageContentDataFactory().
                setImage(attachment.getName()).
                setMimeType(attachment.getMimeType()).
                create();

            request.setDraft(false).
                setParent(parent).
                setName(ContentName.fromString(ContentName.ensureValidContentName(attachment.getName().toString()))).
                setContentType(api.schema.content.ContentTypeName.IMAGE).
                setDisplayName(attachment.getName().toString()).
                setData(data);//.
            // TODO: Remove? addAttachment(attachment);

            return request;
        }


    }
}