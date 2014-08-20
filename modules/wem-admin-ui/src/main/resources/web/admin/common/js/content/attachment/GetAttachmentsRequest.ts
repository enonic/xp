module api.content.attachment {

    export class GetAttachmentsRequest extends AttachmentResourceRequest<AttachmentListJson, Attachment[]> {

        private contentId: api.content.ContentId;


        constructor(id: api.content.ContentId) {
            super();
            super.setMethod("GET");
            this.contentId = id;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "all");
        }

        sendAndParse(): wemQ.Promise<Attachment[]> {

            return this.send().then((response: api.rest.JsonResponse<AttachmentListJson>) => {
                return this.fromJsonArrayToAttachments(response.getResult().attachments);
            });
        }
    }
}