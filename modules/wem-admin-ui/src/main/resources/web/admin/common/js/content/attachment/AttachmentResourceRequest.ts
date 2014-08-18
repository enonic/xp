module api.content.attachment {

    export class AttachmentResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "attachment");
        }

        getResourcePath(): api.rest.Path {
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
                setBlobKey(new api.blob.BlobKey(json.blobKey)).
                setAttachmentName(new AttachmentName(json.attachmentName)).
                setSize(json.size).
                setMimeType(json.mimeType).
                build();
        }
    }
}