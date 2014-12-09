module api.ui.uploader {

    export class FileUploadProgressEvent {

        private uploadItem: UploadItem;

        constructor(uploadItem: UploadItem) {
            this.uploadItem = uploadItem;
        }

        getUploadItem(): UploadItem {
            return this.uploadItem;
        }
    }
}