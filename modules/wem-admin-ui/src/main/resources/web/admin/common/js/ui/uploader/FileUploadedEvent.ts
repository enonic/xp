module api.ui.uploader {

    export class FileUploadedEvent {

        private uploadedItem: UploadItem;

        constructor(uploadItem: UploadItem) {
            this.uploadedItem = uploadItem;
        }

        getUploadedItem(): UploadItem {
            return this.uploadedItem;
        }
    }
}