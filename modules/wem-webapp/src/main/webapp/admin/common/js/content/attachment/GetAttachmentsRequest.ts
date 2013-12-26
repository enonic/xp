module api_content_attachment {

    export class GetAttachmentsRequest extends AttachmentResourceRequest<api_content_attachment.AttachmentListJson> {

        private contentId: api_content.ContentId;


        constructor(id: api_content.ContentId) {
            super();
            super.setMethod("GET");
            this.contentId = id;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString()
            };
        }

        getRequestPath(): api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "all");
        }

        sendAndParse(): JQueryPromise<api_content_attachment.Attachment[]> {

            var deferred = jQuery.Deferred<api_content_attachment.Attachment[]>();

            this.send().done((response: api_rest.JsonResponse<api_content_attachment.AttachmentListJson>) => {
                deferred.resolve(this.fromJsonArrayToAttachments(response.getResult().attachments));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}