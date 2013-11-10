module api_form_inputtype_content_image {

    export class ImageSelectorSelectedOption {

        private content:api_content.ContentSummary;

        private uploadId:string;

        private attachmentName:api_content.AttachmentName;

        static fromContent(content:api_content.ContentSummary):ImageSelectorSelectedOption {
            return new ImageSelectorSelectedOption( content, null, null );
        }

        static fromUpload(uploadId:string, attachmentName:api_content.AttachmentName):ImageSelectorSelectedOption {
            return new ImageSelectorSelectedOption( null, uploadId, attachmentName );
        }

        constructor(content:api_content.ContentSummary, uploadId:string, attachmentName:api_content.AttachmentName ){
            this.content = content;
            this.uploadId = uploadId;
            this.attachmentName = attachmentName;
        }

        hasContent():boolean {
            return this.content != null
        }

        getContent():api_content.ContentSummary {
            return this.content;
        }

        getUploadId():string {
            return this.uploadId;
        }

        getAttachmentName():api_content.AttachmentName {
            return this.attachmentName;
        }
    }
}