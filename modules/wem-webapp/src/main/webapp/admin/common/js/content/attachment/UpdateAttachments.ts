module api.content {

    export class UpdateAttachments {

        private attachments: api.content.attachment.Attachments;

        private contentId: api.content.ContentId;

        constructor(contentId: api.content.ContentId, attachments: api.content.attachment.Attachments) {
            this.contentId = contentId;
            this.attachments = attachments;
        }

        static create(contentId: api.content.ContentId, attachmentsArray: api.content.attachment.Attachment[]): UpdateAttachments {
            var attachments: api.content.attachment.Attachments = new api.content.attachment.AttachmentsBuilder().
                addAll(attachmentsArray).
                build();

            return new UpdateAttachments(contentId, attachments);
        }

        toJson(): api.content.attachment.UpdateAttachmentsJson {

            var attachments: api.content.attachment.AttachmentJson[] = [];
            this.attachments.getAttachments().forEach((attachment: api.content.attachment.Attachment)=> {
                attachments.push(attachment.toJson());
            });

            return {
                "contentId": this.contentId.toString(),
                "attachments": attachments
            }
        }
    }
}