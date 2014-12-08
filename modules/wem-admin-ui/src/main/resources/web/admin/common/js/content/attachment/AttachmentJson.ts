module api.content.attachment {

    export interface AttachmentJson {

        blobKey:string;

        name:string;

        label:string;

        mimeType:string;

        size:number;
    }
}