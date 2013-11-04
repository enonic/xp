module api_ui {

    export interface ImageUploaderListener extends api_event.Listener {

        onFileUploaded:(id:string, name:string, mimeType:string) => void;

        onUploadComplete:() => void;

    }
}