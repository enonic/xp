module api_content_attachment {

    export interface AttachmentJson {

        blobKey:string;

        attachmentName:string;

        mimeType:string;

        size:number;
    }
}