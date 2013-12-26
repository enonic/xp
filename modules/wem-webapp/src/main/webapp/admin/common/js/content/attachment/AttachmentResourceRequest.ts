module api_content_attachment {

    export class AttachmentResourceRequest<T> extends api_rest.ResourceRequest<T> {

        private resourcePath: api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "attachment");
        }

        getResourcePath(): api_rest.Path {
            return this.resourcePath;
        }

        fromJsonArrayToAttachments(jsonArray: AttachmentJson[]): Attachment[] {
            var array: Attachment[] = [];
            jsonArray.forEach((json: AttachmentJson)=> {
                array.push(this.fromJsonToAttachment(json));
            });
            return array;
        }

        fromJsonToAttachment(json: AttachmentJson): Attachment {
            return new AttachmentBuilder().
                setBlobKey(new api_blob.BlobKey(json.blobKey)).
                setAttachmentName(new AttachmentName(json.attachmentName)).
                setSize(json.size).
                setMimeType(json.mimeType).
                build();
        }
    }
}