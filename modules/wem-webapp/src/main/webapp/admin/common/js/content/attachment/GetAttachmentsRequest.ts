module api.content.attachment {

    export class GetAttachmentsRequest extends AttachmentResourceRequest<api.content.attachment.AttachmentListJson> {

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

        sendAndParse(): JQueryPromise<api.content.attachment.Attachment[]> {

            var deferred = jQuery.Deferred<api.content.attachment.Attachment[]>();

            this.send().done((response: api.rest.JsonResponse<api.content.attachment.AttachmentListJson>) => {
                deferred.resolve(this.fromJsonArrayToAttachments(response.getResult().attachments));
            }).fail((response: api.rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}