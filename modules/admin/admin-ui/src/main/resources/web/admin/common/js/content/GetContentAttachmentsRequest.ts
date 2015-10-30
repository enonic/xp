module api.content {

    import Attachments = api.content.attachment.Attachments;
    import AttachmentJson = api.content.attachment.AttachmentJson;

    export class GetContentAttachmentsRequest extends ContentResourceRequest<any, any> {

        private contentId: ContentId;

        constructor(contentId: ContentId) {
            super();
            super.setMethod("GET");
            this.contentId = contentId;
        }

        getParams(): Object {
            return {
                id: this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'getAttachments');
        }

        sendAndParse(): wemQ.Promise<any> {
            return this.send().then((response: api.rest.JsonResponse<AttachmentJson[]>) => {
                return response.getResult().length > 0 ? Attachments.create().fromJson(response.getResult()).build() : null;
            });
        }

    }
}