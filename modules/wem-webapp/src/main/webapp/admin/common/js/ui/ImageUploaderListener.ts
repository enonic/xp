module api.ui {

    export interface ImageUploaderListener extends api.event.Listener {

        onFileUploaded:(uploadItem:UploadItem) => void;

        onUploadComplete:() => void;

    }
}