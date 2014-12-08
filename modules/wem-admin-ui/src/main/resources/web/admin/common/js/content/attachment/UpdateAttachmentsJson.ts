module api.content.attachment {

    export interface UpdateAttachmentsJson {

        contentId: string;

        attachments: api.content.attachment.AttachmentJson[];
    }

}