module api_content{

    export class Attachment {

        private uploadId:string;

        private attachmentName:AttachmentName;

        constructor(uploadId:string, attachmentName:AttachmentName)
        {
            this.uploadId = uploadId;
            this.attachmentName = attachmentName;
        }

        getUploadId():string {
            return this.uploadId;
        }

        getAttachmentName():AttachmentName {
            return this.attachmentName;
        }
    }
}
