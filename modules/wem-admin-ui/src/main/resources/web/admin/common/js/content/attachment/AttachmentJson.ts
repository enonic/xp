module api.content.attachment {

    export interface AttachmentJson {

        blobKey:string;

        attachmentName:string;

        mimeType:string;

        size:number;
    }
}